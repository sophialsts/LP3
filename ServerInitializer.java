import java.util.concurrent.CountDownLatch;

public class ServerInitializer {

    private final CountDownLatch latch;

    public ServerInitializer(CountDownLatch latch) {
        this.latch = latch;
    }

    // aguarda módulo acabar de carregar
    public void waitForInitialization() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[ERRO] Aguardando inicialização interrompida.");
        }
    }

    // só começa o servidor depois dos módulos estarem todos prontos
    public void startServer() {
        waitForInitialization();
        System.out.println("Servidor Principal Online: Pronto para aceitar conexões (Socket.bind())\n");
        System.out.println("[SISTEMA] Processo de inicialização concluído.\n\n");
    }
}
