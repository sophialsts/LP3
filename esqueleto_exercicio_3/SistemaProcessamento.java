import java.util.concurrent.*;
import java.util.Random;

public class SistemaProcessamento {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE PROCESSAMENTO DE PEDIDOS  ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        BlockingQueue<Pedido> fila = new LinkedBlockingQueue<Pedido>(50);
        GerenciadorEstoque estoque = new GerenciadorEstoque();
        GerenciadorEstatisticas stats = new GerenciadorEstatisticas();
        Monitor monitor = new Monitor(fila, stats);

        ExecutorService execMonitor = Executors.newFixedThreadPool(1);
        execMonitor.submit(monitor);

        Random random = new Random();
        String[] tiposProdutores = {"Api", "Web", "Mobile"};

        // TODO: Criar ExecutorService para produtores (3 threads)
        ExecutorService produtores = Executors.newFixedThreadPool(3);
        
        // TODO: Criar 3 produtores (API, Web, Mobile) - cada um gera 20 pedidos
        for(int i=0;i<3;i++) {
            int indice = random.nextInt(3);
            String tipo = tiposProdutores[indice];
            produtores.submit(new Produtor(fila, tipo, 20, stats));
        }
        // TODO: Criar ExecutorService para consumidores (5 threads)
        ExecutorService consumidores = Executors.newFixedThreadPool(5);
        
        // TODO: Criar 5 consumidores

        for(int i=0;i<5;i++) {
            int id = random.nextInt(1000);
            produtores.submit(new Consumidor(id, fila, estoque, stats));
        }

        // TODO: Aguardar produtores finalizarem
        produtores.awaitTermination(30,TimeUnit.SECONDS);
        
        // TODO: Aguardar consumidores finalizarem
        consumidores.awaitTermination(30,TimeUnit.SECONDS);

        // TODO: Parar monitor
        monitor.parar();

        // TODO: Exibir relatório final
        stats.exibirRelatorioFinal();
        
        // TODO: Exibir estoque final
        estoque.exibirEstoque();
        
        System.out.println("\nSistema finalizado com sucesso!");
    }
}