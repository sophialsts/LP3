import java.net.*;
import java.util.*;

public class CalculadoraClienteUDP {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        try {
            DatagramSocket socketCliente = new DatagramSocket();
            
            InetAddress enderecoServidor = 
            InetAddress.getByName("localhost");

            byte[] enviaData = new byte[1024];
            byte[] recebeData = new byte[1024];

            System.out.println("Digite o primeiro número: ");
            double num1 = teclado.nextDouble();
            System.out.println("Digite o segundo número: ");
            double num2 = teclado.nextDouble();
            System.out.println("Digite o operador (+,-,*,/): ");
            char op = teclado.next().charAt(0);
            String mensagem = num1 + "," + num2 + "," + op;

            enviaData = mensagem.getBytes();
            DatagramPacket pacoteEnvia = new DatagramPacket(enviaData, 
            enviaData.length, enderecoServidor, 12345);
            socketCliente.send(pacoteEnvia);

            DatagramPacket pacoteRecebe = new DatagramPacket(recebeData, 
            recebeData.length);
            socketCliente.receive(pacoteRecebe);
            String mensagemRecebida = new String(pacoteRecebe.getData());
            System.out.println("O resultado é: " + mensagemRecebida);
            
        } catch (Exception e) {
        }
    }

}
