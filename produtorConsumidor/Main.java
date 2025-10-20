package produtorConsumidor;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    
    public static void main(String[] args) {
        
        // 1. Criamos a fila com uma capacidade máxima de 3 itens.
        BlockingDeque<String> deque = new LinkedBlockingDeque<>(3);
        System.out.println("Esteira de produção de brinquedos iniciada (Capacidade: 3).");
    
        Produtor prod = new Produtor(deque);
        Consumidor consu = new Consumidor(deque);
    
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
        // 2. O Produtor vai tentar inserir um item a cada 1 segundo.
        scheduler.scheduleAtFixedRate(prod, 0, 1, TimeUnit.SECONDS);

        // 3. O Consumidor vai começar a trabalhar.
        scheduler.execute(consu);

        try {
            Thread.sleep(15000); // tempo que o sistema vai rodar
            System.out.println("\nMain: 15 segundos se passaram. Finalizando o sistema...");
            scheduler.shutdownNow(); 

            // aguarda até 5 segundos para que as tarefas realmente terminem.
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Main: As tarefas não terminaram a tempo!");
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt(); // Restaura o status de interrupção
        }
    }
}