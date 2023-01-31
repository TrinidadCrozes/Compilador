package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoMetodo;

import java.util.List;

public class NodoAccesoConstructor extends NodoPrimario {

    List<NodoExpresion> argsActuales;
    Clase claseConstructor;
    Constructor constConParamsConformantes;

    public NodoAccesoConstructor(Token token, List<NodoExpresion> argsActuales) {
        this.token = token;
        this.argsActuales = argsActuales;
    }

    @Override
    public TipoMetodo chequear() throws ExcepcionSemantica {
        claseConstructor = TablaDeSimbolos.getClase(token.getLexema());
        if(claseConstructor == null) {
            throw new ExcepcionSemantica(token, "no existe clase declarada con el nombre del constructor");
        }
        if(!claseConstructor.esClaseConcreta()) {
            throw new ExcepcionSemantica(token, "no existe clase concreta declarada con el nombre del constructor");
        }
        constConParamsConformantes = claseConstructor.existeConstructor(argsActuales);
        if(constConParamsConformantes == null) {
            throw new ExcepcionSemantica(token, "no existe constructor en la clase donde cada parámetro actual conforme con cada parámetro formal");
        } else {
            if (encadenado == null)
                return claseConstructor.getTipo();
            else
                return encadenado.chequear(claseConstructor.getTipo());
        }
    }

    @Override
    public boolean esAsignable() {
        if(encadenado == null) {
            return false;
        } else {
            return encadenado.esAsignable();
        }
    }

    @Override
    public boolean esLlamable() {
        if(encadenado == null) {
            return true;
        } else {
            return encadenado.esLlamable();
        }
    }

    @Override
    public void generar() {
        TablaDeSimbolos.codigo.add("RMEM 1  ; Reservamos memoria para el resultado del malloc (la referencia al nuevo CIR de " + claseConstructor.tokenClase.getLexema() + ")");
        TablaDeSimbolos.codigo.add("PUSH " + (claseConstructor.getCantAtributos() + 1) + "  ;  Apilo la cantidad de var de instancia del CIR de " + claseConstructor.tokenClase.getLexema() + " +1 por VT");
        TablaDeSimbolos.codigo.add("PUSH simple_malloc  ; La dirección de la rutina para alojar memoria en el heap");
        TablaDeSimbolos.codigo.add("CALL  ; Llamo a malloc");
        TablaDeSimbolos.codigo.add("DUP  ; Para no perder la referencia al nuevo CIR");
        TablaDeSimbolos.codigo.add("PUSH " + claseConstructor.getLabelVT() + "  ; Apilamos la dirección del comienzo de la VT de la clase " + claseConstructor.tokenClase.getLexema());
        TablaDeSimbolos.codigo.add("STOREREF 0  ; Guardamos la Referencia a la VT en el CIR que creamos");
        TablaDeSimbolos.codigo.add("DUP");
        for(NodoExpresion param: argsActuales) {
            param.generar();
            TablaDeSimbolos.codigo.add("SWAP");
        }
        TablaDeSimbolos.codigo.add("PUSH " + constConParamsConformantes.getLabel());
        TablaDeSimbolos.codigo.add("CALL  ; Llamo al constructor " + constConParamsConformantes.getLabel());

        if(encadenado != null) {
            encadenado.setEsLadoIzqAsig(this.esLadoIzqAsig);
            encadenado.generar();
        }
    }

}
