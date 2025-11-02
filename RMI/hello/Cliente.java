package RMI.hello;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente {
    private Cliente() {}

    public static void main(String args[]) {
        try {
            // 1. Obtém o registro (no localhost, porta 1099 por padrão) 
            Registry registry = LocateRegistry.getRegistry("localhost");

            // 2. Faz o "lookup" para obter o stub do servidor 
            Hello stub = (Hello) registry.lookup("Hello");

            // 3. Invoca o método remoto 
            String response = stub.sayHello();
            System.out.println("Response: " + response);  
        } catch (Exception e) {
            System.out.println("Cliente exception" + e.toString());
            e.printStackTrace();
        }
    }
}