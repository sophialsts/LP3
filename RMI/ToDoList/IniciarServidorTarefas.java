package RMI.ToDoList;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class IniciarServidorTarefas {
    
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            GerenciadorTarefasImpl servicoTarefas = new GerenciadorTarefasImpl();
            registry.rebind("ServicoTarefas", servicoTarefas);
            System.out.println("Servidor de Tarefas no ar!");
        }
        catch(Exception e) {
            System.out.println("Server Exception" + e.toString());
            e.printStackTrace();
        }   
    }

}
