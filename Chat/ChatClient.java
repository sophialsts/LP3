import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(HOST, PORT);
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            AtomicBoolean running = new AtomicBoolean(true);

            // Thread para ler mensagens do servidor
            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                } finally {
                    running.set(false);
                }
            });
            reader.setDaemon(true);
            reader.start();

            // Loop para enviar teclado → servidor
            String userLine;
            while (running.get() && (userLine = userIn.readLine()) != null) {
                serverOut.println(userLine);
                if ("exit".equalsIgnoreCase(userLine.trim())) {
                    break;
                }
            }
            try { reader.join(300); } catch (InterruptedException ignore) {}
        } catch (IOException e) {
            System.err.println("[Cliente] Erro: " + e.getMessage());
        }
    }
}