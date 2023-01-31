package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public class NodoAccesoThis extends NodoPrimario {

    public NodoAccesoThis(Token token) {
        this.token = token;
    }


    @Override
    public TipoMetodo chequear() throws ExcepcionSemantica {
        MetodoOConstructor met = TablaDeSimbolos.metodoOConstructorActual;
        if(met.isMetEstatico()) {
            throw new ExcepcionSemantica(token, "un método estático no puede tener un acceso this");
        } else {
            if(encadenado == null) {
                return TablaDeSimbolos.claseActual.getTipo();
            } else {
                return encadenado.chequear(TablaDeSimbolos.claseActual.getTipo());
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
        TablaDeSimbolos.codigo.add("LOAD 3  ; Cargo this");

        if(encadenado != null) {
            encadenado.setEsLadoIzqAsig(this.esLadoIzqAsig);
            encadenado.generar();
        }
    }
}
