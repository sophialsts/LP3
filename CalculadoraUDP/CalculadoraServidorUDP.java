
import java.io.IOException;
import java.net.*;


public class CalculadoraServidorUDP {
    static Calculadora calc = new Calculadora();
    public static void main(String[] args) throws IOException {
        try {
            DatagramSocket socket = new DatagramSocket(12345);
            byte[] entrada = new byte[1024];
            byte[] saida = new byte[1024];
            while (true) {
                DatagramPacket pacoteEntrada = new 
                DatagramPacket(entrada, entrada.length);
                socket.receive(pacoteEntrada);
                String mensagemRecebida = new String(pacoteEntrada.getData());
                
                String[] partes = mensagemRecebida.split(",");
        
                calc.atribuir(Double.parseDouble(partes[0]), 
                Double.parseDouble(partes[1]), 
                partes[2].charAt(0));

                //System.out.println("Operacao realizada: " + partes[2].charAt(0));
    
                double resultado = calc.calcular();
                saida = String.valueOf(resultado).getBytes();
                //System.out.println("Operacao realizada: " + resultado);
                DatagramPacket pacoteSaida = new DatagramPacket(saida, 
                saida.length, pacoteEntrada.getAddress(), pacoteEntrada.getPort());
                socket.send(pacoteSaida);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
