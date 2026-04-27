import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.Thread;

public class TrucoServer {
    Jogo jogo;
    ServerSocket serverSocket;
    List<Jogador> jogadores;
    List<TrucoClientHandler> clientHandlers;
    Scanner scanner = new Scanner(System.in);

    public TrucoServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.jogadores = new ArrayList<>();
        this.clientHandlers = new ArrayList<>();
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public void startServer() {
        try {
            while (clientHandlers.size() != 4) {
                System.out.println("Aguardando jogadores ("+clientHandlers.size()+"/4");

                Socket socket = serverSocket.accept(); // Aqui chega os players conectando pelo TrucoClient
                
                TrucoClientHandler clientHandler = new TrucoClientHandler(socket, this);

                clientHandlers.add(clientHandler);
                jogadores.add(clientHandler.getJogador()); // Nessas duas linhas conecta o TrucoClientHandler com o Jogador, assim tem maior controle em mandar mensagens privadas e alguns outros detalhes

                mensagemParaTodos("Aguardando jogadores ("+clientHandlers.size()+"/4");
                
                Thread thread = new Thread(clientHandler); // Assim que inicia a Thread, vai direto ler o run() dentro do ClientHandler, por isso tenho que implementar o Runnable no Handler
                thread.start(); // Inicia a Thread, ou seja, como se tivesse dois ou + códigos rodando ao mesmo tempo (e está mesmo)
            }
        } catch (Exception e) {
            closeServer();
        }
    }

    public void mensagemParaTodos(String mensagem) {
        System.out.println(mensagem); // o Sout dentro do TrucoServer é apenas pra deixar registrado no Server que a mensagem passou por aqui, é totalmente opcional, mas pode ajudar a debugar
        try {
            for(TrucoClientHandler clientHandler : clientHandlers) { // Não tem como mandar um /all ent só manda individualmente pra todo mundo msm
                clientHandler.enviarMensagem(mensagem);
            }
        } catch (Exception e) {
            closeServer();
        }
    }

    public void mensagemPrivada(Jogador jogador, String mensagem) {
        System.out.println("Enviando para " + jogador.getNome() + ": " + mensagem);
        jogador.enviarMensagem(mensagem); // O caminho que a mensagem faz para ser ENVIADA: Jogo -> Server -> Jogador -> ClientHandler -> Socket -> Client
    }

    public void processarMensagemClient(Jogador jogador, String mensagem) {
        if (jogo != null) {
            jogo.adicionarMensagem(jogador, mensagem); // O caminho que a mensagem faz para CHEGAR: Client -> Socket -> ClientHandler -> Server -> Jogo -> o Jogo "pausa" pra receber a mensagem requisitada -> Jogo volta a funcionar
        }
    }

    public void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4080);
            TrucoServer server = new TrucoServer(serverSocket);

            server.startServer();

            Jogo jogo = new Jogo(server);
            server.setJogo(jogo);
            jogo.iniciarJogo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
/*
Passo a Passo num jogo de truco.
1 - Definir nomes e duplas [Jogo -> Dupla -> Jogador]
2 - Iniciar a partida (dupla que chegar a 12 pontos ganha) [Dupla -> Jogo]
    3 - Iniciar a rodada (dupla melhor de 3 ganha) [Dupla -> Jogo]
        4 - Iniciar a mão (as duplas jogam as cartas e ganha a mais forte) [Jogo]
            5 - Embaralhar cartas [Jogo -> Baralho]
            6 - Distribuir cartas para os jogadores [Jogo -> Dupla -> Jogador -> Baralho]
            7 - Jogadores escolhem e jogam as cartas [Jogo -> Dupla -> Jogador -> Carta]
            8 - Compara as cartas e define ganhador da mão [Jogo <-> Baralho]
            9 - Soma quantidade de pontos da mão para o jogador/dupla [Jogo -> Dupla]
*/
