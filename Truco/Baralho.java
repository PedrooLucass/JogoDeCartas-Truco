import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baralho {
    protected String[] todosNaipes = {"Ouros", "Espadas", "Copas", "Paus"};
    protected char[] todosValores = {'4', '5', '6', '7', 'Q', 'J', 'K', 'A', '2', '3'};
    public int quantidadeCartas;
    List<Carta> baralho = new ArrayList<>();

    public Baralho() {
        for(char valor : todosValores) {
            for(String naipe : todosNaipes) {
                Carta carta = new Carta(valor, naipe);
                baralho.add(carta);
            }
        }
    }
    public void embaralhar() {
        Collections.shuffle(baralho);
        this.quantidadeCartas = baralho.size();
    }
    public Carta getCarta() {
        if (quantidadeCartas == 0) {
            throw new RuntimeException("Baralho vazio!");
        }

        Carta carta = baralho.get(quantidadeCartas - 1);
        baralho.remove(quantidadeCartas - 1);
        quantidadeCartas--;

        return carta;
    }
    public List<Carta> compararCartas(List<Carta> mesa, Carta cartaVira) {
        List<Carta> cartaMaisForte = new ArrayList<>();

        int valorManilha;
        int valorMaisForte = -1;

        if (cartaVira.valorToInt() == 10) {
            valorManilha = 1;
        } else {
            valorManilha = cartaVira.valorToInt()+1;
        }

        for (Carta carta : mesa) {
            int valorAtual = carta.valorToInt();

            if(valorManilha == valorAtual) {
                valorAtual = 11;
            }

            if (valorAtual > valorMaisForte) {
                cartaMaisForte.clear();
                cartaMaisForte.add(carta);
                valorMaisForte = valorAtual;
            } else if (valorAtual == valorMaisForte) {
                cartaMaisForte.add(carta);
            }
        }

        return cartaMaisForte;
    }

    public Carta compararManilhas(List<Carta> listaManilhas) {
        int valorAtual, valorMaisForte = -1;
        Carta cartaMaisForte = listaManilhas.get(0);

        for(Carta cartaManilha : listaManilhas) {
            switch (cartaManilha.getNaipe()) {
                case "Ouros":
                    valorAtual = 1;
                    break;
                case "Espadas":
                    valorAtual = 2;
                    break;
                case "Copas":
                    valorAtual = 3;
                    break;
                case "Paus":
                    valorAtual = 4;
                    break;
                default:
                    valorAtual = 0;
                    break;
            }

            if (valorAtual > valorMaisForte) {
                valorMaisForte = valorAtual;
                cartaMaisForte = cartaManilha;
            }
        }
        return cartaMaisForte;
    }
}
