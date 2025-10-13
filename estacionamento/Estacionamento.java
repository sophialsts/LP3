import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Estacionamento {

    private final Semaphore vagasRegulares = new Semaphore(5, true);
    private final Semaphore vagasPrioritarias = new Semaphore(2, true);
    private final Semaphore portaoEntrada = new Semaphore(1, true);
    private final Semaphore portaoSaida = new Semaphore(1, true);

    private final AtomicInteger totalEntradas = new AtomicInteger(0);
    private final AtomicInteger totalDesistencias = new AtomicInteger(0);

    public boolean tentarEstacionar(Veiculo v) throws InterruptedException {
        portaoEntrada.acquire();
        System.out.println(v + " chegou ao portão de entrada");

        boolean conseguiuVaga = false;
        if (v.isPrioritario()) {
            conseguiuVaga = vagasPrioritarias.tryAcquire();
            if (conseguiuVaga) {
                System.out.println(v + " conseguiu vaga PRIORITÁRIA");
            } else if (vagasRegulares.tryAcquire()) {
                conseguiuVaga = true;
                System.out.println(v + " conseguiu vaga REGULAR");
            }
        } else {
            conseguiuVaga = vagasRegulares.tryAcquire();
            if (conseguiuVaga) {
                System.out.println(v + " conseguiu vaga REGULAR");
            }
        }

        portaoEntrada.release();

        if (conseguiuVaga) {
            totalEntradas.incrementAndGet();
        } else {
            System.out.println(v + " desistiu (sem vaga)");
            totalDesistencias.incrementAndGet();
        }

        return conseguiuVaga;
    }

    public void sair(Veiculo v) throws InterruptedException {
        portaoSaida.acquire();
        System.out.println(v + " está saindo do estacionamento...");
        Thread.sleep(500); // tempo simbólico de saída
        if (v.isPrioritario() && vagasPrioritarias.availablePermits() < 2) {
            vagasPrioritarias.release();
        } else if (!v.isPrioritario() && vagasRegulares.availablePermits() < 5) {
            vagasRegulares.release();
        }
        System.out.println(v + " saiu com sucesso!");
        portaoSaida.release();
    }

    public void exibirStatus() {
        System.out.println("\n=== STATUS DO ESTACIONAMENTO ===");
        System.out.printf("Vagas regulares disponíveis: %d/5%n", vagasRegulares.availablePermits());
        System.out.printf("Vagas prioritárias disponíveis: %d/2%n", vagasPrioritarias.availablePermits());
        System.out.printf("Total de entradas: %d%n", totalEntradas.get());
        System.out.printf("Total de desistências: %d%n", totalDesistencias.get());
        System.out.println("================================");
    }

    public int getTotalEntradas() { return totalEntradas.get(); }
    public int getTotalDesistencias() { return totalDesistencias.get(); }
}
