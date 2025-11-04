package RMI.Chat;

import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

// TODO: Importar a classe para a exportação manual (UnicastRemoteObject)

/**
 * ChatServidorImpl (A Implementação)
 * * Esta classe é o servidor real.
 * Ela implementa a interface, mas NÃO herda de UnicastRemoteObject.
 */
public class ChatServidorImpl implements IChatServidor {

    /**
     * O construtor é simples, pois não precisamos chamar 'super()'.
     */
    public ChatServidorImpl() {
        System.out.println("Objeto do servidor criado.");
    }


    /*
     * TODO: Implemente o método 'conectar' que você definiu na interface.
     * A lógica dele deve ser apenas imprimir no console do SERVIDOR.
     * Ex: System.out.println(nomeCliente + " conectou-se ao chat.");
     */
    @Override
    public void conectar(String nomeCliente) throws RemoteException {
        System.out.println(nomeCliente + " conectou-se ao chat.");
    }


    /*
     * TODO: Implemente o método 'enviarMensagem' que você definiu.
     * A lógica dele deve ser apenas imprimir no console do SERVIDOR.
     * Ex: System.out.println(nomeCliente + ": " + mensagem);
     */
    @Override
    public void enviarMensagem(String nomeCliente, String mensagem) throws RemoteException {
        System.out.println(nomeCliente + ": " + mensagem);
    }


    /**
     * O 'main' vai iniciar o servidor.
     */
    public static void main(String[] args) {
        
        System.out.println("Iniciando o servidor de chat...");
        
        try {
            // --- É AQUI QUE VOCÊ PRATICA ---
            
            // 1. Criar o objeto servidor real (o "especialista")
            ChatServidorImpl servidor = new ChatServidorImpl();

            // 2. TODO: "Exportar" o objeto 'servidor' para a rede manualmente.
            //    Você precisa usar o método estático de 'UnicastRemoteObject'.
            //    Guarde o resultado (que é o 'Stub') em uma variável.
            //    (Dica: O segundo parâmetro pode ser 0, para usar uma porta anônima).
            IChatServidor stub = (IChatServidor) UnicastRemoteObject.exportObject(servidor,0); 
            
            // 3. TODO: Criar o "Porteiro" (Registry) em uma porta (ex: 1099).
            Registry registry = LocateRegistry.createRegistry(1098);
            
            // 4. TODO: Registrar o 'stub' (que você obteve no passo 2) no "Porteiro".
            //    Dê a ele um nome fácil de lembrar (ex: "ChatRMI").
            registry.rebind("ChatRMI", stub);
            
            System.out.println("Servidor está no ar. Aguardando clientes.");

        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}