import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CentroLogistica {

    private final Semaphore docasComuns = new Semaphore(3, true);
    private final Semaphore docasRefrigeradas = new Semaphore(2, true);
    private final Semaphore balancaEntrada = new Semaphore(1, true);
    
    private final AtomicInteger caminhoesOperadosComSucesso = new AtomicInteger(0);
    private final AtomicInteger caminhoesQueDesistiram = new AtomicInteger(0);

    public TipoDoca tentarAlocarDoca(Caminhao c) throws InterruptedException {
        try {
            balancaEntrada.acquire();
            System.out.println(c + " está na balança...");

            if (c.isCargaComum()) {
                if (docasComuns.tryAcquire()) {
                    System.out.println(c + " (Comum) acessou doca COMUM");
                    caminhoesOperadosComSucesso.incrementAndGet();
                    return TipoDoca.COMUM;
                } else {
                    System.out.println(c + " (Comum) DESISTIU (sem doca comum).");
                    caminhoesQueDesistiram.incrementAndGet();
                    return null;
                }
            } else {
                if (docasRefrigeradas.tryAcquire()) {
                    System.out.println(c + " (Refrigerado) acessou doca REFRIGERADA");
                    caminhoesOperadosComSucesso.incrementAndGet();
                    return TipoDoca.REFRIGERADA;
                } else if (docasComuns.tryAcquire()) {
                    System.out.println(c + " (Refrigerado) acessou doca COMUM (Refrigeradas lotadas)");
                    caminhoesOperadosComSucesso.incrementAndGet();
                    return TipoDoca.COMUM;
                } else {
                    System.out.println(c + " (Refrigerado) DESISTIU (sem doca de nenhum tipo).");
                    caminhoesQueDesistiram.incrementAndGet();
                    return null;
                }
            }
        } finally {
            balancaEntrada.release();
            System.out.println(c + " liberou a BALANÇA.");
        }
    }

    public void liberarDoca(Caminhao c) {
        if (c.getDocaAlocada() == TipoDoca.REFRIGERADA) {
            docasRefrigeradas.release();
            System.out.println(c + " liberou doca REFRIGERADA.");
        } else if (c.getDocaAlocada() == TipoDoca.COMUM) {
            docasComuns.release();
            System.out.println(c + " liberou doca COMUM.");
        }
    }

    public void imprimirStatus() {
        System.out.println("\n=== STATUS FINAL DO CENTRO ===");
        System.out.printf("Docas Comuns disponíveis: %d/3%n", docasComuns.availablePermits());
        System.out.printf("Docas Refrigeradas disponíveis: %d/2%n", docasRefrigeradas.availablePermits());
        System.out.printf("Total de caminhões operados: %d%n", caminhoesOperadosComSucesso.get());
        System.out.printf("Total de caminhões que desistiram: %d%n", caminhoesQueDesistiram.get());
        System.out.println("==============================");
    }
    
    public Semaphore getDocasComuns() { return docasComuns; }
    public Semaphore getDocasRefrigeradas() { return docasRefrigeradas; }
    public Semaphore getBalancaEntrada() { return balancaEntrada; }
}