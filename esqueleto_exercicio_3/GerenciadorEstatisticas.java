import java.util.concurrent.atomic.*;

class GerenciadorEstatisticas {
    private final AtomicInteger pedidosGerados = new AtomicInteger(0);
    private final AtomicInteger pedidosProcessados = new AtomicInteger(0);
    private final AtomicInteger pedidosRejeitados = new AtomicInteger(0);
    private final long tempoInicio;
    
    public GerenciadorEstatisticas() {
        this.tempoInicio = System.currentTimeMillis();
    }
    
    public void registrarPedidoGerado() {
        pedidosGerados.incrementAndGet();
    }
    
    public void registrarPedidoProcessado() {
        pedidosProcessados.incrementAndGet();
    }
    
    public void registrarPedidoRejeitado() {
        pedidosRejeitados.incrementAndGet();
    }
    
    public void exibirEstatisticas(int tamanhoFila) {
        // TODO: Implementar exibição formatada
        System.out.println("\n=== ESTATÍSTICAS ===");
        System.out.println("Fila: " + tamanhoFila + " pedidos");
        System.out.println("Gerados: " + pedidosGerados.get());
        System.out.println("Processados: " + pedidosProcessados.get());
        System.out.println("Rejeitados: " + pedidosRejeitados.get());
        System.out.println("===================\n");
    }
    
    public void exibirRelatorioFinal() {
        long tempoTotal = System.currentTimeMillis() - tempoInicio;
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          RELATÓRIO FINAL               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Gerados total: " + pedidosGerados.get());
        System.out.println("Processados total: " + pedidosProcessados.get());
        System.out.println("Rejeitados total: " + pedidosRejeitados.get());
        System.out.println("Duração total: " + tempoTotal);
        System.out.println("===================\n");

    }
}