package minijavaCompiler.semantic.nodosAST.nodosExpresion;

import minijavaCompiler.lexical.Token;

public abstract class NodoOperando extends NodoExpresion {

    public Token token;

    public NodoOperando() {}

    public Token getToken() {
        return token;
    }

}
