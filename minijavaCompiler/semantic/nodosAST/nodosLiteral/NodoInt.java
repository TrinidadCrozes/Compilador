package minijavaCompiler.semantic.nodosAST.nodosLiteral;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.tipos.TipoInt;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoOperando;

public class NodoInt extends NodoOperando {

    public NodoInt(Token token) {
        this.token = token;
    }


    @Override
    public TipoMetodo chequear() {
        return new TipoInt();
    }

    @Override
    public void generar() {
        TablaDeSimbolos.codigo.add("PUSH " + token.getLexema() + "  ; Apila el entero " + token.getLexema());
    }

}
