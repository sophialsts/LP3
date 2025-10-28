import java.util.Random;
import java.util.concurrent.*;

class Consumidor implements Runnable {
    private final BlockingQueue<Pedido> fila;
    private final GerenciadorEstoque estoque;
    private final GerenciadorEstatisticas stats;
    private final int id;
    private final Random random = new Random();
    
    public Consumidor(int id, BlockingQueue<Pedido> fila, GerenciadorEstoque estoque,
                     GerenciadorEstatisticas stats) {
        this.id = id;
        this.fila = fila;
        this.estoque = estoque;
        this.stats = stats;
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 1. Tenta remover um pedido da fila, esperando até 5 segundos
                Pedido pedido = fila.poll(5, TimeUnit.SECONDS);
                
                // 2. Se o pedido for null, a fila está vazia há 5s. Assume que não há mais trabalho.
                if (pedido == null) {
                    break; // Sai do loop para finalizar a thread
                }
                
                // 3. Se pegou um pedido, processa-o
                processarPedido(pedido);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Consumidor-" + id + "] foi interrompido.");
        }
        System.out.println("[Consumidor-" + id + "] Finalizou.");
    }

    private void processarPedido(Pedido pedido) throws InterruptedException {
        // 1. Tenta reservar o estoque. Esta operação é atômica.
        if (estoque.reservarEstoque(pedido.getProduto(), pedido.getQuantidade())) {
            // 2. Se conseguiu reservar, simula o tempo de processamento
            Thread.sleep(random.nextInt(201) + 100); // 100-300ms
            System.out.println("[Consumidor-" + id + "] Processou: " + pedido);
            stats.registrarPedidoProcessado();
        } else {
            // 3. Se não conseguiu (falta de estoque), registra como rejeitado
            System.out.println("[Consumidor-" + id + "] Rejeitou (sem estoque): " + pedido);
            stats.registrarPedidoRejeitado();
        }
    }
}