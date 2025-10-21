import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Trabalhador implements Runnable {

    private final BlockingQueue<String> fila;

    public Trabalhador(BlockingQueue<String> fila) {
        this.fila = fila;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                System.out.println(Thread.currentThread().getName() + " - Tentando pegar uma tarefa da fila...");
                String tarefa = fila.take();
                System.out.println("Tarefa: " + tarefa + " captada por: " + Thread.currentThread().getName());
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Finalizando...");
            }
        }
    }
    
}