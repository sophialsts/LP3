package RMI.sistemaLeilao;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente {
    
    private final String nome;

    public Cliente(String nome) {
        this.nome = nome;
    }

    public static void main(String[] args) {

        /*try {
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
        } */


        try {
            // 1. Obtém o registro (no localhost, porta 1099 por padrão) 
            Registry registry = LocateRegistry.getRegistry("localhost");

            Metodos stub = (Metodos) registry.lookup("Metodos");

            // invoca os métodos
        }
        catch(Exception e) {
            System.out.println("Cliente exception" + e.toString());
            e.printStackTrace();
        }
    }

    public void notificacaoMaiorLance() {
        System.out.println(" ----------------------------------------\n");
        System.out.println("\nFoi identificado um novo maior lance no leilão!\n");
        System.out.println(" ----------------------------------------\n");
    }

    public void notificacaoEncerramentoLeilao() {
        System.out.println(" ----------------------------------------\n");
        System.out.println("\nO leilão foi encerrado!\n");
        System.out.println(" ----------------------------------------\n");
    }

}
