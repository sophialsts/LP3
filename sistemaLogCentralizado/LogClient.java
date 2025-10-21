import java.io.*;
import java.net.*;

public class LogClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {

        try (
            Socket socket = new Socket(HOST, PORT);
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter serverOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); // o true é para ativar o auto-flush, que envia assim que uma mensagem foi enviada com enter, sem isso ele capta no buffer mas não envia, aqui ele automaticamente já envia
        )
        {
            String linha;
            System.out.print("[LOG] = ");
            while((linha = userIn.readLine()) != null){
                serverOut.println(linha);
                System.out.print("[LOG] = ");
                if(linha.equals("exit")) {
                    break;
                }
            }
        }
        catch(IOException e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        }
        System.out.println("Encerrando...");
    }

}