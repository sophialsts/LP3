package RMI.TransporteAplicativo.Exercicio_TransporteAplicativo;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ServidorDespachante extends UnicastRemoteObject implements ServicoDespachante {
    
    private final ConcurrentHashMap<String, Motorista> motoristas;
    private final ConcurrentHashMap<String, RequisicaoCorrida> corridas;
    private final ConcurrentHashMap<String, Atribuicao> atribuicoes;
    
    private final ReentrantLock lockMatching;
    
    private final ExecutorService executorMatching;
    private final ScheduledExecutorService executorTimeout;
    
    private static final long TIMEOUT_MATCHING_MS = 3000;  // 3 segundos
    private static final long TIMEOUT_CONFIRMACAO_MS = 2000;  // 2 segundos
    
    public ServidorDespachante() throws RemoteException {
        super();
        this.motoristas = new ConcurrentHashMap<>();
        this.corridas = new ConcurrentHashMap<>();
        this.atribuicoes = new ConcurrentHashMap<>();
        this.lockMatching = new ReentrantLock();
        this.executorMatching = Executors.newFixedThreadPool(10);
        this.executorTimeout = Executors.newScheduledThreadPool(5);
        
        log("Servidor Despachante iniciado");
    }
    
    // =========================================================================
    // MÉTODOS DE MOTORISTA (IMPLEMENTADOS)
    // =========================================================================
    
    @Override
    public boolean registrarMotorista(InfoMotorista info, CallbackMotorista callback) 
            throws RemoteException {
        String motoristaId = info.getMotoristaId();
        
        if (motoristas.containsKey(motoristaId)) {
            log("REGISTRO FALHOU - Motorista já existe: " + motoristaId);
            return false;
        }
        
        Motorista motorista = new Motorista(info, callback);
        motoristas.put(motoristaId, motorista);
        
        log(String.format("REGISTRO - Motorista=%s Nome=%s Posicao=%s", 
            motoristaId, info.getNome(), info.getPosicaoAtual()));
        
        return true;
    }
    
    @Override
    public void atualizarStatus(String motoristaId, StatusMotorista status) 
            throws RemoteException {
        Motorista motorista = motoristas.get(motoristaId);
        
        if (motorista == null) {
            throw new RemoteException("Motorista não encontrado: " + motoristaId);
        }
        
        motorista.setStatus(status);
        log(String.format("STATUS ATUALIZADO - Motorista=%s NovoStatus=%s", 
            motoristaId, status));
    }
    
    @Override
    public boolean aceitarAtribuicao(String atribuicaoId) throws RemoteException {
        Atribuicao atribuicao = atribuicoes.get(atribuicaoId);
        
        if (atribuicao == null) {
            log("ACEITACAO FALHOU - Atribuicao não encontrada: " + atribuicaoId);
            return false;
        }
        
        atribuicao.setConfirmada(true);
        
        RequisicaoCorrida corrida = corridas.get(atribuicao.getCorridaId());
        if (corrida != null) {
            corrida.setStatus(StatusRequisicao.ATRIBUIDA);
        }
        
        log(String.format("CONFIRMADA - Atribuicao=%s Corrida=%s Motorista=%s Tempo=%dms",
            atribuicaoId, atribuicao.getCorridaId(), atribuicao.getMotoristaId(),
            System.currentTimeMillis() - atribuicao.getTimestampAtribuicao()));
        
        return true;
    }
    
    @Override
    public void iniciarCorrida(String atribuicaoId) throws RemoteException {
        Atribuicao atribuicao = atribuicoes.get(atribuicaoId);
        
        if (atribuicao == null) {
            throw new RemoteException("Atribuição não encontrada: " + atribuicaoId);
        }
        
        atribuicao.setIniciada(true);
        
        Motorista motorista = motoristas.get(atribuicao.getMotoristaId());
        if (motorista != null) {
            motorista.setStatus(StatusMotorista.EM_CORRIDA);
        }
        
        log(String.format("INICIADA - Corrida=%s Motorista=%s", 
            atribuicao.getCorridaId(), atribuicao.getMotoristaId()));
    }
    
    @Override
    public void concluirCorrida(String atribuicaoId) throws RemoteException {
        Atribuicao atribuicao = atribuicoes.get(atribuicaoId);
        
        if (atribuicao == null) {
            throw new RemoteException("Atribuição não encontrada: " + atribuicaoId);
        }
        
        atribuicao.setConcluida(true);
        
        Motorista motorista = motoristas.get(atribuicao.getMotoristaId());
        if (motorista != null) {
            motorista.setStatus(StatusMotorista.DISPONIVEL);
            motorista.setAtribuicaoAtual(null);
        }
        
        log(String.format("CONCLUÍDA - Corrida=%s Motorista=%s", 
            atribuicao.getCorridaId(), atribuicao.getMotoristaId()));
    }
    
    // =========================================================================
    // MÉTODOS DE PASSAGEIRO
    // =========================================================================
    
    @Override
    public BilheteCorrida solicitarCorrida(InfoPassageiro passageiro, Localizacao origem,
                                            Localizacao destino, Prioridade prioridade) 
            throws RemoteException {
        
        String corridaId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        RequisicaoCorrida requisicao = new RequisicaoCorrida(corridaId, passageiro, origem, destino, prioridade);
        
        corridas.put(corridaId, requisicao);
        
        log(String.format("REQUISICAO - Corrida=%s Passageiro=%s Prioridade=%s Origem=%s Destino=%s",
            corridaId, passageiro.getNome(), prioridade, origem, destino));
        
        // Iniciar matching assíncrono
        executorMatching.submit(() -> processarMatching(requisicao));
        
        // Agendar timeout de matching
        executorTimeout.schedule(() -> verificarTimeoutMatching(corridaId), 
            TIMEOUT_MATCHING_MS, TimeUnit.MILLISECONDS);
        
        return requisicao.toBilhete();
    }
    
    @Override
    public BilheteCorrida consultarCorrida(String corridaId) throws RemoteException {
        RequisicaoCorrida corrida = corridas.get(corridaId);
        
        if (corrida == null) {
            throw new RemoteException("Corrida não encontrada: " + corridaId);
        }
        
        return corrida.toBilhete();
    }
    
    @Override
    public boolean cancelarCorrida(String corridaId) throws RemoteException {
        RequisicaoCorrida corrida = corridas.get(corridaId);
        
        if (corrida == null) {
            return false;
        }
        
        StatusRequisicao status = corrida.getStatus();
        
        // Só pode cancelar se PENDENTE ou ATRIBUIDA sem início
        if (status != StatusRequisicao.PENDENTE && status != StatusRequisicao.ATRIBUIDA) {
            log("CANCELAMENTO NEGADO - Corrida não está em estado cancelável: " + corridaId);
            return false;
        }
        
        // Se já atribuída, verificar se não foi iniciada
        if (status == StatusRequisicao.ATRIBUIDA) {
            String atribuicaoId = corrida.getAtribuicaoId();
            Atribuicao atribuicao = atribuicoes.get(atribuicaoId);
            
            if (atribuicao != null && atribuicao.isIniciada()) {
                log("CANCELAMENTO NEGADO - Corrida já iniciada: " + corridaId);
                return false;
            }
            
            // Notificar motorista sobre cancelamento
            if (atribuicao != null) {
                Motorista motorista = motoristas.get(atribuicao.getMotoristaId());
                if (motorista != null) {
                    try {
                        motorista.getCallback().aoCancelar(atribuicaoId);
                        motorista.setStatus(StatusMotorista.DISPONIVEL);
                        motorista.setAtribuicaoAtual(null);
                    } catch (RemoteException e) {
                        log("ERRO ao notificar cancelamento ao motorista: " + e.getMessage());
                    }
                }
            }
        }
        
        corrida.setStatus(StatusRequisicao.CANCELADA);
        log("CANCELADA - Corrida=" + corridaId);
        
        return true;
    }
    
    // =========================================================================
    // MÉTODOS PARA OS ALUNOS IMPLEMENTAREM (4 MÉTODOS)
    // =========================================================================
    
    /**
     * MÉTODO 1 - PARA O ALUNO IMPLEMENTAR (2,0 pontos)
     * 
     * Processa o matching de uma requisição de corrida.
     * 
     * REQUISITOS:
     * 1. Usar lockMatching.lock() para garantir exclusão mútua no matching
     * 2. Encontrar o melhor motorista disponível usando encontrarMelhorMotorista()
     * 3. Se encontrar motorista:
     *    - Criar atribuição com UUID
     *    - Marcar motorista como ocupado (setAtribuicaoAtual)
     *    - Atualizar corrida (motoristaAtribuido, atribuicaoId) ?
     *    - Adicionar atribuição no mapa atribuicoes
     *    - Logar "[MATCHING] Corrida=X Motorista=Y Distancia=Z Tentativa=1"
     *    - Chamar notificarMotorista() para enviar callback
     * 4. Se não encontrar motorista, logar e não fazer nada (timeout cuidará)
     * 5. SEMPRE liberar lock no finally
     * 6. Tratar exceções adequadamente
     * 
     * DICA: Use o padrão try-lock-finally
     */
    private void processarMatching(RequisicaoCorrida requisicao) {
        lockMatching.lock();
        try {
            Motorista melhorMotorista = encontrarMelhorMotorista(requisicao);
            
            if(melhorMotorista != null) {
                String corridaId = requisicao.getCorridaId();
                String atribuicaoId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                
                String motoristaId = melhorMotorista.getInfo().getMotoristaId();

                Atribuicao atribuicao = new Atribuicao(atribuicaoId, corridaId, motoristaId);

                melhorMotorista.setAtribuicaoAtual(atribuicaoId);
                // O status do motorista só deve mudar para EM_CORRIDA quando a corrida for iniciada.
                atribuicoes.put(atribuicaoId, atribuicao);
                requisicao.setAtribuicaoId(atribuicaoId);
                requisicao.setMotoristaAtribuido(motoristaId);

                Localizacao localizacaoRequisicao = requisicao.getOrigem();
                Localizacao posicaoMotorista = melhorMotorista.getInfo().getPosicaoAtual();
                double distancia = calcularDistancia(localizacaoRequisicao, posicaoMotorista);

                log(String.format("[MATCHING] Corrida=%s Motorista=%s Distancia=%.4f Tentativa=1", corridaId, motoristaId, distancia));

                notificarMotorista(motoristaId, atribuicaoId);
            }
            else {
                log(String.format("[MATCHING] Nenhum motorista disponível para a corrida %s", requisicao.getCorridaId()));
            }
        } catch(Exception e) {
            log("[ERRO] Falha crítica no processarMatching: " + e.getMessage());
        }
        finally {
           lockMatching.unlock();
        }
    }
    
    /**
     * MÉTODO 2 - PARA O ALUNO IMPLEMENTAR (2,0 pontos)
     * 
     * Encontra o melhor motorista disponível para uma corrida.
     * 
     * CRITÉRIOS (em ordem de prioridade):
     * 1. Prioridade: VIP antes de STANDARD
     * 2. Distância: menor distância euclidiana entre motorista.posicaoAtual e corrida.origem
     * 3. Empate: requisição mais antiga (menor timestamp)
     * 
     * REQUISITOS:
     * 1. Iterar sobre motoristas.values()
     * 2. Filtrar apenas motoristas disponíveis (usar isDisponivel())
     * 3. Calcular distância usando calcularDistancia()
     * 4. Comparar:
     *    - Se prioridades diferentes: VIP tem precedência
     *    - Se mesma prioridade: menor distância vence
     *    - Se mesma distância: timestamp menor vence
     * 5. Retornar o melhor motorista ou null se nenhum disponível
     * 
     * DICA: Mantenha variáveis para melhorMotorista, menorDistancia
     */
    private Motorista encontrarMelhorMotorista(RequisicaoCorrida requisicao) {
        double menorDistancia = Double.MAX_VALUE;
        Localizacao localizacaoRequisicao = requisicao.getOrigem();
        Motorista melhorMotorista = null;
        
        for(Motorista motorista : motoristas.values()) {
            if(motorista.isDisponivel()){
                Localizacao posicaoMotorista = motorista.getInfo().getPosicaoAtual();
                double distancia = calcularDistancia(localizacaoRequisicao, posicaoMotorista);
                
                // Simplificando a lógica de acordo com o seu código original, priorizando apenas a menor distância.
                if(melhorMotorista == null) {
                    melhorMotorista = motorista;
                    menorDistancia = distancia;
                }
                else if(distancia < menorDistancia) {
                    menorDistancia = distancia;
                    melhorMotorista = motorista;
                }
            }
        }
        return melhorMotorista;
    }
    
    /**
     * MÉTODO 3 - PARA O ALUNO IMPLEMENTAR (1,5 pontos)
     * 
     * Notifica motorista sobre atribuição de corrida e aguarda confirmação.
     * 
     * REQUISITOS:
     * 1. Buscar motorista e atribuição pelos IDs
     * 2. Criar objeto AtribuicaoCorrida com dados da requisição
     * 3. Chamar motorista.getCallback().aoAtribuir(atribuicao)
     * 4. Agendar timeout de confirmação usando executorTimeout.schedule():
     *    - Após TIMEOUT_CONFIRMACAO_MS (2 segundos)
     *    - Se não confirmada, chamar tratarFalhaConfirmacao()
     * 5. Tratar RemoteException:
     *    - Logar erro de callback
     *    - Chamar tratarFalhaConfirmacao()
     * 
     * DICA: Use lambda para o timeout: () -> { if (!atribuicao.isConfirmada()) ... }
     */
    private void notificarMotorista(String motoristaId, String atribuicaoId) {
        Atribuicao atribuicao = atribuicoes.get(atribuicaoId);
        if (atribuicao == null) return;

        try {
            Motorista motorista = motoristas.get(motoristaId);
            String corridaId = atribuicao.getCorridaId();
            RequisicaoCorrida requisicao = corridas.get(corridaId);
            InfoPassageiro passageiro = requisicao.getPassageiro();
            Localizacao origem = requisicao.getOrigem();
            Localizacao destino = requisicao.getDestino();
            Prioridade prioridade = requisicao.getPrioridade();
            long timestampAtribuicao = atribuicao.getTimestampAtribuicao();
            // pega todos os dados necessários para criar atribuicaoCorrida

            AtribuicaoCorrida atribuicaoCorrida = new AtribuicaoCorrida(atribuicaoId, corridaId, passageiro, origem, destino, prioridade, timestampAtribuicao);

            // quando pedir para usar função ver o tipo de retorno, nesse caso aqui é void
            motorista.getCallback().aoAtribuir(atribuicaoCorrida); // aqui é usado um método da interface remota de CallBack, isso que pode dar a exceção lá em baixo

            // tava escrito na dica como fazer, os parâmetros de schedule() são sempre o tempo e a medida de tempo (tempo era variável fixa e em ms)
            executorTimeout.schedule(() -> {
                if (!atribuicao.isConfirmada()) {
                    tratarFalhaConfirmacao(atribuicaoId);
                } // aqui é se a atribuição não funcionar no tempo ele trata a falha, mas não dá exceção
            }, TIMEOUT_CONFIRMACAO_MS, TimeUnit.MILLISECONDS);
        }
        catch(RemoteException e) {
            log(String.format("[ERRO] Erro de callback para motorista %s: %s", motoristaId, e.getMessage()));
            tratarFalhaConfirmacao(atribuicaoId);
        }
    }
    
    /**
     * MÉTODO 4 - PARA O ALUNO IMPLEMENTAR (1,5 pontos)
     * 
     * Trata falha de confirmação de motorista e realoca corrida.
     * 
     * REQUISITOS:
     * 1. Buscar atribuição e verificar se já foi confirmada (se sim, retornar)
     * 2. Logar falha: "[FALHA] Corrida=X Motorista=Y Motivo=TimeoutConfirmacao Reatribuindo=true"
     * 3. Liberar motorista:
     *    - setStatus(DISPONIVEL)
     *    - setAtribuicaoAtual(null)
     * 4. Buscar corrida e resetar estado:
     *    - setMotoristaAtribuido(null)
     *    - setAtribuicaoId(null)
     *    - setStatus(PENDENTE)
     * 5. Remover atribuição do mapa
     * 6. Reprocessar matching: executorMatching.submit(() -> processarMatching(corrida))
     * 
     * IMPORTANTE: Este método permite retry automático de matching
     */
    private void tratarFalhaConfirmacao(String atribuicaoId) {
        Atribuicao atribuicao = atribuicoes.get(atribuicaoId);
        
        if (atribuicao != null && atribuicao.isConfirmada()) {
            return;
        }
        
        if (atribuicao != null) {
            log(String.format("[FALHA] Corrida=%s Motorista=%s Motivo=TimeoutConfirmacao Reatribuindo=true", atribuicao.getCorridaId(), atribuicao.getMotoristaId()));
            
            Motorista motorista = motoristas.get(atribuicao.getMotoristaId());
            if (motorista != null) {
                motorista.setStatus(StatusMotorista.DISPONIVEL);
                motorista.setAtribuicaoAtual(null);
            }
            
            RequisicaoCorrida corrida = corridas.get(atribuicao.getCorridaId());
            if (corrida != null) {
                corrida.setMotoristaAtribuido(null);
                corrida.setAtribuicaoId(null);
                corrida.setStatus(StatusRequisicao.PENDENTE);
                atribuicoes.remove(atribuicaoId);
                executorMatching.submit(() -> processarMatching(corrida));
            }
        }
    }
    
    // =========================================================================
    // MÉTODOS AUXILIARES (JÁ IMPLEMENTADOS)
    // =========================================================================
    
    /**
     * Verifica timeout de matching (3 segundos).
     */
    private void verificarTimeoutMatching(String corridaId) {
        RequisicaoCorrida corrida = corridas.get(corridaId);
        
        if (corrida == null) return;
        
        // Se ainda está PENDENTE após 3 segundos, expirar
        if (corrida.getStatus() == StatusRequisicao.PENDENTE) {
            corrida.setStatus(StatusRequisicao.EXPIRADA);
            long tempoTotal = System.currentTimeMillis() - corrida.getTimestamp();
            
            log(String.format("EXPIRADA - Corrida=%s Motivo=TimeoutMatching Tempo=%dms",
                corridaId, tempoTotal));
        }
    }
    
    /**
     * Calcula distância euclidiana entre duas localizações.
     */
    private double calcularDistancia(Localizacao a, Localizacao b) {
        double dx = a.getLatitude() - b.getLatitude();
        double dy = a.getLongitude() - b.getLongitude();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Método auxiliar de log com timestamp.
     */
    private void log(String mensagem) {
        String timestamp = String.format("[%tT]", System.currentTimeMillis());
        System.out.println(timestamp + " " + mensagem);
    }
    
    /**
     * Shutdown gracioso.
     */
    public void shutdown() {
        executorMatching.shutdown();
        executorTimeout.shutdown();
        try {
            executorMatching.awaitTermination(5, TimeUnit.SECONDS);
            executorTimeout.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log("Erro no shutdown: " + e.getMessage());
        }
    }
    
    // =========================================================================
    // MAIN
    // =========================================================================
    
    public static void main(String[] args) {
        try {
            // Criar e exportar servidor
            ServidorDespachante servidor = new ServidorDespachante();
            
            // Criar registry na porta 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Registrar servidor
            registry.rebind("DespachanteCorridas", servidor);
            
            System.out.println("=== SERVIDOR DESPACHANTE INICIADO ===");
            System.out.println("Registry: localhost:1099");
            System.out.println("Serviço: DespachanteCorridas");
            System.out.println("Aguardando conexões...\n");
            
            // Manter servidor rodando
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}