package minijavaCompiler.lexical;

import minijavaCompiler.filemanager.GestorDeArchivo;

import java.io.IOException;
import java.util.HashMap;

public class AnalizadorLexico {

    String lexema;
    char charActual;
    int nroColumnaInicial;
    GestorDeArchivo gestorDeFuente;
    PalabrasClave palabrasClave;
    private String primeraLineaComentario;
    private int nroPrimeraLineaComentario;

    public AnalizadorLexico(GestorDeArchivo gestorDeFuente) throws IOException {
        this.gestorDeFuente = gestorDeFuente;
        palabrasClave = new PalabrasClave();
        actualizarCharActual();
    }

    public Token nextToken() throws ExcepcionLexica, IOException {
        lexema = "";
        return e0();
    }

    private void actualizarLexema() {
        lexema = lexema + charActual;
    }

    public void actualizarCharActual() throws IOException {
        charActual = gestorDeFuente.proximoChar();
    }

    private Token e0() throws IOException, ExcepcionLexica {
        nroColumnaInicial = gestorDeFuente.getNroColumna() - 1;

        if(Character.isUpperCase(charActual)) {
            actualizarLexema();
            actualizarCharActual();
            return e1();
        }
        else if(Character.isLowerCase(charActual)) {
            actualizarLexema();
            actualizarCharActual();
            return e2();
        }
        else if(Character.isDigit(charActual)) {
            actualizarLexema();
            actualizarCharActual();
            return e3();
        }
        else if(charActual == '\'') {
            actualizarLexema();
            actualizarCharActual();
            return e4();
        }
        else if(charActual == '"') {
            actualizarLexema();
            actualizarCharActual();
            return e8();
        }
        else if(charActual == '(') {
            actualizarLexema();
            actualizarCharActual();
            return e11();
        }
        else if(charActual == ')') {
            actualizarLexema();
            actualizarCharActual();
            return e12();
        }
        else if(charActual == '{') {
            actualizarLexema();
            actualizarCharActual();
            return e13();
        }
        else if(charActual == '}') {
            actualizarLexema();
            actualizarCharActual();
            return e14();
        }
        else if(charActual == ';') {
            actualizarLexema();
            actualizarCharActual();
            return e15();
        }
        else if(charActual == ',') {
            actualizarLexema();
            actualizarCharActual();
            return e16();
        }
        else if(charActual == '.') {
            actualizarLexema();
            actualizarCharActual();
            return e17();
        }
        else if(charActual == '+') {
            actualizarLexema();
            actualizarCharActual();
            return e18();
        }
        else if(charActual == '-') {
            actualizarLexema();
            actualizarCharActual();
            return e20();
        }
        else if(charActual == '*') {
            actualizarLexema();
            actualizarCharActual();
            return e22();
        }
        else if(charActual == '/') {
            actualizarLexema();
            actualizarCharActual();
            return e23();
        }
        else if(charActual == '%') {
            actualizarLexema();
            actualizarCharActual();
            return e24();
        }
        else if(charActual == '=') {
            actualizarLexema();
            actualizarCharActual();
            return e25();
        }
        else if(charActual == '!') {
            actualizarLexema();
            actualizarCharActual();
            return e27();
        }
        else if(charActual == '<') {
            actualizarLexema();
            actualizarCharActual();
            return e29();
        }
        else if(charActual == '>') {
            actualizarLexema();
            actualizarCharActual();
            return e31();
        }
        else if(charActual == '|') {
            actualizarLexema();
            actualizarCharActual();
            return e33();
        }
        else if(charActual == '&') {
            actualizarLexema();
            actualizarCharActual();
            return e35();
        }
        else if(Character.isWhitespace(charActual)) {
            if (charActual == '\n')
                nroColumnaInicial = 1;
            else
                nroColumnaInicial++;
            actualizarCharActual();
            return e0();
        }
        else if (gestorDeFuente.esEOF(charActual)) {
            return e40();
        }
        else {
            actualizarLexema();
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial,"símbolo inválido");
        }
    }

    //Identificador clases
    private Token e1() throws IOException {
        if(Character.isLetter(charActual) || Character.isDigit(charActual) || charActual == '_') {
            actualizarLexema();
            actualizarCharActual();
            return e1();
        }
        else {
            return new Token(TipoDeToken.idClase, lexema, gestorDeFuente.getNroLineaActual());
        }
    }

    //Identificador métodos y variables
    private Token e2() throws IOException {
        if(Character.isLetter(charActual) || Character.isDigit(charActual) || charActual == '_') {
            actualizarLexema();
            actualizarCharActual();
            return e2();
        }
        else {
            TipoDeToken tokenPalabraClave = palabrasClave.getPalabraClave(lexema);
            if(tokenPalabraClave == null) {
                return new Token(TipoDeToken.idMetVar, lexema, gestorDeFuente.getNroLineaActual());
            }
            else {
                return new Token(tokenPalabraClave, lexema, gestorDeFuente.getNroLineaActual());
            }
        }
    }

    //Literal Entero
    private Token e3() throws IOException, ExcepcionLexica {
        if(Character.isDigit(charActual)) {
            actualizarLexema();
            actualizarCharActual();
            if(lexema.length() == 9 && Character.isDigit(charActual)) {
                throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), gestorDeFuente.getNroColumna() - 1, "literal entero inválido");
            } else
                return e3();
        } else if (lexema.length() <= 9) {
            return new Token(TipoDeToken.litInt, lexema, gestorDeFuente.getNroLineaActual());
        } else {
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "literal entero inválido");
        }
    }

    //Literal Char
    private Token e4() throws IOException, ExcepcionLexica {
        if (charActual == '\\') {
            actualizarLexema();
            actualizarCharActual();
            return e6();
        } else if (charActual == '\n' || charActual == '\'' || gestorDeFuente.esEOF(charActual)) {
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "literal caracter inválido");
        } else {
            actualizarLexema();
            actualizarCharActual();
            return e5();
        }
    }

    private Token e5() throws IOException, ExcepcionLexica {
        if (charActual == '\'') {
            actualizarLexema();
            actualizarCharActual();
            return e7();
        } else {
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "literal caracter inválido");
        }
    }

    private Token e6() throws IOException, ExcepcionLexica {
        if(charActual == 'u') {
            actualizarLexema();
            actualizarCharActual();
            return e41();
        } else if (charActual == '\n' || gestorDeFuente.esEOF(charActual)) {
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "literal caracter inválido");
        } else {
            actualizarLexema();
            actualizarCharActual();
            return e5();
        }
    }

    private Token e7() {
        return new Token(TipoDeToken.litChar, lexema, gestorDeFuente.getNroLineaActual());
    }

    //Literal String
    private Token e8() throws IOException, ExcepcionLexica {
        if (charActual == '\n' || gestorDeFuente.esEOF(charActual)) {
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "literal string inválido");
        } else if (charActual == '\\') {
            actualizarLexema();
            actualizarCharActual();
            return e9();
        } else if (charActual == '\"') {
            actualizarLexema();
            actualizarCharActual();
            return e10();
        } else {
            actualizarLexema();
            actualizarCharActual();
            return e8();
        }
    }

    private Token e9() throws IOException, ExcepcionLexica {
        if (charActual == '\\') {
            actualizarLexema();
            actualizarCharActual();
            return e9();
        } else if (charActual == '\n' || gestorDeFuente.esEOF(charActual)){
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "literal string inválido");
        } else {
            actualizarLexema();
            actualizarCharActual();
            return e8();
        }
    }

    private Token e10() {
        return new Token(TipoDeToken.litString, lexema, gestorDeFuente.getNroLineaActual());
    }

    //Puntuación
    private Token e11() {
        return new Token(TipoDeToken.puntParentesisIzq, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e12() {
        return new Token(TipoDeToken.puntParentesisDer, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e13() {
        return new Token(TipoDeToken.puntLlaveIzq, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e14() {
        return new Token(TipoDeToken.puntLlaveDer, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e15() {
        return new Token(TipoDeToken.puntPuntoYComa, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e16() {
        return new Token(TipoDeToken.puntComa, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e17() {
        return new Token(TipoDeToken.puntPunto, lexema, gestorDeFuente.getNroLineaActual());
    }

    //Operadores y Asignación
    private Token e18() throws IOException {
        if (charActual == '=') {
            actualizarLexema();
            actualizarCharActual();
            return e19();
        }
        else {
            return new Token(TipoDeToken.opSuma, lexema, gestorDeFuente.getNroLineaActual());
        }
    }

    private Token e19() {
        return new Token(TipoDeToken.asigIncremento, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e20() throws IOException {
        if (charActual == '=') {
            actualizarLexema();
            actualizarCharActual();
            return e21();
        } else {
            return new Token(TipoDeToken.opResta, lexema, gestorDeFuente.getNroLineaActual());
        }
    }

    private Token e21() {
        return new Token(TipoDeToken.asigDecremento, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e22() {
        return new Token(TipoDeToken.opProducto, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e23() throws IOException, ExcepcionLexica {
        if (charActual == '*') {
            primeraLineaComentario = gestorDeFuente.getLinea();
            nroPrimeraLineaComentario = gestorDeFuente.getNroLineaActual();
            actualizarCharActual();
            return e38();
        } else if (charActual == '/') {
            primeraLineaComentario = gestorDeFuente.getLinea();
            nroPrimeraLineaComentario = gestorDeFuente.getNroLineaActual();
            actualizarCharActual();
            return e37();
        } else {
            return new Token(TipoDeToken.opDivision, lexema, gestorDeFuente.getNroLineaActual());
        }
    }

    private Token e24() {
        return new Token(TipoDeToken.opModulo, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e25() throws IOException {
        if (charActual == '=') {
            actualizarLexema();
            actualizarCharActual();
            return e26();
        } else {
            return new Token(TipoDeToken.asigAsignacion, lexema, gestorDeFuente.getNroLineaActual());
        }
    }

    private Token e26() {
        return new Token(TipoDeToken.opIgual, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e27() throws IOException {
        if (charActual == '=') {
            actualizarLexema();
            actualizarCharActual();
            return e28();
        } else {
            return new Token(TipoDeToken.opNot, lexema, gestorDeFuente.getNroLineaActual());
        }
    }

    private Token e28() {
        return new Token(TipoDeToken.opDistinto, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e29() throws IOException {
        if (charActual == '=') {
            actualizarLexema();
            actualizarCharActual();
            return e30();
        } else {
            return new Token(TipoDeToken.opMenor, lexema, gestorDeFuente.getNroLineaActual());
        }
    }

    private Token e30() {
        return new Token(TipoDeToken.opMenorIgual, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e31() throws IOException {
        if (charActual == '=') {
            actualizarLexema();
            actualizarCharActual();
            return e32();
        } else {
            return new Token(TipoDeToken.opMayor, lexema, gestorDeFuente.getNroLineaActual());
        }
    }

    private Token e32() {
        return new Token(TipoDeToken.opMayorIgual, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e33() throws IOException, ExcepcionLexica {
        if (charActual == '|') {
            actualizarLexema();
            actualizarCharActual();
            return e34();
        } else {
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "operador incompleto");
        }
    }

    private Token e34() {
        return new Token(TipoDeToken.opOr, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e35() throws IOException, ExcepcionLexica {
        if (charActual == '&') {
            actualizarLexema();
            actualizarCharActual();
            return e36();
        } else {
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "operador incompleto");
        }
    }

    private Token e36() {
        return new Token(TipoDeToken.opAnd, lexema, gestorDeFuente.getNroLineaActual());
    }

    //Comentarios
    private Token e37() throws IOException, ExcepcionLexica {
        if (charActual == '\n') {
            lexema = "";
            actualizarCharActual();
            return e0();
        } else if (gestorDeFuente.esEOF(charActual)) {
            lexema = "";
            return e40();
        } else {
            lexema = "";
            actualizarCharActual();
            return e37();
        }
    }

    private Token e38() throws IOException, ExcepcionLexica {
        if (gestorDeFuente.esEOF(charActual)) {
            throw new ExcepcionLexica(lexema, primeraLineaComentario, nroPrimeraLineaComentario, nroColumnaInicial, "comentario multilinea sin cerrar");
        } else if (charActual == '*') {
            lexema = "";
            actualizarCharActual();
            return e39();
        } else {
            lexema = "";
            actualizarCharActual();
            return e38();
        }
    }

    private Token e39() throws ExcepcionLexica, IOException {
        if (charActual == '/') {
            lexema = "";
            actualizarCharActual();
            return e0();
        } else if (charActual == '*') {
            lexema = "";
            actualizarCharActual();
            return e39();
        } else if (gestorDeFuente.esEOF(charActual)) {
            throw new ExcepcionLexica(lexema, primeraLineaComentario, nroPrimeraLineaComentario, nroColumnaInicial, "comentario multilinea sin cerrar");
        } else {
            lexema = "";
            actualizarCharActual();
            return e38();
        }
    }

    private Token e40() {
        return new Token(TipoDeToken.EOF, lexema, gestorDeFuente.getNroLineaActual());
    }

    private Token e41() throws ExcepcionLexica, IOException {
        int cantCharUnicode = 0;
        do {
            if (charActual >= 'A' && charActual <= 'F') {
                cantCharUnicode++;
                actualizarLexema();
                actualizarCharActual();
            } else if(charActual >= 'a' && charActual <= 'f') {
                cantCharUnicode++;
                actualizarLexema();
                actualizarCharActual();
            } else if(charActual >= '0' && charActual <= '9') {
                cantCharUnicode++;
                actualizarLexema();
                actualizarCharActual();
            } else if(charActual == '\'' && cantCharUnicode == 0) {
                actualizarLexema();
                actualizarCharActual();
                return new Token(TipoDeToken.litChar, lexema, gestorDeFuente.getNroLineaActual());
            } else {
                throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "unicode mal formado");
            }
        } while(cantCharUnicode < 4 && charActual != '\'');

        if(cantCharUnicode == 4 && charActual == '\'') {
            actualizarLexema();
            actualizarCharActual();
            return new Token(TipoDeToken.litChar, lexema, gestorDeFuente.getNroLineaActual());
        } else {
            throw new ExcepcionLexica(lexema, gestorDeFuente.getLinea(), gestorDeFuente.getNroLineaActual(), nroColumnaInicial, "unicode mal formado");
        }
    }

}
