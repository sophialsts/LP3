package RMI.Chat;

// TODO: Importar as classes necessárias para o RMI (Registry, etc.)
// TODO: Importar a interface (IChatServidor)
// TODO: Importar as classes para ler a entrada do usuário (ex: Scanner)

/**
 * ClienteChat (O Cliente)
 * * Este programa se conecta ao servidor RMI.
 */
public class ClienteChat {
    
    public static void main(String[] args) {
        
        try {
            // 1. TODO: Encontrar o "Porteiro" (Registry) que está no
            //    'localhost' e na porta que você usou no servidor (ex: 1099).
            // ... seu código para obter o registry aqui ...


            // 2. TODO: Pedir ao "Porteiro" pelo "controle remoto" (o Stub).
            //    Use o mesmo nome que você usou no 'rebind' do servidor (ex: "ChatRMI").
            //    Lembre-se de fazer o "cast" para a sua interface (IChatServidor).
            IChatServidor servicoChat = null; // ... substitua 'null' pela chamada de lookup ...
            

            // 3. TODO: Obter o nome do usuário.
            //    Peça ao usuário para digitar o nome dele no console.
            //    (Ex: "Digite seu nome para entrar no chat: ")
            String meuNome = ""; // ... seu código para ler o nome aqui ...
            
            
            // 4. --- SEU OBJETIVO PRINCIPAL ---
            //    TODO: Chame o método 'conectar' no 'servicoChat'
            //    e passe o 'meuNome' que você acabou de ler.
            // ... sua chamada de método aqui ...
            
            
            System.out.println("Conectado! Agora você pode enviar mensagens.");
            System.out.println("(Digite 'sair' para terminar)");

            // 5. TODO (Bônus): Crie um loop 'while(true)'.
            //    - Leia a próxima linha que o usuário digitar (a mensagem).
            //    - Se a mensagem for "sair", quebre o loop (break).
            //    - Se for qualquer outra coisa, chame o método 'enviarMensagem'
            //      no 'servicoChat', passando 'meuNome' e a 'mensagem'.
            
            // ... seu loop while aqui ...


        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.toString());
            e.printStackTrace();
        }
    }
}