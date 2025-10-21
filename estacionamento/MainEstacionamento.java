import java.util.*;
import java.util.concurrent.*;

public class MainEstacionamento {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Sistema de Estacionamento Inteligente ===");
        System.out.println("\n=== GERANDO VEÍCULOS ===");

        Estacionamento estacionamento = new Estacionamento();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Veiculo> veiculos = new ArrayList<>();
        Random rand = new Random();

        // 20 veículos, 30% prioritários
        for (int i = 1; i <= 20; i++) {
            boolean prioritario = rand.nextDouble() < 0.3;
            Veiculo v = new Veiculo(i, prioritario, estacionamento);
            veiculos.add(v);
            System.out.println("Gerado: " + v);
        }

        System.out.println("\n=== INICIANDO SIMULAÇÃO ===\n");
        for (Veiculo v : veiculos) {
            executor.submit(v); // consigo saber o status do andamento, com future
            Thread.sleep(300); // pequena defasagem de chegada
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        estacionamento.exibirStatus();
        System.out.println("\n=== Simulação Concluída ===");
    }
}
