package minijavaCompiler.semantic.nodosAST.nodosLiteral;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.tipos.TipoBoolean;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoOperando;

public class NodoTrue extends NodoOperando {

    public NodoTrue(Token token) {
        this.token = token;
    }

    @Override
    public TipoMetodo chequear() {
        return new TipoBoolean();
    }

    @Override
    public void generar() {
        TablaDeSimbolos.codigo.add("PUSH " + 1 + "  ; Apilo true = 1");
    }

}
