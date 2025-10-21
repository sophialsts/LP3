import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.Random;

public class Enfileirador implements Runnable {

    private final BlockingQueue<String> fila;
    private Random random = new Random();

    public Enfileirador(BlockingQueue<String> fila) {
        this.fila = fila;
    }

    @Override
    public void run() {
        try {
            String novaTarefa = "Job_" + random.nextInt(50);
            System.out.println("Tentando adicionar nova tarefa na fila...");
            this.fila.put(novaTarefa);
            System.out.println("Nova tarefa: " + novaTarefa + " adicionada!\n");
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

}

