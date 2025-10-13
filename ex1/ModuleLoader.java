import java.util.concurrent.CountDownLatch;

public class ModuleLoader implements Runnable {

    private final String nomeModulo;
    private final int tempoCarregamento; // em milissegundos
    private final CountDownLatch latch;

    public ModuleLoader(String nomeModulo, int tempoCarregamento, CountDownLatch latch) {
        this.nomeModulo = nomeModulo;
        this.tempoCarregamento = tempoCarregamento;
        this.latch = latch;
    }

    @Override
    public void run() { // é chamado automaticamente quando o executor roda para criar a thread (padrão Runnable)
        try {
            System.out.println("[CARREGANDO] Módulo " + nomeModulo + " iniciando...");
            Thread.sleep(tempoCarregamento);
            System.out.println("[OK] Módulo " + nomeModulo + " carregado.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[ERRO] Carregamento do módulo " + nomeModulo + " interrompido.");
        } finally {
            latch.countDown(); //garante que o latch será decrementado mesmo em caso de erro
        }
    }
}
