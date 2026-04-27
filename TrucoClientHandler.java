import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class TrucoClientHandler implements Runnable {

    private Jogador jogador;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private TrucoServer server;

    public TrucoClientHandler(Socket socket, TrucoServer server) {
        try {
            this.server = server;
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // Explicando o Reader: a mensagem chega pelo Socket InputStreamReader como código binário, o BufferedReader deixa legivel, parece difícil mas é simples
            writer.write("Digite seu nome:");
            writer.newLine();
            writer.flush();

            String nome = reader.readLine();

            this.jogador = new Jogador(nome);
            this.jogador.setClientHandler(this);
        } catch (Exception e) {
            closeEverything(socket, reader, writer, null);
        }
    }
    public Jogador getJogador() {
        return this.jogador;
    }

    @Override
    public void run() { // SEMPRE que implementar o Runnable, tem que ter essa função no Override e sem isso o Thread não funciona, nn sei o porquê, só aceitei e nn corri atrás de pesquisar pra saber
        while (socket.isConnected()) {
            try {
                String mensagem = reader.readLine();
                if (mensagem == null) break;

                server.processarMensagemClient(jogador, mensagem); // Aqui manda pro Server, seria mais fácil mandar por um return, porém o return encerra a função e essa aqui não pode ser encerrada nunca, caso contrário deixamos de ouvir o Client

            } catch (Exception e) {
                closeEverything(socket, reader, writer, null);
                break;
            }
        }
    }

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
    }

    public void enviarMensagem(String mensagem) {
        try {
            writer.write(mensagem); // Aqui ele manda a mensagem e o newLine e o flush só servem pra limpar o writer
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            closeEverything(socket, reader, writer, null);
        }
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
}
