public class Carta {
    private String naipe;
    private char valor;

    public Carta(char valor, String naipe) {
        this.valor = valor;
        this.naipe = naipe;
    }

    public char getValor() {
        return this.valor;
    }
    public String getNaipe() {
        return this.naipe;
    }
    public String toString() {
        switch (this.valor) {
            case 'Q':
                return "Rainha de " + this.naipe;
            case 'J':
                return "Valete de " + this.naipe;
            case 'K':
                return "Rei de " + this.naipe;
            case 'A':
                return "As de " + this.naipe;
            default:
                return this.valor + " de " + this.naipe;
        }
    }
    public int valorToInt() {
        switch (this.valor) {
            case '4':
                return 1;
            case '5':
                return 2;
            case '6':
                return 3;
            case '7':
                return 4;
            case 'Q':
                return 5;
            case 'J':
                return 6;
            case 'K':
                return 7;
            case 'A':
                return 8;
            case '2':
                return 9;
            case '3':
                return 10;
            default:
                return 0;
        }
    }
}


