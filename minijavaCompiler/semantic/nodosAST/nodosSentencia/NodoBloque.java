package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.ExcepcionSemantica;
import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.nodosAST.nodosAcceso.NodoAcceso;
import minijavaCompiler.semantic.nodosAST.nodosAcceso.NodoAccesoVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NodoBloque extends NodoSentencia {

    List<NodoSentencia> listaSentencias;
    HashMap<String,NodoVarLocal> variablesLocales;
    int offsetVarLocalDisp;


    public NodoBloque() {
        this.listaSentencias = new ArrayList<>();
        this.variablesLocales = new HashMap<>();
        this.offsetVarLocalDisp = 0;
    }

    public void insertarSentencia(NodoSentencia nodoSentencia) {
        this.listaSentencias.add(nodoSentencia);
    }

    public void insertarVarLocal(NodoVarLocal nodoVarLocal) {
        nodoVarLocal.setOffset(offsetVarLocalDisp);
        offsetVarLocalDisp--;
        this.variablesLocales.put(nodoVarLocal.tokenVarLocal.getLexema(), nodoVarLocal);
    }

    @Override
    public void chequear() throws ExcepcionSemantica {
        this.setOffsetVarLocalDisp();

        TablaDeSimbolos.apilarBloque(this);
        for(NodoSentencia sentencia : listaSentencias) {
            if(sentencia != null)
                sentencia.chequear();
        }
        TablaDeSimbolos.desapilarBloque();
    }

    @Override
    public void generar() {
        TablaDeSimbolos.apilarBloque(this);
        for(NodoSentencia sentencia : listaSentencias) {
            if(sentencia != null)
                sentencia.generar();
        }
        TablaDeSimbolos.desapilarBloque();

        liberarMemVarLocales();
    }

    public void setOffsetVarLocalDisp() {
        if(TablaDeSimbolos.pilaBloquesActuales.size() > 0) {
            this.offsetVarLocalDisp = TablaDeSimbolos.pilaBloquesActuales.get(0).getOffsetVarLocalDisp();
        } else {
            this.offsetVarLocalDisp = 0;
        }
    }

    public int getOffsetVarLocalDisp() {
        return offsetVarLocalDisp;
    }

    public NodoVarLocal getVarLocal(Token token) {
        return variablesLocales.get(token.getLexema());
    }

    private void liberarMemVarLocales() {
        TablaDeSimbolos.codigo.add("FMEM " + this.variablesLocales.size());
    }

    public int getCantVarLocalesUsadas() {
        return offsetVarLocalDisp * -1;
    }
}

