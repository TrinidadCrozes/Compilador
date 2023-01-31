package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.semantic.nodosAST.nodosAcceso.NodoAcceso;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;

public abstract class NodoAsignacion extends NodoSentencia {

    NodoAcceso acceso;
    NodoExpresion expresion;

    public NodoAsignacion() {

    }

    public void setAcceso(NodoAcceso acceso) {
        this.acceso = acceso;
    }

    public void setExpresion(NodoExpresion expresion) {
        this.expresion = expresion;
    }

}
