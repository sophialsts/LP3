import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

class Produtor implements Runnable {
    private final BlockingQueue<Pedido> fila;
    private final String fonte; // API, Web, Mobile
    private final GerenciadorEstatisticas stats;
    private final Random random = new Random();
    private static final String[] CLIENTES = {"João", "Maria", "Pedro", "Ana", "Carlos"};
    private static final String[] PRODUTOS = {"Notebook", "Mouse", "Teclado", "Monitor", "Headset"};

    private volatile boolean rodando = true;
    private int pedidosGerados = 0;

    public Produtor(BlockingQueue<Pedido> fila, String fonte, GerenciadorEstatisticas stats) {
        this.fila = fila;
        this.fonte = fonte;
        this.stats = stats;
    }

    public void parar() {
        rodando = false;
    }

        public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            
            while (rodando) {
                ExecutorService executor = Executors.newCachedThreadPool();
                // TODO: Gerar pedido aleatório
                Pedido pedido = gerarPedidoAleatorio();

                // TODO: Adicionar na fila usando put()
                fila.put(pedido);
                pedidosGerados++;
                System.out.println("[" + fonte + "] Gerou pedido: " + pedido);

                // TODO: Registrar nas estatísticas
                this.stats.registrarPedidoGerado();

                // TODO: Simular delay entre pedidos (50-200ms)
                Thread.sleep(random.nextInt(150) + 50);

                System.out.println("Deseja continuar gerando pedidos? (s/n)");
                String resposta = scanner.nextLine();
                if (!resposta.equalsIgnoreCase("s")) {
                    parar();
                }
                executor.shutdown();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("[" + fonte + "] Finalizou. Total de pedidos gerados: " + pedidosGerados);
        }
    }
    
    private Pedido gerarPedidoAleatorio() {

        // TODO: Implementar geração aleatória

        Random gerador = new Random();
        
        String cliente = CLIENTES[gerador.nextInt(CLIENTES.length)];
        String produto = PRODUTOS[gerador.nextInt(PRODUTOS.length)];
        int quantidade = gerador.nextInt(5) + 1;
        PrioridadePedido prioridade = PrioridadePedido.values()[gerador.nextInt(PrioridadePedido.values().length)]; // pega os valores do enum de prioridades e escolhe um aleatoriamente

        return new Pedido(cliente, produto, quantidade, prioridade);
    }
}