public class MeuRunnable implements Runnable{
    static Object lock1 = new Object();
	static Object lock2 = new Object();

    static int i = -1;
    public void run(){
        synchronized(lock1){
            i++;
            String nome = Thread.currentThread().getName();
            System.out.println(nome + ":" + i);
        }
        synchronized(lock2){
            i++;
            String nome = Thread.currentThread().getName();
            System.out.println(nome + ":" + i);
        }
    }
}
