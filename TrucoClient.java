import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class TrucoClient {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Scanner scanner = new Scanner(System.in);

    public TrucoClient(Socket socket) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            closeEverything(socket, reader, writer, scanner);
        }
    }

    public void receberMensagem() {
        // Isso aqui se chama Expressão Lambda, vale pesquisar rapidinho o que é por fora, mas basicamente a new Thread() deveria receber como parametro uma classe/objeto e com a expressõa lambda a gente cria a classe/objeto dentro da própria chamada do new Thread()
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mensagem;

                while (socket.isConnected()) {
                    try {
                        mensagem = reader.readLine();
                        System.out.println(mensagem);
                    } catch (Exception e) {
                        closeEverything(socket, reader, writer, scanner);
                    }
                }
            }
        }).start();
    }

    public void enviarMensagem() {
        new Thread(() -> { 
            while (socket.isConnected()) {
                try {
                    String mensagem = scanner.nextLine();

                    writer.write(mensagem);
                    writer.newLine();
                    writer.flush();
                } catch (Exception e) {
                    closeEverything(socket, reader, writer, scanner);
                    break;
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer, Scanner scanner) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4080);
            TrucoClient client = new TrucoClient(socket);
            client.receberMensagem(); // Se você reparar, o Client ficaria parado aqui sem o Thread pois o mensagem = reader.readLine(); é um comando que bloqueia o fluxo até que chegasse uma resposta, além de estar rodando em um while(true), porem com o Thread a aplicação lê essa linha e deixa ela rodando enquanto em outra Thread fica responsável para enviar a mensagem na prox linha
            client.enviarMensagem();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
