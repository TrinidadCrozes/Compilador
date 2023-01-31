package minijavaCompiler.lexical;

public class ExcepcionLexica extends Exception {

    public ExcepcionLexica(String lexema, String textoLinea, int nroLineaActual, int nroColumna, String mensajeDeError) {
        super(getErrorMessage(lexema, textoLinea, nroLineaActual, nroColumna, mensajeDeError));
    }

    private static String getErrorMessage(String lexema, String textoLinea, int nroLineaActual, int nroColumna, String mensajeDeError) {
        String detalle = "Detalle: ";
        String mensajeDetallado = detalle + textoLinea;
        String pointer = "";
        for(int i = 0; i < (detalle.length() + nroColumna - 1); i++) {
            if (mensajeDetallado.charAt(i) == '\t')
                pointer += "\t";
            else
                pointer += " ";
        }
        pointer += "^";

        return "Error Léxico en línea " + nroLineaActual + " y columna " + nroColumna + ": " + lexema + " " + mensajeDeError + "\n" + mensajeDetallado + "\n" + pointer + "\n" + "[Error:" + lexema + "|" + nroLineaActual + "]" ;
    }
}
