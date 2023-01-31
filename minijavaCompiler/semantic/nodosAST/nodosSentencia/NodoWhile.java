package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoBoolean;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public class NodoWhile extends NodoSentencia {

    Token tokenWhile;
    NodoExpresion expresionCondicion;
    NodoSentencia sentencia;
    private static int numWhile;
    private static int numFinWhile;
    public NodoWhile(Token tokenWhile, NodoExpresion expresionCondicion, NodoSentencia sentencia) {
        this.tokenWhile = tokenWhile;
        this.expresionCondicion = expresionCondicion;
        this.sentencia = sentencia;
    }

    @Override
    public void chequear() throws ExcepcionSemantica {
        TipoMetodo tipoExpresionCond = expresionCondicion.chequear();
        if(tipoExpresionCond.esIgualTipo(new TipoBoolean())) {
            NodoBloque nodoBloqueWhile = new NodoBloque();
            nodoBloqueWhile.setOffsetVarLocalDisp();
            TablaDeSimbolos.apilarBloque(nodoBloqueWhile);
            if(sentencia != null)
                sentencia.chequear();
            TablaDeSimbolos.desapilarBloque();
        } else {
            throw new ExcepcionSemantica(tokenWhile, "la expresión de la condición no es de tipo boolean");
        }
    }

    @Override
    public void generar() {
        String etiqueta_while = nuevaEtiquetaWhile();
        String etiqueta_finWhile = nuevaEtiquetaFinWhile();

        TablaDeSimbolos.codigo.add(etiqueta_while + ": NOP");
        expresionCondicion.generar();
        TablaDeSimbolos.codigo.add("BF " +  etiqueta_finWhile + "  ; Salta a la siguiente línea después del while si la condición es falsa");
        if(sentencia != null)
            sentencia.generar();
        TablaDeSimbolos.codigo.add("JUMP " + etiqueta_while);
        TablaDeSimbolos.codigo.add(etiqueta_finWhile + ": NOP");
    }

    private String nuevaEtiquetaWhile() {
        String etiqueta = "while_" + numWhile;
        numWhile++;
        return etiqueta;
    }

    private String nuevaEtiquetaFinWhile() {
        String etiqueta = "finWhile_" + numFinWhile;
        numFinWhile++;
        return etiqueta;
    }
}
