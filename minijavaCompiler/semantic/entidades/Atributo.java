package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.TipoDeToken;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.tipos.Tipo;

public class Atributo implements EntradaVar {

    Token tokenAtributo;
    TipoDeToken visibilidad;
    Tipo tipo;
    Token tokenClaseDec;
    int offset;

    public Atributo(Token token, TipoDeToken visibilidad, Tipo tipo, Token tokenClaseDec) {
        this.tokenAtributo = token;
        this.visibilidad = visibilidad;
        this.tipo = tipo;
        this.tokenClaseDec = tokenClaseDec;
        offset = -1;
    }

    public void chequearDeclaraciones() throws ExcepcionSemantica {
        tipo.verificarExistenciaTipo();
    }

    public Token getToken() {
        return tokenAtributo;
    }

    public TipoDeToken getVisibilidad() {
        return visibilidad;
    }


    public Tipo getTipo() {
        return tipo;
    }


    public Token getTokenClaseDec() {
        return tokenClaseDec;
    }


    public boolean mismaSignatura(Atributo atr) {
        boolean mismoNombre = tokenAtributo.getLexema().equals(atr.tokenAtributo.getLexema());
        boolean mismaVisibilidad = visibilidad.equals(atr.visibilidad);
        boolean mismoTipo = tipo.getNombreTipo().equals(atr.tipo.getNombreTipo());
        return mismoNombre && mismaVisibilidad && mismoTipo;
    }

    @Override
    public int offset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public boolean esAtributo() {
        return true;
    }

    @Override
    public boolean offsetAsignado() {
        return offset != -1;
    }
}
