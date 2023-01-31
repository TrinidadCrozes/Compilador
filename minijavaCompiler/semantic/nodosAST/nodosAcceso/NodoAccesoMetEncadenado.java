package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.tipos.TipoVoid;

import java.util.List;

public class NodoAccesoMetEncadenado extends NodoEncadenado {

    Token tokenIdMet;
    List<NodoExpresion> argsActuales;
    Metodo entradaMetodo;

    public NodoAccesoMetEncadenado(Token tokenIdMet, List<NodoExpresion> args, NodoEncadenado encadenado) {
        super(encadenado);
        this.tokenIdMet = tokenIdMet;
        this.argsActuales = args;
    }

    @Override
    public TipoMetodo chequear(TipoMetodo tipoElemIzquierdo) throws ExcepcionSemantica {
        Clase clase = TablaDeSimbolos.getClase(tipoElemIzquierdo.getNombreTipo());
        if(clase == null) {
            throw new ExcepcionSemantica(tokenIdMet, "no existe clase");
        } else {
            Metodo met = clase.existeMetodo(tokenIdMet.getLexema(), argsActuales);
            entradaMetodo = met;
            if(met == null) {
                throw new ExcepcionSemantica(tokenIdMet, "no existe método con igual id donde cada parámetro actual conforme con cada parámetro formal");
            } else {
                if(encadenado == null) {
                    return met.getTipoRetorno();
                } else {
                    return encadenado.chequear(met.getTipoRetorno());
                }
            }
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

    public boolean esLlamable() {
        if(encadenado == null) {
            return true;
        } else {
            return encadenado.esLlamable();
        }
    }

    @Override
    public void generar() {
        if(entradaMetodo.isMetEstatico()) {
            TablaDeSimbolos.codigo.add("POP  ; Elimino referencia anterior");
            if(!entradaMetodo.getTipoRetorno().esIgualTipo(new TipoVoid())) {
                TablaDeSimbolos.codigo.add("RMEM 1  ; Reservamos una locación de memoria para guardar el resultado");
            }
            for (NodoExpresion param : argsActuales) {
                param.generar();
            }
            TablaDeSimbolos.codigo.add("PUSH " + entradaMetodo.getLabel() + "  ; Apila el método " + entradaMetodo.getTokenMetodo().getTipoDeToken());
            TablaDeSimbolos.codigo.add("CALL  ; Llama al método en el tope de la pila " + entradaMetodo.getTokenMetodo().getLexema());
        } else {
            if(!entradaMetodo.getTipoRetorno().esIgualTipo(new TipoVoid())) {
                TablaDeSimbolos.codigo.add("RMEM 1  ; Reservamos una locación de memoria para guardar el resultado de " + entradaMetodo.getTokenMetodo().getLexema());
                TablaDeSimbolos.codigo.add("SWAP");
            }
            for (NodoExpresion param : argsActuales) {
                param.generar();
                TablaDeSimbolos.codigo.add("SWAP");
            }
            TablaDeSimbolos.codigo.add("DUP");
            TablaDeSimbolos.codigo.add("LOADREF 0  ; Apilo el offset de la VT en el CIR (siempre 0)");
            TablaDeSimbolos.codigo.add("LOADREF " + entradaMetodo.offset() + "  ; Apilo el offset del método " + entradaMetodo.getTokenMetodo().getLexema() + " en la VT");
            TablaDeSimbolos.codigo.add("CALL  ; Llama al método en el tope de la pila");
        }

        if(encadenado != null) {
            encadenado.setEsLadoIzqAsig(this.esLadoIzqAsig);
            encadenado.generar();
        }
    }
}
