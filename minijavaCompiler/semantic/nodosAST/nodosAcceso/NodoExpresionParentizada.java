package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.ExcepcionSemantica;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;

public class NodoExpresionParentizada extends NodoPrimario {

    NodoExpresion expresion;

    public NodoExpresionParentizada(Token token, NodoExpresion expresion) {
        this.token = token;
        this.expresion = expresion;
    }

    @Override
    public TipoMetodo chequear() throws ExcepcionSemantica {
        if(encadenado == null) {
            return expresion.chequear();
        } else {
            return encadenado.chequear(expresion.chequear());
        }
    }

    @Override
    public boolean esAsignable() {
        if (encadenado == null) {
            return false;
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
        expresion.generar();

        if(encadenado != null) {
            encadenado.setEsLadoIzqAsig(this.esLadoIzqAsig);
            encadenado.generar();
        }
    }
}
