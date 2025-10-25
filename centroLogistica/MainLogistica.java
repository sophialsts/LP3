import java.util.*;
import java.util.concurrent.*;
import java.util.Random;

public class MainLogistica {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Caminhao> caminhoes = new ArrayList<>();
        CentroLogistica centroLogistica = new CentroLogistica();
        Random random = new Random();

        System.out.println("=== GERANDO 25 CAMINHÕES ===");
        for(int i=0; i<25;i++) {
            boolean eComum = random.nextDouble() >= 0.3; 
            
            Caminhao c = new Caminhao(eComum, centroLogistica);
            caminhoes.add(c);
            System.out.println("Gerado: " + c);
        }

        System.out.println("\n=== INICIANDO SIMULAÇÃO ===\n");
        for(Caminhao c:caminhoes) {
            executor.submit(c);
            Thread.sleep(300); 
        }

        executor.shutdown();
        executor.awaitTermination(90, TimeUnit.SECONDS); 

        centroLogistica.imprimirStatus();
        System.out.println("\n=== SIMULAÇÃO CONCLUÍDA ===");
    }
}