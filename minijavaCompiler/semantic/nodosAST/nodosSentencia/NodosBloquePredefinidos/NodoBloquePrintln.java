package minijavaCompiler.semantic.nodosAST.nodosSentencia.NodosBloquePredefinidos;

import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoBloque;

public class NodoBloquePrintln extends NodoBloque {

    public NodoBloquePrintln(){
        super();
    }

    public void generar() {
        TablaDeSimbolos.codigo.add("PRNLN  ; Imprime el caracter de nueva línea");
    }
}
