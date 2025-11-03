package RMI.ToDoList;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.List;

public class ClienteTarefas {
    
    public static void main(String[] args) {        
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            GerenciadorTarefas servico = (GerenciadorTarefas) registry.lookup("ServicoTarefas"); // pega o 'controle remoto' / stub pro cliente
            Scanner leitor = new Scanner(System.in);
            while(true) {
                System.out.println("1 -  Adicionar");
                System.out.println("2 -  Listar");
                System.out.println("0 -  Sair");
                int op = leitor.nextInt();
                leitor.nextLine();
                switch(op) {
                    case 0:
                        return;
                    case 1:
                        System.out.println("Digite a tarefa que você deseja adicionar:\n");
                        String novaTarefa = leitor.nextLine();
                        servico.adicionarTarefa(novaTarefa); //chamada RMI
                        break;
                    case 2:
                        List<String> listaTarefas = servico.listarTarefas(); //chamada RMI
                        for(String tarefa : listaTarefas) {
                            System.out.println(tarefa);
                        }
                        break;
                }
            }
        }
        catch(Exception e) {
            System.out.println("Server Exception" + e.toString());
            e.printStackTrace();
        }

    }

}
