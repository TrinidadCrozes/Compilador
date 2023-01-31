package minijavaCompiler.semantic.nodosAST.nodosExpresion;

import minijavaCompiler.semantic.entidades.ExcepcionSemantica;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public abstract class NodoExpresion {

    public abstract TipoMetodo chequear() throws ExcepcionSemantica;

    public abstract void generar();
}
