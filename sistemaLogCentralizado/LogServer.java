import java.io.*;
import java.net.*;

public class LogServer {

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(12345)) {
            while(true) {
                System.out.println("Aguardando conexão socket...");
                Socket newSocket = server.accept(); // espera o socket do client com o host e a porta ser criado no LogClient (sem a mensagem ainda), como está na mesma porta o sevidor espera qualquer conexão e atribui a esse socket aqui para ser jogado para o LogHandler lidar com isso
                System.out.println("Conexão realizada! Passando para LogHandler!\n");
                new Thread(new LogHandler(newSocket)).start();
            }
        }
        catch(IOException e) {
        }
    }

    public static class LogHandler implements Runnable {

        private final Socket socket;

        public LogHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // ouve o LogClient
                ) {
                String log;
                while( (log = in.readLine()) != null ) {
                    System.out.println("[LOG DO CLIENTE]: " + log);
                }
            }
            catch(IOException e) {
            }
        }
    }


}