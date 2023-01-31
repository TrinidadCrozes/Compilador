package minijavaCompiler.semantic.nodosAST.nodosAcceso;

public abstract class NodoPrimario extends NodoAcceso {

    NodoEncadenado encadenado;

    public void setEncadenado(NodoEncadenado encadenado) {
        this.encadenado = encadenado;
    }

}
