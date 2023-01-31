package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.Token;

public interface EntradaVar {

    public Token getToken();

    public int offset();

    public boolean esAtributo();

    boolean offsetAsignado();
}
