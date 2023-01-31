package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.tipos.TipoVoid;

import java.util.List;

public class NodoAccesoMetodo extends NodoPrimario {

    List<NodoExpresion> argsActuales;
    Metodo entradaMetodo;

    public NodoAccesoMetodo(Token token, List<NodoExpresion> argsActuales) {
        this.token = token;
        this.argsActuales = argsActuales;
    }

    @Override
    public TipoMetodo chequear() throws ExcepcionSemantica {
        MetodoOConstructor metActual = TablaDeSimbolos.metodoOConstructorActual;
        Metodo metConParamsConformantes = TablaDeSimbolos.claseActual.existeMetodo(token.getLexema(), argsActuales);
        entradaMetodo = metConParamsConformantes;
        if(metConParamsConformantes == null) {
            throw new ExcepcionSemantica(token, "no existe método con igual id donde cada parámetro actual conforme con cada parámetro formal");
        } else {
            if(metActual.isMetEstatico() && !metConParamsConformantes.isMetEstatico()) {
                throw new ExcepcionSemantica(token, "no es posible tener una llamada a un método dinámico en un método estático");
            }
            if(encadenado == null)
                return metConParamsConformantes.getTipoRetorno();
            else
                return encadenado.chequear(metConParamsConformantes.getTipoRetorno());
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
            if(!entradaMetodo.getTipoRetorno().esIgualTipo(new TipoVoid())) {
                TablaDeSimbolos.codigo.add("RMEM 1  ; Reservamos una locación de memoria para guardar el resultado");
            }
            for (NodoExpresion param : argsActuales) {
                param.generar();
            }
            TablaDeSimbolos.codigo.add("PUSH " + entradaMetodo.getLabel() + "  ; Apila el método ");
            TablaDeSimbolos.codigo.add("CALL  ; Llama al método en el tope de la pila");
        } else {
            TablaDeSimbolos.codigo.add("LOAD 3  ; Cargo this");
            if(!entradaMetodo.getTipoRetorno().esIgualTipo(new TipoVoid())) {
                TablaDeSimbolos.codigo.add("RMEM 1  ; Reservamos una locación de memoria para guardar el resultado");
                TablaDeSimbolos.codigo.add("SWAP");
            }
            for (NodoExpresion param : argsActuales) {
                param.generar();
                TablaDeSimbolos.codigo.add("SWAP");
            }
            TablaDeSimbolos.codigo.add("DUP");
            TablaDeSimbolos.codigo.add("LOADREF 0  ; Apilo el offset de la VT en el CIR (siempre 0)");
            TablaDeSimbolos.codigo.add("LOADREF " + entradaMetodo.offset() + "  ; Apilo el offset del método " + entradaMetodo.getTokenMetodo().getLexema() + " en la VT");
            TablaDeSimbolos.codigo.add("CALL  ; Llama al método " + entradaMetodo.getTokenMetodo().getTipoDeToken() + " en el tope de la pila");
        }

        if(encadenado != null) {
            encadenado.setEsLadoIzqAsig(this.esLadoIzqAsig);
            encadenado.generar();
        }
    }
}
