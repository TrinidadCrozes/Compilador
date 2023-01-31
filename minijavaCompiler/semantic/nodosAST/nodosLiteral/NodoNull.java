package minijavaCompiler.semantic.nodosAST.nodosLiteral;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.ExcepcionSemantica;
import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.tipos.TipoNull;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoOperando;

public class NodoNull extends NodoOperando {

    public NodoNull(Token token) {
        this.token = token;
    }

    @Override
    public TipoMetodo chequear() throws ExcepcionSemantica {
        return new TipoNull();
    }

    @Override
    public void generar() {
        TablaDeSimbolos.codigo.add("PUSH 0  ; Apilo null = 0");
    }

}
