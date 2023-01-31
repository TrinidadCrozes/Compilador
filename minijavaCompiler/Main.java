package minijavaCompiler;

import minijavaCompiler.filemanager.GestorDeArchivo;
import minijavaCompiler.lexical.AnalizadorLexico;
import minijavaCompiler.lexical.ExcepcionLexica;
import minijavaCompiler.semantic.entidades.ExcepcionSemantica;
import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.syntactic.AnalizadorSintactico;
import minijavaCompiler.syntactic.ExcepcionSintactica;

import java.io.FileWriter;
import java.io.IOException;
import java.util.WeakHashMap;

public class Main {

    public static void main(String[]args) {
        try {
            String file = args[0];
            String file_salida = args[1];
            try {
                GestorDeArchivo gestorDeArchivo = new GestorDeArchivo(file);
                AnalizadorLexico analizadorLexico = new AnalizadorLexico(gestorDeArchivo);
                TablaDeSimbolos t = new TablaDeSimbolos();
                AnalizadorSintactico analizadorSintactico = new AnalizadorSintactico(analizadorLexico);
                t.chequearDeclaraciones();
                t.consolidar();
                t.chequearSentencias();
                t.generar();
                escribirArchivoSalida(file_salida);
                System.out.println("Compilación Exitosa \n\n[SinErrores]");
            }
            catch (ExcepcionLexica excepcionLexica) {
                System.out.println(excepcionLexica.getMessage());
            }
            catch (ExcepcionSintactica excepcionSintactica) {
                System.out.println(excepcionSintactica.getMessage());
            }
            catch (ExcepcionSemantica excepcionSemantica) {
                System.out.println(excepcionSemantica.getMessage());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void escribirArchivoSalida(String file_salida) throws IOException {
        FileWriter fileWriter = new FileWriter(file_salida);
        for(String s: TablaDeSimbolos.codigo) {
            fileWriter.write(s+"\n");
        }
        fileWriter.close();
    }

}
