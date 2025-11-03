package RMI.Chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * IChatServidor (A Interface)
 * * Este é o "contrato" RMI.
 * Defina aqui os métodos que o cliente pode chamar no servidor.
 */
public interface IChatServidor extends Remote {

    /**
     * Um cliente usa este método para "entrar" no chat,
     * anunciando seu nome para o servidor.
     * * TODO: Defina a assinatura do método 'conectar'.
     * - Ele deve se chamar "conectar".
     * - Ele deve receber um parâmetro (o nome do cliente).
     * - Ele deve (obrigatoriamente) declarar que pode lançar uma RemoteException.
     */
    // void ...(...);
    
    
    /**
     * (Passo 2) Um cliente usa este método para enviar uma mensagem.
     * * TODO: Defina a assinatura do método 'enviarMensagem'.
     * - Ele deve receber dois parâmetros (o nome do cliente e a mensagem).
     * - Ele também deve declarar a RemoteException.
     */
    // void ...(...);
    
}