package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.semantic.entidades.ExcepcionSemantica;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public abstract class NodoEncadenado {

    NodoEncadenado encadenado;
    boolean esLadoIzqAsig = false;

    public NodoEncadenado(NodoEncadenado encadenado) {
        this.encadenado = encadenado;
    }

    public abstract TipoMetodo chequear(TipoMetodo t) throws ExcepcionSemantica;

    public abstract boolean esAsignable();

    public abstract boolean esLlamable();

    public abstract void generar();

    public void setEsLadoIzqAsig(boolean esLadoIzqAsig) {
        this.esLadoIzqAsig = esLadoIzqAsig;
    }
}
