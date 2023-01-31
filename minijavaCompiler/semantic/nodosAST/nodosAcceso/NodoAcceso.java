package minijavaCompiler.semantic.nodosAST.nodosAcceso;

import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoOperando;

public abstract class NodoAcceso extends NodoOperando {

    protected boolean esLadoIzqAsig = false;

    public abstract boolean esAsignable();


    public abstract boolean esLlamable();

    public void setEsLadoIzqAsig() {
        esLadoIzqAsig = true;
    }
}
