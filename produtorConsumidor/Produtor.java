package produtorConsumidor;

import java.util.Random;
import java.util.concurrent.BlockingDeque;

public final class Produtor implements Runnable{

    private static final String[] BRINQUEDOS = {"Boneca", "Carrinho", "Beyblade", "Lego", "Pipa"};
    
    private final BlockingDeque<String> deque;
    private final Random random = new Random();

    public Produtor(final BlockingDeque<String> deque){
        this.deque = deque;
    }

    @Override
    public void run() {
        try {
            String brinquedo = BRINQUEDOS[random.nextInt(BRINQUEDOS.length)];
            System.out.println("Produtor: Tentando fabricar '" + brinquedo + "' (Fila: " + deque.size() + "/" + (deque.remainingCapacity() + deque.size()) + ")");
            this.deque.put(brinquedo);
            System.out.println("Produtor: Fabricou '" + brinquedo + "' com sucesso!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
