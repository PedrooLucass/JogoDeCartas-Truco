import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Jogador {
    private List<Carta> maoCartas = new ArrayList<>();
    private TrucoClientHandler clientHandler;
    private String nome;

    public Jogador(String nome) {
        this.nome = nome;
    }
    public List<Carta> getMaoCartas() {
        return maoCartas;
    }
    public void setClientHandler(TrucoClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }
    public String getNome() {
        return this.nome;
    }
    public void addCarta(Carta carta) {
        this.maoCartas.add(carta);
    }

    public Carta jogarCarta(Scanner scanner, Jogo jogo) {
        enviarMensagem("Qual carta gostaria de jogar?");

        verCartas();

        int indexEscolhaCarta = jogo.esperarJogadaInt(this, 1, maoCartas.size())-1;
        if (indexEscolhaCarta+1 > maoCartas.size() || indexEscolhaCarta+1 <= 0) {
            enviarMensagem("Opcao inválida! Endereço de carta não existe!");
        }

        Carta cartaEscolhida = maoCartas.get(indexEscolhaCarta);
        maoCartas.remove(indexEscolhaCarta);

        return cartaEscolhida;
    }
    public void verCartas() {
        for(int i = 0; i < maoCartas.size(); i++) {
            enviarMensagem((i+1) + " - " + maoCartas.get(i));
        }
    }
    public void resetMaoDeCartas() {
        if (!maoCartas.isEmpty()) {
            maoCartas.clear();
        }
    }

    public void enviarMensagem(String mensagem) {
        clientHandler.enviarMensagem(mensagem);
    }
}
