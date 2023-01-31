package minijavaCompiler.semantic.nodosAST.nodosSentencia.NodosBloquePredefinidos;

import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoBloque;

public class NodoBloqueRead extends NodoBloque {

    public NodoBloqueRead(){
        super();
    }

    public void generar() {
        TablaDeSimbolos.codigo.add("READ  ; Lee un valor entero");
        TablaDeSimbolos.codigo.add("PUSH 48  ; Por ASCII");
        TablaDeSimbolos.codigo.add("SUB");
        TablaDeSimbolos.codigo.add("STORE 3  ; Devuelve el valor entero leído, poniendo el tope de la pila en la locación reservada");
    }
}
