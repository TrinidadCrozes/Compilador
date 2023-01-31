package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.ExcepcionSemantica;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public class NodoAsigAsignacion extends NodoAsignacion {

    Token tokenAsig;

    public NodoAsigAsignacion(Token tokenAsig) {
        super();
        this.tokenAsig = tokenAsig;
    }

    @Override
    public void chequear() throws ExcepcionSemantica {
        TipoMetodo tipoAcceso = acceso.chequear();

        if(!acceso.esAsignable()) {
            throw new ExcepcionSemantica(tokenAsig, "no es posible realizar la asignación porque no termina en una variable");
        }

        TipoMetodo tipoExpresion = expresion.chequear();

        if(!tipoExpresion.esSubtipo(tipoAcceso)) {
            throw new ExcepcionSemantica(tokenAsig, "el tipo de la expresion de la derecha no conforma con el tipo del acceso");
        }

    }

    @Override
    public void generar() {
        expresion.generar(); //Se generó y está su valor en el tope de la pila
        acceso.setEsLadoIzqAsig();
        acceso.generar();
    }
}
