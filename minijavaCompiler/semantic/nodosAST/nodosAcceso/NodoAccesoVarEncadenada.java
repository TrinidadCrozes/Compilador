package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.lexical.TipoDeToken;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public class NodoAccesoVarEncadenada extends NodoEncadenado {

    Token tokenIdVar;
    Atributo atributo;

    public NodoAccesoVarEncadenada(Token tokenIdVar, NodoEncadenado encadenado) {
        super(encadenado);
        this.tokenIdVar = tokenIdVar;
    }

    @Override
    public TipoMetodo chequear(TipoMetodo tipoElemIzquierdo) throws ExcepcionSemantica {
        Clase clase = TablaDeSimbolos.getClase(tipoElemIzquierdo.getNombreTipo());
        if(clase == null) {
            throw new ExcepcionSemantica(tokenIdVar, "no existe clase " + tipoElemIzquierdo.getNombreTipo());
        } else {
            if(clase.esClaseConcreta()) {
                atributo = ((ClaseConcreta)clase).getAtributo(tokenIdVar);
                if (atributo == null) {
                    throw new ExcepcionSemantica(tokenIdVar, "no es un atributo de la clase a la izquierda en el encadenado");
                } else if (atributo.getVisibilidad().equals(TipoDeToken.pcPrivate) &&
                        (!TablaDeSimbolos.claseActual.tokenClase.getLexema().equals(atributo.getTokenClaseDec().getLexema()) ||
                                !clase.tokenClase.getLexema().equals(atributo.getTokenClaseDec().getLexema()))) {
                    throw new ExcepcionSemantica(tokenIdVar, "no es un atributo público de la clase a la izquierda en el encadenado");
                }
            } else {
                throw new ExcepcionSemantica(tokenIdVar, clase.tokenClase.getLexema() + " no es una clase concreta");
            }
        }
        if (encadenado == null) {
            return atributo.getTipo();
        } else {
            return encadenado.chequear(atributo.getTipo());
        }
    }

    @Override
    public boolean esAsignable() {
        if(encadenado == null) {
            return true;
        } else {
            return encadenado.esAsignable();
        }
    }

    public boolean esLlamable() {
        if(encadenado == null) {
            return false;
        } else {
            return encadenado.esLlamable();
        }
    }

    @Override
    public void generar() {
        if(!esLadoIzqAsig || encadenado != null) {
            TablaDeSimbolos.codigo.add("LOADREF " + atributo.offset() + "  ; Apilo el valor del atributo " + atributo.getToken().getLexema());
        } else {
            TablaDeSimbolos.codigo.add("SWAP");
            TablaDeSimbolos.codigo.add("STOREREF " + atributo.offset() + "  ; Guardo el valor de la expresión en el atributo " + atributo.getToken().getLexema());
        }

        if(encadenado != null) {
            encadenado.setEsLadoIzqAsig(this.esLadoIzqAsig);
            encadenado.generar();
        }
    }
}
