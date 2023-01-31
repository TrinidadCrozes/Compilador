package minijavaCompiler.semantic.nodosAST.nodosLiteral;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.tipos.TipoChar;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoOperando;

public class NodoChar extends NodoOperando {

    public NodoChar(Token token) {
        this.token = token;
    }

    @Override
    public TipoMetodo chequear() {
        return new TipoChar();
    }

    @Override
    public void generar() {
        TablaDeSimbolos.codigo.add("PUSH " + token.getLexema() + "  ; Apila el char " + token.getLexema());
    }

}
