package calculadora;

public class nodoCalculadora {
    String symbol;
    nodoCalculadora sig; 

    public nodoCalculadora(){

    }

    public nodoCalculadora(String symbol) {
        this.symbol = symbol;
        this.sig = null;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public nodoCalculadora getSig() {
        return sig;
    }

    public void setSig(nodoCalculadora sig) {
        this.sig = sig;
    }
}