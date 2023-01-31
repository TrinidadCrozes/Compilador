package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.semantic.entidades.ExcepcionSemantica;

public abstract class NodoSentencia {

    public abstract void chequear() throws ExcepcionSemantica;

    public abstract void generar();
}
