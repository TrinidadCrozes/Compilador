package minijavaCompiler.semantic.nodosAST.nodosSentencia.NodosBloquePredefinidos;

import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoBloque;

public class NodoBloqueDebugPrint extends NodoBloque {

    public NodoBloqueDebugPrint(){
        super();
    }

    public void generar() {
        TablaDeSimbolos.codigo.add("LOAD 3  ; Apila el parámetro");
        TablaDeSimbolos.codigo.add("IPRINT  ; Imprime el entero en el tope de la pila");
    }
}
