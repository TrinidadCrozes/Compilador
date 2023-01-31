package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.tipos.Tipo;

public class ParametroFormal implements EntradaVar{

    Token tokenParametro;
    Tipo tipoParametro;
    int offset;

    public ParametroFormal(Token token, Tipo tipo) {
        this.tokenParametro = token;
        this.tipoParametro = tipo;
        this.offset = 1;
    }

    public Tipo getTipo() {
        return tipoParametro;
    }

    public Token getToken() {
        return tokenParametro;
    }

    public Tipo getTipoParametro() {
        return tipoParametro;
    }

    public void chequearDeclaraciones() throws ExcepcionSemantica {
        tipoParametro.verificarExistenciaTipo();
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
        return false;
    }

    @Override
    public boolean offsetAsignado() {
        return offset != -1;
    }
}
