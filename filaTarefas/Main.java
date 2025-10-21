import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        BlockingQueue<String> fila = new ArrayBlockingQueue<>(5);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

        Enfileirador enfileirador = new Enfileirador(fila);
        Trabalhador trabalhador1 = new Trabalhador(fila);
        Trabalhador trabalhador2 = new Trabalhador(fila);
        Trabalhador trabalhador3 = new Trabalhador(fila);

        scheduler.scheduleAtFixedRate(enfileirador, 0, 1, TimeUnit.SECONDS);

        scheduler.execute(trabalhador1);
        scheduler.execute(trabalhador2);
        scheduler.execute(trabalhador3);

        try {
            Thread.sleep(20000);
            System.out.println("Encerrando todas as threads.");
            scheduler.shutdownNow();

            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Main: As tarefas não terminaram a tempo!");
            }
        }
        catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }

}