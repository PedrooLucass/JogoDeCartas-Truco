public class Mensagem {
    private Jogador jogador;
    private String mensagem;

    public Mensagem(Jogador jogador, String mensagem) {
        this.jogador = jogador;
        this.mensagem = mensagem;
    }

    public Jogador getJogador() {
        return jogador;
    }
    public String getMensagem() {
        return mensagem;
    }
}
