package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.tipos.TipoVoid;

import java.util.List;

public class NodoAccesoMetEstatico extends NodoPrimario {

    Token tokenClase;
    List<NodoExpresion> argsActuales;
    Metodo metConParamsConformantes;

    public NodoAccesoMetEstatico(Token tokenClase, Token tokenMetVar, List<NodoExpresion> argsActuales) {
        this.token = tokenMetVar;
        this.tokenClase = tokenClase;
        this.argsActuales = argsActuales;
    }

    @Override
    public TipoMetodo chequear() throws ExcepcionSemantica {
        Clase claseDer = TablaDeSimbolos.getClase(tokenClase.getLexema());
        if(claseDer == null) {
            throw new ExcepcionSemantica(tokenClase, "no existe la clase " + tokenClase.getLexema());
        } else {
            if(claseDer.esClaseConcreta()) {
                metConParamsConformantes = claseDer.existeMetodo(token.getLexema(), argsActuales);
                if (metConParamsConformantes == null) {
                    throw new ExcepcionSemantica(token, "no existe método con igual id donde cada parámetro actual conforme con cada parámetro formal");
                } else {
                    if(metConParamsConformantes.isMetEstatico()) {
                        if (encadenado == null)
                            return metConParamsConformantes.getTipoRetorno();
                        else
                            return encadenado.chequear(metConParamsConformantes.getTipoRetorno());
                    } else {
                        throw new ExcepcionSemantica(token, metConParamsConformantes.getToken().getLexema() + " no es un método estático");
                    }
                }
            } else {
                throw new ExcepcionSemantica(tokenClase, tokenClase.getLexema() + " no es una clase concreta");
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
        if(!metConParamsConformantes.getTipoRetorno().esIgualTipo(new TipoVoid())) {
            TablaDeSimbolos.codigo.add("RMEM 1  ; Reservamos una locación de memoria para guardar el resultado");
        }
        for (NodoExpresion param : argsActuales) {
            param.generar();
        }
        TablaDeSimbolos.codigo.add("PUSH " + metConParamsConformantes.getLabel() + "  ; Apila el método ");
        TablaDeSimbolos.codigo.add("CALL  ; Llama al método en el tope de la pila");

        if(encadenado != null) {
            encadenado.setEsLadoIzqAsig(this.esLadoIzqAsig);
            encadenado.generar();
        }
    }
}
