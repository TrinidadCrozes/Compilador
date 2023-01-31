package minijavaCompiler.semantic.nodosAST.nodosSentencia.NodosBloquePredefinidos;

import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoBloque;

public class NodoBloquePrintC extends NodoBloque {

    public NodoBloquePrintC(){
        super();
    }

    public void generar() {
        TablaDeSimbolos.codigo.add("LOAD 3  ; Apila el parámetro");
        TablaDeSimbolos.codigo.add("CPRINT  ; Imprime el char en el tope de la pila");
    }
}
