package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.Token;

public class ExcepcionSemantica extends Exception {

    public ExcepcionSemantica(Token token, String text) {
        super(getErrorMessage(token, text));
    }

    private static String getErrorMessage(Token token, String text) {
        String explicacionError = "Error Semantico en linea " + token.getNroLinea() + ": " + text;
        String codigoError = "[Error:" + token.getLexema() + "|" + token.getNroLinea() + "]";
        return explicacionError + "\n\n" + codigoError;
    }
}
