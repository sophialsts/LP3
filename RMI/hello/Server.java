package RMI.hello;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements Hello {
    public Server() throws RemoteException {
        super();
    }

    public String sayHello() throws RemoteException {
        return "Hello, world";
    }

    public static void main(String args[]) {
        try {
            // 1. Este comando cria o registro RMI na porta 1099 
            LocateRegistry.createRegistry(1099);

            Server obj = new Server();

            // 2. Obtém o registro e "amarra" (bind) o objeto a um nome [cite: 513, 514]
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Hello", obj);

            System.out.println("Server ready"); // 
        } catch (Exception e) {
            System.out.println("Server Exception" + e.toString());
            e.printStackTrace();
        }
    }
}