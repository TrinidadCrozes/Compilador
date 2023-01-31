package minijavaCompiler.lexical;

public class Token {

    TipoDeToken tipoDeToken;
    String lexema;
    int nroLinea;

    public Token(TipoDeToken tipoDeToken, String lexema, int nroLinea) {
        this.tipoDeToken = tipoDeToken;
        this.lexema = lexema;
        this.nroLinea = nroLinea;
    }

    public TipoDeToken getTipoDeToken() {
        return tipoDeToken;
    }

    public String getLexema() {
        return lexema;
    }

    public int getNroLinea() {
        return nroLinea;
    }

}
