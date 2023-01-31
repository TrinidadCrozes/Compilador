package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoBoolean;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public class NodoIf extends NodoSentencia {

    Token tokenIf;
    NodoExpresion expresionCondicion;
    NodoSentencia sentenciaIf;
    NodoSentencia sentenciaElse; //puede ser NULL
    static int numElse;
    static int numIf;

    public NodoIf(Token tokenIf, NodoExpresion expresionCondicion, NodoSentencia sentenciaIf, NodoSentencia sentenciaElse) {
        this.tokenIf = tokenIf;
        this.expresionCondicion = expresionCondicion;
        this.sentenciaIf = sentenciaIf;
        this.sentenciaElse = sentenciaElse;
    }

    @Override
    public void chequear() throws ExcepcionSemantica {
        TipoMetodo tipoExpresionCond = expresionCondicion.chequear();
        if(tipoExpresionCond.esIgualTipo(new TipoBoolean())) {
            NodoBloque nodoBloqueIf = new NodoBloque();
            nodoBloqueIf.setOffsetVarLocalDisp();
            TablaDeSimbolos.apilarBloque(nodoBloqueIf);
            if(sentenciaIf != null)
                sentenciaIf.chequear();
            TablaDeSimbolos.desapilarBloque();

            if (sentenciaElse != null) {
                NodoBloque nodoBloqueElse = new NodoBloque();
                nodoBloqueElse.setOffsetVarLocalDisp();
                TablaDeSimbolos.apilarBloque(nodoBloqueElse);
                sentenciaElse.chequear();
                TablaDeSimbolos.desapilarBloque();
            }
        } else {
            throw new ExcepcionSemantica(tokenIf, "la expresión de la condición no es de tipo boolean");
        }
    }

    @Override
    public void generar() {
        String etiqueta_finIf = nuevaEtiquetaFinIf();

        expresionCondicion.generar();
        if(sentenciaElse == null) {
            TablaDeSimbolos.codigo.add("BF " + etiqueta_finIf + "  ; Salta a " + etiqueta_finIf + " si es falsa la condición");
            if(sentenciaIf != null)
                sentenciaIf.generar();
            TablaDeSimbolos.codigo.add(etiqueta_finIf + ": NOP");
        } else {
            String etiqueta_else = nuevaEtiquetaElse();
            TablaDeSimbolos.codigo.add("BF " + etiqueta_else + "  ; Salta a " + etiqueta_else + " si es falsa la condición");
            if(sentenciaIf != null)
                sentenciaIf.generar();
            TablaDeSimbolos.codigo.add("JUMP " + etiqueta_finIf);
            TablaDeSimbolos.codigo.add(etiqueta_else + ": NOP");
            sentenciaElse.generar();
            TablaDeSimbolos.codigo.add(etiqueta_finIf + ": NOP");
        }
    }

    private String nuevaEtiquetaElse() {
        String etiqueta = "else_" + numElse;
        numElse++;
        return etiqueta;
    }

    private String nuevaEtiquetaFinIf() {
        String etiqueta = "finIf_" + numIf;
        numIf++;
        return etiqueta;
    }
}
