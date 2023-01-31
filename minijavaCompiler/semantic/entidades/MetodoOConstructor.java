package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoBloque;
import minijavaCompiler.semantic.tipos.TipoMetodo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class MetodoOConstructor {

    Token token;
    TipoMetodo tipoRetorno;
    boolean metEstatico;
    ArrayList<ParametroFormal> parametros;
    public HashMap<String, ParametroFormal> mapParametros;
    public NodoBloque bloque;
    int offsetParamsDisp;


    public MetodoOConstructor() {}

    public TipoMetodo getTipoRetorno() {
        return tipoRetorno;
    }

    public boolean isMetEstatico() {
        return metEstatico;
    }

    abstract public void insertarParametro(ParametroFormal param) throws ExcepcionSemantica;

    abstract public void insertarBloque(NodoBloque nodoBloque);

    public ArrayList<ParametroFormal> getParametros() {
        return parametros;
    }

    public ParametroFormal getParametro(Token token) {
        if(mapParametros.get(token.getLexema()) != null) {
            return mapParametros.get(token.getLexema());
        } else {
            return null;
        }
    }

    public NodoBloque getBloque() {
        return bloque;
    }

    public void chequearSentencias() throws ExcepcionSemantica {
        TablaDeSimbolos.metodoOConstructorActual = this;
        bloque.chequear();
    }

    public boolean conformanParametros(List<NodoExpresion> argsActuales) throws ExcepcionSemantica {
        if (argsActuales.size() == parametros.size()) {
            boolean sonConformantes = true;
            int i = 0;
            for (NodoExpresion exp : argsActuales) {
                TipoMetodo tipoExp = exp.chequear();
                if (!tipoExp.esSubtipo(parametros.get(i).getTipo())) {
                    sonConformantes = false;
                    break;
                }
                i++;
            }
            if (sonConformantes) {
                return true;
            }
        }
        return false;
    }

    public Token getToken() {
        return token;
    }

    public void generar() {
        TablaDeSimbolos.metodoOConstructorActual = this;
        TablaDeSimbolos.codigo.add("\n"+ getLabel() + ": LOADFP  ; Apila el valor del registro fp");
        TablaDeSimbolos.codigo.add("LOADSP  ; Apila el valor del registro sp");
        TablaDeSimbolos.codigo.add("STOREFP  ; Almacena el tope de la pila en el registro fp");
        bloque.generar();
        TablaDeSimbolos.codigo.add("STOREFP  ; Almacena el tope de la pila en el registro fp");
        TablaDeSimbolos.codigo.add("RET " + this.getCantLiberarRetorno());
    }

    public int getCantLiberarRetorno() {
        if(metEstatico) {
            return parametros.size();
        } else {
            return parametros.size() + 1;
        }
    }

    public int getOffsetLugarRetorno() {
        if(metEstatico) {
            return 1 + parametros.size() + 2;
        } else {
            return 1 + parametros.size() + 3;
        }
    }

    public abstract String getLabel();
}
