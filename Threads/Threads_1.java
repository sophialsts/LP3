public class Threads_1 {
    public static void main(String[] args) {
        Thread t = Thread.currentThread();
        System.out.println(t.getName());

        MeuRunnable r = new MeuRunnable();
        
        Thread t0 = new Thread(r);
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        Thread t3 = new Thread(r);

        t0.start();
        //t0.start();
        t1.start();
        t2.start();
        t3.start();

    }
}
