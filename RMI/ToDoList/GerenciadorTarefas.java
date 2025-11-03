package RMI.ToDoList;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;

public interface GerenciadorTarefas extends Remote{

    void adicionarTarefa(String tarefa) throws RemoteException;
    List<String> listarTarefas() throws RemoteException;

}