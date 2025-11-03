package RMI.ToDoList;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class GerenciadorTarefasImpl extends UnicastRemoteObject implements GerenciadorTarefas {
    // extends UnicastRemoteObject, torna o serviço do Gerenciador de Tarefas remoto e possível de se trabalhar na rede,
    // conectar com clientes, retornar respostas, chamadas RMI

    List<String> tarefas;

    public GerenciadorTarefasImpl() throws RemoteException {
        super();
        this.tarefas = Collections.synchronizedList(new ArrayList<>());
    }

    public void adicionarTarefa(String tarefa) throws RemoteException {
        this.tarefas.add(tarefa);
    }

    public List<String> listarTarefas() throws RemoteException {
        return new ArrayList<>(this.tarefas);
    }

}
