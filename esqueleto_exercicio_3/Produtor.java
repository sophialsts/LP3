import java.util.Random;
import java.util.concurrent.*;

class Produtor implements Runnable {
    private final BlockingQueue<Pedido> fila;
    private final String fonte;
    private final int quantidadePedidos;
    private final GerenciadorEstatisticas stats;
    private final Random random = new Random();
    
    private static final String[] CLIENTES = {"João", "Maria", "Pedro", "Ana", "Carlos"};
    private static final String[] PRODUTOS = {"Notebook", "Mouse", "Teclado", "Monitor", "Headset"};
    
    public Produtor(BlockingQueue<Pedido> fila, String fonte, int quantidadePedidos, 
                    GerenciadorEstatisticas stats) {
        this.fila = fila;
        this.fonte = fonte;
        this.quantidadePedidos = quantidadePedidos;
        this.stats = stats;
    }
    
    @Override
    public void run() {
        try {
            for (int i = 0; i < this.quantidadePedidos; i++) {
                // 1. Gerar pedido aleatório
                Pedido novoPedido = gerarPedidoAleatorio();
                
                // 2. Adicionar na fila usando put()
                fila.put(novoPedido);
                System.out.println("[" + fonte + "] Gerou: " + novoPedido);
                
                // 3. Registrar nas estatísticas
                stats.registrarPedidoGerado();

                // 4. Simular delay entre pedidos (50-200ms)
                Thread.sleep(random.nextInt(150) + 50);
            }
            System.out.println("[" + fonte + "] Finalizou geração de pedidos");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[" + fonte + "] Foi interrompido durante a geração de pedidos.");
        }
    }
    
    private Pedido gerarPedidoAleatorio() {
        String cliente = CLIENTES[random.nextInt(CLIENTES.length)];
        String produto = PRODUTOS[random.nextInt(PRODUTOS.length)];
        int quantidade = random.nextInt(5) + 1; // Gera de 1 a 5 unidades
        PrioridadePedido prioridade = PrioridadePedido.fromValor(random.nextInt(3) + 1);
        return new Pedido(cliente, produto, quantidade, prioridade);
    }
}