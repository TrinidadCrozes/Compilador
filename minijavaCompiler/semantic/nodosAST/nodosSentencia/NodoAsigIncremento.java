package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.ExcepcionSemantica;
import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.tipos.TipoInt;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public class NodoAsigIncremento extends NodoAsignacion {

    Token tokenAsig;

    public NodoAsigIncremento(Token tokenAsig) {
        super();
        this.tokenAsig = tokenAsig;
    }

    @Override
    public void chequear() throws ExcepcionSemantica {
        TipoMetodo tipoAcceso = acceso.chequear();
        if(tipoAcceso.esIgualTipo(new TipoInt())) {
            if (!acceso.esAsignable()) {
                throw new ExcepcionSemantica(tokenAsig, "no es posible realizar la asignación porque no termina en una variable");
            }
            TipoMetodo tipoExpresion = expresion.chequear();
            if(!tipoExpresion.esIgualTipo(new TipoInt())) {
                throw new ExcepcionSemantica(tokenAsig, "la expresión no es de tipo entero");
            }
        } else {
            throw new ExcepcionSemantica(tokenAsig, "el acceso no es de tipo entero");
        }
    }

    @Override
    public void generar() {
        acceso.generar();
        expresion.generar();
        TablaDeSimbolos.codigo.add("ADD");
        acceso.setEsLadoIzqAsig();
        acceso.generar();
    }
}
