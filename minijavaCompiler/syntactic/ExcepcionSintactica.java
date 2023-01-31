package minijavaCompiler.syntactic;

import minijavaCompiler.lexical.TipoDeToken;
import minijavaCompiler.lexical.Token;

public class ExcepcionSintactica extends Exception {

    public ExcepcionSintactica(Token token, String text) {
        super(getErrorMessage(token, text));
    }

    private static String getErrorMessage(Token token, String text) {
        String explicacionError = "Error Sintactico en linea " + token.getNroLinea() + ": se esperaba " + text + " se encontro " + token.getLexema();
        String codigoError = "[Error:" + token.getLexema() + "|" + token.getNroLinea() + "]";
        return explicacionError + "\n\n" + codigoError;
    }

}
