import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SincronizarColecoes {

    // private static List<String> lista = new CopyOnWriteArrayList<>();
    private static Map<Integer, String> mapa = new ConcurrentHashMap<>();
    private static BlockingQueue<String> fila = new LinkedBlockingQueue<>();
    // private static BlockingDeque<String> deque = new LinkedBlockingDeque<>();

    public static void main(String[] args) throws InterruptedException {
        MeuRunnable runnable = new MeuRunnable();
        Thread t0 = new Thread(runnable);
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t0.start();
        t1.start();
        t2.start();
        Thread.sleep(500);
        // System.out.println(lista);
        // System.out.println(mapa);
        System.out.println(fila);
    }

    public static class MeuRunnable implements Runnable { 
        public void run() {
            // lista.add("LP-III");
            // mapa.put(new Random().nextInt(100), "LP-III");
            fila.add("LP-III");
            String name = new Thread().currentThread().getName();
            System.out.println(name + ": inseriu na lista");
        }
    }
}
