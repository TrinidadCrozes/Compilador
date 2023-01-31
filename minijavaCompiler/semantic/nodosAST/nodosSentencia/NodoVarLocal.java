package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoMetodo;

import java.util.List;

public class NodoVarLocal extends NodoSentencia implements EntradaVar {

    Token tokenVarLocal;
    Token tokenAsignacion;
    NodoExpresion expresionVarLocal;
    TipoMetodo tipoVarLocal;
    int offset;

    public NodoVarLocal(Token tokenVarLocal, Token tokenAsignacion, NodoExpresion expresion) {
        this.tokenVarLocal = tokenVarLocal;
        this.tokenAsignacion = tokenAsignacion;
        this.expresionVarLocal = expresion;
        this.offset = 1;
    }

    public TipoMetodo getTipo() {
        return tipoVarLocal;
    }

    @Override
    public void chequear() throws ExcepcionSemantica {
        MetodoOConstructor met = TablaDeSimbolos.metodoOConstructorActual;
        if(met.getParametro(tokenVarLocal) != null) {
            throw new ExcepcionSemantica(tokenVarLocal, "el id de la var local" + tokenVarLocal.getLexema() + " es un id de un parámetro del método que contiene la declaración");
        } else {
            List<NodoBloque> bloques = TablaDeSimbolos.pilaBloquesActuales;
            for(NodoBloque bloque: bloques) {
                if(bloque.getVarLocal(tokenVarLocal) != null) {
                    throw new ExcepcionSemantica(tokenVarLocal, "el id de la var local " + tokenVarLocal.getLexema() + " es un id de una var local anteriormente declarada");
                }
            }
            TipoMetodo tipoExp = expresionVarLocal.chequear();
            if (tipoExp.getNombreTipo().equals("null")) {
                throw new ExcepcionSemantica(tokenAsignacion, "el tipo de la expresión de la var local " + tokenVarLocal.getLexema() + " es null");
            } else if(tipoExp.getNombreTipo().equals("void")){
                throw new ExcepcionSemantica(tokenAsignacion, "el tipo de la expresión de la var local " + tokenVarLocal.getLexema() + " es un retorno void");
            } else {
                this.tipoVarLocal = tipoExp;
            }
            TablaDeSimbolos.pilaBloquesActuales.get(0).insertarVarLocal(this);
        }
    }

    @Override
    public void generar() {
        TablaDeSimbolos.codigo.add("RMEM 1  ; Reservo un lugar en memoria para almacenar el valor de la var local " + tokenVarLocal.getLexema());
        if(expresionVarLocal != null) {
            expresionVarLocal.generar();
            TablaDeSimbolos.codigo.add("STORE " + this.offset + "  ; Almaceno el valor de la expresión de la var local " + tokenVarLocal.getLexema());
        }
    }

    @Override
    public Token getToken() {
        return tokenVarLocal;
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
        return offset != 1;
    }
}
