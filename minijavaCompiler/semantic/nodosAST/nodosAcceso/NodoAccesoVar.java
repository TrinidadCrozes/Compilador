package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.lexical.TipoDeToken;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoBloque;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoVarLocal;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public class NodoAccesoVar extends NodoPrimario {

    EntradaVar evar;
    public NodoAccesoVar(Token token) {
        this.token = token;
    }

    @Override
    public TipoMetodo chequear() throws ExcepcionSemantica {
        MetodoOConstructor met = TablaDeSimbolos.metodoOConstructorActual;
        ClaseConcreta claseActual = (ClaseConcreta) TablaDeSimbolos.claseActual;
        TipoMetodo tipoVariable;
        NodoVarLocal varLocal = null;
        for(NodoBloque bloque: TablaDeSimbolos.pilaBloquesActuales) {
            varLocal = bloque.getVarLocal(token);
            if(varLocal != null) {
                break;
            }
        }
        if(varLocal != null) {
            tipoVariable = varLocal.getTipo();
            evar = varLocal;
        } else {
            ParametroFormal paramFormal = met.getParametro(token);
            if(paramFormal != null) {
                tipoVariable = paramFormal.getTipo();
                evar = paramFormal;
            } else {
                Atributo atributo = claseActual.getAtributo(token);
                if (atributo != null) {
                    if(atributo.getVisibilidad().equals(TipoDeToken.pcPrivate) && !atributo.getTokenClaseDec().getLexema().equals(claseActual.tokenClase.getLexema())) {
                        throw new ExcepcionSemantica(token, "no es un atributo visible en la clase actual");
                    } else {
                        if (met.isMetEstatico()) {
                            throw new ExcepcionSemantica(token, "un acceso no puede comenzar con un atributo en un método estático");
                        } else {
                            tipoVariable = atributo.getTipo();
                            evar = atributo;
                        }
                    }
                } else {
                    throw new ExcepcionSemantica(token, "no se encontró ninguna variable accesible con id " + token.getLexema());
                }
            }
        }
        if(encadenado != null)
            return encadenado.chequear(tipoVariable);
        else
            return tipoVariable;
    }

    @Override
    public boolean esAsignable() {
        if(encadenado == null) {
            return true;
        } else {
            return encadenado.esAsignable();
        }
    }

    @Override
    public boolean esLlamable() {
        if(encadenado == null) {
            return false;
        } else {
            return encadenado.esLlamable();
        }
    }

    @Override
    public void generar() {
        if(evar.esAtributo()) {
            TablaDeSimbolos.codigo.add("LOAD 3  ; Cargo this ");
            if(!esLadoIzqAsig || encadenado != null) {
                TablaDeSimbolos.codigo.add("LOADREF " + evar.offset());
            } else {
                TablaDeSimbolos.codigo.add("SWAP");
                TablaDeSimbolos.codigo.add("STOREREF " + evar.offset());
            }
        } else {
            if(!esLadoIzqAsig || encadenado != null) {
                TablaDeSimbolos.codigo.add("LOAD " + evar.offset() + " ; Apilo el valor en memoria del offset de " + evar.getToken().getLexema());
            } else {
                TablaDeSimbolos.codigo.add("STORE " + evar.offset());
            }
        }
        if(encadenado != null) {
            encadenado.setEsLadoIzqAsig(this.esLadoIzqAsig);
            encadenado.generar();
        }
    }

}
