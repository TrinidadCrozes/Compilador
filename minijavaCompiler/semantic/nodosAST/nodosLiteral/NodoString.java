package minijavaCompiler.semantic.nodosAST.nodosLiteral;

import minijavaCompiler.lexical.TipoDeToken;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.tipos.TipoClase;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoOperando;

public class NodoString extends NodoOperando {

    private static int index;

    public NodoString(Token token) {
        this.token = token;
    }

    @Override
    public TipoMetodo chequear() {
        return new TipoClase(new Token(TipoDeToken.idClase, "String", token.getNroLinea()));
    }

    public int getIndex() {
        index++;
        return index;
    }

    @Override
    public void generar() {
        String string = token.getLexema();
        int i = getIndex();
        TablaDeSimbolos.codigo.add(".DATA");
        TablaDeSimbolos.codigo.add("string"+ i +": DW " + string + ",0  ; Apilo el string");
        TablaDeSimbolos.codigo.add(".CODE");
        TablaDeSimbolos.codigo.add("PUSH string" + i);
    }


}
