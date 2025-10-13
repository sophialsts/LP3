import java.util.concurrent.*;

public class MeuRunnable {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(4); //para os 4 módulos
        ServerInitializer server = new ServerInitializer(latch);

        ExecutorService executor = Executors.newCachedThreadPool();

        // módulos com tempos
        executor.submit(new ModuleLoader("Configuração", 6000, latch));
        executor.submit(new ModuleLoader("Cache", 9000, latch));
        executor.submit(new ModuleLoader("Chaves de Criptografia", 12000, latch));
        executor.submit(new ModuleLoader("Conexão de Log", 4000, latch));

        // inicializa o servidor e aguarda os módulos estarem prontos
        executor.submit(server::startServer);

        executor.shutdown(); //impede novas submissões de tarefas e encerra o executor quando todas as tarefas forem concluídas
    }
}
