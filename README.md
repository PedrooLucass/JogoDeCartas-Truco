# Jogo de Truco (Java)

Implementação de um jogo de Truco em Java com arquitetura cliente-servidor, permitindo interação entre múltiplos jogadores via sockets.

## Visão geral

O projeto simula uma partida de Truco utilizando conceitos de orientação a objetos e comunicação em rede. A lógica do jogo é centralizada no servidor, enquanto os clientes interagem enviando comandos e recebendo atualizações em tempo real.

# Estrutura do projeto
Carta – Representa uma carta do baralho (naipe e valor)
Baralho – Responsável por criar e embaralhar as cartas
Jogador – Armazena dados do jogador e sua mão
Dupla – Representa equipes e pontuação
Jogo – Contém a lógica principal da partida
TrucoServer – Gerencia conexões e fluxo do jogo
TrucoClientHandler – Faz a comunicação entre servidor e cliente
TrucoClient – Interface de entrada do jogador via terminal

# Principais características
Arquitetura cliente-servidor usando sockets
Uso de múltiplas threads para lidar com jogadores simultâneos
Comunicação assíncrona com fila de mensagens (BlockingQueue)
Separação clara entre lógica de jogo e rede
Modelagem orientada a objetos
Pontos fortes do código
Organização modular das classes
Uso de estruturas concorrentes para sincronização
Encapsulamento das entidades do jogo
Facilidade de extensão (novas regras ou funcionalidades podem ser adicionadas sem grandes alterações)

# Como executar
1º Servidor
java TrucoServer
2º Cliente
java TrucoClient

Certifique-se de iniciar o servidor antes de conectar os quatro clientes.

# Autor

Pedro Lucas
