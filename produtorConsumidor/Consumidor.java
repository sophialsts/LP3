package produtorConsumidor;

import java.util.concurrent.BlockingDeque;

public final class Consumidor implements Runnable{

    private BlockingDeque<String> deque;

    public Consumidor(BlockingDeque<String> deque){
        this.deque = deque;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            try {
                System.out.println("Consumidor: Aguardando na esteira... (Fila: " + deque.size() + ")");
                String item = deque.take();
                System.out.println("Consumidor: Empacotou: '" + item + "'\n");
                // Simula o tempo de empacotamento (3 segundos)
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("\nConsumidor: Fui interrompido e estou finalizando.");
            }  
        }
    }
    
}
