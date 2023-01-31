package minijavaCompiler.filemanager;

import java.io.*;

public class GestorDeArchivo {

    private final char EOF = '\u001a';
    private int posChar;
    private int nroLineaActual;

    private String lineaActual;
    private String lineaAnterior;
    private char proxChar;
    private BufferedReader bufferedReader;

    public GestorDeArchivo(String file) throws IOException {
        posChar = 0;
        nroLineaActual = 1;

        bufferedReader = new BufferedReader(new FileReader(file));

        lineaActual = bufferedReader.readLine();
    }

    public char proximoChar() throws IOException {
        if (proxChar == '\n')
            nroLineaActual++;
        if(lineaActual != null) {
            if(posChar >= (lineaActual.length())) {
                lineaAnterior = lineaActual;
                lineaActual = bufferedReader.readLine();
                proxChar = '\n';
                posChar = 0;
            }
            else {
                proxChar = lineaActual.charAt(posChar);
                posChar++;
            }
        }
        else {
            proxChar = EOF;
            bufferedReader.close();
        }

        return proxChar;
    }

    public int getNroLineaActual() {
        return nroLineaActual;
    }

    public int getNroColumna() {
        return posChar + 1;
    }

    public String getLinea() {
        if (proxChar == '\n')
            return lineaAnterior;
        else
            return lineaActual;
    }

    public boolean esEOF(char caracter) {
        return caracter == EOF;
    }

}
