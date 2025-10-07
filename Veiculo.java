import java.util.Random;

public class Veiculo implements Runnable {

    private final int id;
    private final boolean prioritario;
    private final Estacionamento estacionamento;
    private final Random rand = new Random();

    public Veiculo(int id, boolean prioritario, Estacionamento estacionamento) {
        this.id = id;
        this.prioritario = prioritario;
        this.estacionamento = estacionamento;
    }

    public boolean isPrioritario() { return prioritario; }

    @Override
    public void run() {
        try {
            if (!estacionamento.tentarEstacionar(this)) return;

            int tempo = 1000 + rand.nextInt(4000); // 1–5 s
            System.out.println(this + " está estacionado por " + tempo + "ms");
            Thread.sleep(tempo);
            estacionamento.sair(this);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String toString() {
        return "Veículo #" + id + " (" + (prioritario ? "PRIORITARIO" : "NORMAL") + ")";
    }
}
