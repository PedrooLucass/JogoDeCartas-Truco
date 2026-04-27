import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Jogo {
    private BlockingQueue<Mensagem> filaMensagens = new LinkedBlockingQueue<>(); // Quando essa fila ser requisitada no .take() ela vai bloquar o fluxo de entrada das Threads pra poder pegar a mensagem desejada do jogador desejado, por isso o nome do bagulho é BLOCKINGqueue
    List<Dupla> duplas = new ArrayList<>();
    Baralho baralho = new Baralho();
    Scanner scanner = new Scanner(System.in);

    Carta cartaVira;

    int pontosRodada;
    TrucoServer server;

    public Jogo(TrucoServer server) {
        this.server = server;
    }

    public void iniciarJogo() {
        iniciarPartida();
    }

    public void iniciarPartida() {
        iniciarDuplas();
        duplas.get(0).zerarPartida();
        duplas.get(1).zerarPartida();

        while(duplas.get(0).getPontosPartida() < 12 && duplas.get(1).getPontosPartida() < 12) {
            String nomeDuplaGanhadora = iniciarRodada();

            if (duplas.get(0).getNomeDupla() == nomeDuplaGanhadora) {
                server.mensagemParaTodos(pontosRodada + " pontos sendo adicionados para a dupla "+ duplas.get(0).getNomeDupla());
                duplas.get(0).addPontosPartida(pontosRodada);
            } else {
                server.mensagemParaTodos(pontosRodada + " pontos sendo adicionados para a dupla "+ duplas.get(1).getNomeDupla());
                duplas.get(1).addPontosPartida(pontosRodada);
            }
        }

        if (duplas.get(0).getPontosPartida() >= 12) {
            server.mensagemParaTodos("Vencedor da partida: " + duplas.get(0).getNomeDupla());
        } else {
            server.mensagemParaTodos("Vencedor da partida: " + duplas.get(1).getNomeDupla());
        }
    }

    public String iniciarRodada() {
        String duplaFezPrimeira = "";
        baralho = new Baralho();
        pontosRodada = 1;

        duplas.get(0).zerarRodada();
        duplas.get(1).zerarRodada();

        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                duplas.get(j).jogadores.get(i).resetMaoDeCartas();
            }
        }

        baralho.embaralhar();
        distribuirCartas();

        server.mensagemParaTodos("---------------------------------------------------");

        cartaVira = baralho.getCarta();
        server.mensagemParaTodos("A carta vira é: "+ cartaVira.toString());

        while (duplas.get(0).getPontosRodada() < 2 && duplas.get(1).getPontosRodada() < 2) {

            if (duplas.get(0).getPontosRodada() == 1 && duplas.get(1).getPontosRodada() == 0) {
                duplaFezPrimeira = duplas.get(0).getNomeDupla();
            } else if (duplas.get(0).getPontosRodada() == 0 && duplas.get(1).getPontosRodada() == 1) {
                duplaFezPrimeira = duplas.get(1).getNomeDupla();
            }

            int indexJogadorGanhador = iniciarMao();

            if (indexJogadorGanhador == -1) {
                if (duplaFezPrimeira == null || duplaFezPrimeira.isEmpty()) {
                    server.mensagemParaTodos("Mão empatada! Proxima mão para desempate!");

                    indexJogadorGanhador = iniciarMao();

                    int buscadorIndex = 0;
                    for(int i = 0; i < 2; i++) {
                        for(int j = 0; j < 2; j++) {

                            if(buscadorIndex == indexJogadorGanhador) {
                                server.mensagemParaTodos(duplas.get(j).jogadores.get(i).getNome() + " ganhou a mão e o desempate!");
                                return duplas.get(j).getNomeDupla();
                            }
                            buscadorIndex++;
                        }
                    }
                } else {
                    if (duplaFezPrimeira == duplas.get(0).getNomeDupla()) {
                        server.mensagemParaTodos("Dupla "+ duplas.get(0).getNomeDupla() +" ganham por terem feito a primeira mão!");
                        return duplas.get(0).getNomeDupla();
                    } else {
                        server.mensagemParaTodos("Dupla "+ duplas.get(1).getNomeDupla() +" ganham por terem feito a primeira mão!");
                        return duplas.get(1).getNomeDupla();
                    }
                }   
            }

            int buscadorIndex = 0;

            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < 2; j++) {

                    if(buscadorIndex == indexJogadorGanhador) {
                        server.mensagemParaTodos(duplas.get(j).jogadores.get(i).getNome() + " ganhou a mão!");
                        duplas.get(j).addPontosRodada(pontosRodada);

                        int indexDuplaOposta = (j == 1) ? 0 : 1;

                        // Esse switch case é para tornar o plater que jogou a carta mais forte
                        switch (indexJogadorGanhador) {
                            case 1: // Quando o 2o player a jogar ganha a mão
                                Collections.swap(duplas.get(indexDuplaOposta).jogadores, 0, 1);
                                Collections.swap(duplas, 0, 1);
                                break;
                            case 2: // Quando o 3o player a jogar ganha a mão
                                Collections.swap(duplas.get(indexDuplaOposta).jogadores, 0, 1);
                                Collections.swap(duplas.get(j).jogadores, 0, 1);
                                break;
                            case 3: // Quando o 4o player a jogar ganha a mão
                                Collections.swap(duplas.get(j).jogadores, 0, 1);
                                Collections.swap(duplas, 0, 1);
                                break;
                        }
                    }
                    buscadorIndex++;
                }
            }
        }
        if (duplas.get(0).getPontosRodada() > 2) {
            return duplas.get(0).getNomeDupla();
        }
        if (duplas.get(1).getPontosRodada() > 2) {
            return duplas.get(1).getNomeDupla();
        }
        return "";
    }

    public int iniciarMao() {
        server.mensagemParaTodos("A mão está iniciando!");
        List<Carta> mesa = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {

                Jogador jogador = duplas.get(j).jogadores.get(i);

                server.mensagemParaTodos("Aguardando jogada de "+ jogador.getNome());
                while (true) {
                    server.mensagemPrivada(jogador, "\n\n------------------------------\n"+ jogador.getNome() + ", o que gostaria de fazer?");
                    server.mensagemPrivada(jogador, "1 - Jogar carta");
                    server.mensagemPrivada(jogador, "2 - Ver suas cartas");
                    server.mensagemPrivada(jogador, "3 - Trucar e jogar carta");
                    server.mensagemPrivada(jogador, "4 - Verificar pontos da partida");

                    int opcao = esperarJogadaInt(jogador, 1, 4);

                    if (opcao == 1) {
                        break;
                    }
                    if (opcao == 2) {
                        server.mensagemPrivada(jogador, "Lembrando que a carta vira é: " + cartaVira.toString());
                        server.mensagemPrivada(jogador, "\nEssas são suas cartas: ");
                        jogador.verCartas();
                    }
                    if (opcao == 3) {
                        server.mensagemParaTodos(jogador.getNome() + " está trucando!");

                        int opcaoTruco0, opcaoTruco1;
                        int indexDuplaTrucada = (j == 1) ? 0 : 1; // Aqui é pra identificar qual é a dupla que trucou (j == 1) pra assim ter acesso a outra dupla e fazer a proposta de truco individualmente pra dupla trucada
                        opcaoTruco0 = fazerPropostaTruco(indexDuplaTrucada, 0);
                        opcaoTruco1 = fazerPropostaTruco(indexDuplaTrucada, 1);

                        if (opcaoTruco0 == 1 && opcaoTruco1 == 1) { // Só é aceito o truco se ambos jogadores aceitarem
                            if (pontosRodada == 1) {
                                pontosRodada = 3;
                            } else {
                                pontosRodada += 3;
                            }
                            server.mensagemParaTodos("Agora a mão vale " + pontosRodada + " pontos.");
                            break;
                        } else {
                            server.mensagemParaTodos("A dupla inimiga correu!");
                            return mesa.size(); // Aqui vai retornar o index do ultimo jogador que jogou a carta, por exemplo, se é a primeira jogada e a pessoa já trucou, supondo que o adversário recusou, a pessoa que trucou ainda nn jogou carta alguma ent o mesa.size() vai retornar 0 que é justamente o index da pessoa que trucou
                        }
                    }

                    if (opcao == 4) {
                        server.mensagemPrivada(jogador, duplas.get(0).getNomeDupla() + ": " + duplas.get(0).getPontosPartida());
                        server.mensagemPrivada(jogador, duplas.get(1).getNomeDupla() + ": " + duplas.get(1).getPontosPartida());
                    }
                }

                server.mensagemPrivada(jogador, "Lembrando que a carta vira é: " + cartaVira.toString());
                Carta carta = jogador.jogarCarta(scanner, this);
                server.mensagemParaTodos("-> " + jogador.getNome() + " jogou " + carta);

                mesa.add(carta);
            }
        }
        List<Carta> cartaGanhadora = baralho.compararCartas(mesa, cartaVira);

        if (cartaGanhadora.size() >= 2) {
            if (cartaGanhadora.get(0).valorToInt() == (cartaVira.valorToInt()+1)) {
                server.mensagemParaTodos("Houveram manilhas jogadas na mesa!");

                Carta manilhaGanhadora = baralho.compararManilhas(cartaGanhadora);
                server.mensagemParaTodos("Porem a "+ manilhaGanhadora.toString() +" é a mais forte!");

                return mesa.indexOf(manilhaGanhadora);
            } else {
                server.mensagemParaTodos("Embuchou!");

                return -1;
            } 
        } else {
            return mesa.indexOf(cartaGanhadora.get(0));
        }
    }

    public void adicionarMensagem(Jogador jogador, String mensagem) {
        filaMensagens.add(new Mensagem(jogador, mensagem)); // Como tava FODA fazer o Jogo ter acesso as mensagens recebidas do Client de uma forma que o Jogo pudesse utilizar a mensagem, então ficou assim, meio estranho mas nn acho que chega a ser gambiarra. Assim burla o return, pq o return sempre corta o fluxo do Thread, assim ele só adiciona na lista e depois quando for necessário pro Jogo, o Jogo vai e pega a mensagem.
    }

    public int esperarJogadaInt(Jogador jogador, int numeroMIN, int numeroMAX) {
        while(true) {
            try {
                while (true) {
                    Mensagem mensagem = filaMensagens.take();

                    if (mensagem.getJogador().equals(jogador)) {
                        if (Integer.parseInt(mensagem.getMensagem()) >= numeroMIN && Integer.parseInt(mensagem.getMensagem()) <= numeroMAX) {
                            return Integer.parseInt(mensagem.getMensagem());
                        } else {
                            server.mensagemPrivada(jogador, "Número fora do escopo permitido! ");
                        }
                        
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String esperarJogadaStr(Jogador jogador) { // Mesma coisa da função de cima, só que pra String e sem número minimo e máximo, obviamente
        while(true) {
            try {
                Mensagem mensagem = filaMensagens.take();

                if (mensagem.getJogador().equals(jogador)) {
                    return mensagem.getMensagem();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int fazerPropostaTruco(int indexDupla, int indexJogador) {
        Jogador jogador = duplas.get(indexDupla).jogadores.get(indexJogador);

        Jogador parceiro = (indexJogador == 1) ? duplas.get(indexDupla).jogadores.get(0) : duplas.get(indexDupla).jogadores.get(1); // Aqui é só pra identificar qual é o parceiro e nn acabar vendo as própias cartas sem querer

        server.mensagemPrivada(jogador, "As cartas do seu aliado são: ");
        for(Carta carta : parceiro.getMaoCartas()) {
            jogador.enviarMensagem(carta.toString());
        }
        server.mensagemPrivada(jogador, jogador.getNome() + ", gostaria de aceitar o truco?");
        server.mensagemPrivada(jogador, "1 - Sim");
        server.mensagemPrivada(jogador, "2 - Não");

        return esperarJogadaInt(jogador, 1, 2);
    }

    public void iniciarDuplas() {
        List<Jogador> jogadores = server.jogadores;

        if (jogadores.size() != 4) {
            server.mensagemParaTodos("É necessário ter 4 jogadores.");
            return;
        }

        duplas.clear();

        server.mensagemPrivada(jogadores.get(0), "Qual sera o nome da sua dupla? "); // Aqui nn ficou muito democrático, os dois primeiros a entrarem no server ficam em duplas opostas e decidem qual vai ser o nome da respectiva dupla
        Dupla dupla0 = new Dupla(esperarJogadaStr(jogadores.get(0)), jogadores.get(0), jogadores.get(2)); // Parece confuso mas só é o new Dupla(Nome da Dupla, Jogador0, Jogador2 

        server.mensagemPrivada(jogadores.get(1), "Qual sera o nome da sua dupla? ");
        Dupla dupla1 = new Dupla(esperarJogadaStr(jogadores.get(1)), jogadores.get(1), jogadores.get(3));

        server.mensagemParaTodos("Duplas formadas!");
        server.mensagemParaTodos(dupla0.getNomeDupla() + " vs " + dupla1.getNomeDupla());

        // Aqui é só pra mostrar o nome das duplas e mostrar pra cada jogador o seu respectivo parceiro
        server.mensagemPrivada(jogadores.get(0), "Sua dupla é: "+jogadores.get(2).getNome()); 
        server.mensagemPrivada(jogadores.get(2), "Sua dupla é: "+jogadores.get(0).getNome());
        server.mensagemPrivada(jogadores.get(1), "Sua dupla é: "+jogadores.get(3).getNome());
        server.mensagemPrivada(jogadores.get(3), "Sua dupla é: "+jogadores.get(1).getNome());

        duplas.add(dupla0);
        duplas.add(dupla1);
    }

    public void distribuirCartas() {
        for (Dupla dupla : duplas) {
            for (Jogador jogador : dupla.jogadores) {
                System.out.println("Distribuindo cartas para " + jogador.getNome()); // Vc pediu pra tirar esse sout, pode tirar se quiser, só deixei aqui pq agora ele nem vai chegar pros Players/Client, só vai ficar como registro do Server, totalmente opcional tb
                jogador.addCarta(baralho.getCarta());
                jogador.addCarta(baralho.getCarta());
                jogador.addCarta(baralho.getCarta());
            }
        }
    }

    public void closeScanner() {
        scanner.close();
    }
}