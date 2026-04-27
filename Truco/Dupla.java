import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dupla {
    public String nome;
    public List<Jogador> jogadores = new ArrayList<>();
    public int pontuacaoPartida;
    public int pontuacaoRodada;

    public Dupla(String nome, Jogador jogador1, Jogador jogador2) {
        this.nome = nome;
        this.jogadores.add(jogador1);
        this.jogadores.add(jogador2);
        this.pontuacaoPartida = 0;
        this.pontuacaoRodada = 0;
    }
    public int getPontosPartida() {
        return pontuacaoPartida;
    }
    public int getPontosRodada() {
        return pontuacaoRodada;
    }
    public String getNomeDupla() {
        return this.nome;
    }
    public void zerarPartida() {
        this.pontuacaoPartida = 0;
    }
    public void zerarRodada() {
        this.pontuacaoRodada = 0;
    }
    public void addPontosRodada(int pontos) {
        this.pontuacaoRodada += pontos;
    }
    public void addPontosPartida(int pontos) {
        this.pontuacaoPartida += pontos;
    }
    public void inverterDupla() {
        Collections.swap(this.jogadores, 0, 1);
    }
}
