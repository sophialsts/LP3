import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    private static final int PORT = 12345;
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>(); // thread-safe, armazena clientes

    public static void main(String[] args) {
        System.out.println("[Servidor] Ouvindo na porta " + PORT + "...");
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[Servidor] Erro: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private String name;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                // primeira mensagem = nome
                out.println("Digite seu nome:");
                this.name = in.readLine();
                if (name == null || name.isBlank()) {
                    name = "Anon-" + socket.getPort();
                }

                clients.add(this);
                out.println("Bem-vindo, " + name + "! Digite mensagens. Use 'exit' para sair.");
                broadcast("[Servidor] " + name + " entrou no chat.", null);

                String line;
                while ((line = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(line.trim())) {
                        out.println("Você saiu do chat. Até logo!");
                        break;
                    }
                    broadcast("[" + name + "] " + line, this);
                }
            } catch (IOException e) {
                // desconexão
            } finally {
                clients.remove(this);
                broadcast("[Servidor] " + name + " saiu do chat.", null);
                try { socket.close(); } catch (IOException ignore) {}
            }
        }

        private void broadcast(String msg, ClientHandler sender) {
            for (ClientHandler client : clients) {
                if (client == sender) continue;
                synchronized (client.out) {
                    client.out.println(msg);
                }
            }
        }
    }
}