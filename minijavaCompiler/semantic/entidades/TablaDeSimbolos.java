package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.TipoDeToken;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoBloque;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodosBloquePredefinidos.*;
import minijavaCompiler.semantic.tipos.*;

import javax.swing.text.TabableView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class TablaDeSimbolos {

    public static Clase claseActual;
    public static MetodoOConstructor metodoOConstructorActual;
    public static List<NodoBloque> pilaBloquesActuales;
    public static HashMap<String, Clase> clases;
    public static List<String> codigo;
    public static Metodo metodoMain;


    public TablaDeSimbolos() throws ExcepcionSemantica {
        clases = new HashMap<>();
        pilaBloquesActuales = new ArrayList<>();
        codigo = new ArrayList<>();
        crearClasesPredefinidas();
    }

    public static Clase getClase(String nombreClase) {
        return clases.get(nombreClase);
    }

    private void crearClasesPredefinidas() throws ExcepcionSemantica {
        crearClaseObject();
        crearClaseString();
        crearClaseSystem();
    }

    private void crearClaseObject() throws ExcepcionSemantica {
        Token tokenClase = new Token(TipoDeToken.idClase,"Object", 0);
        ClaseConcreta claseObject = new ClaseConcreta(tokenClase);
        claseObject.verificadoHerenciaCircular = true;
        Token tokenMet = new Token(TipoDeToken.idMetVar, "debugPrint", 0);
        Metodo metDebugPrint = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        Token tokenParam = new Token(TipoDeToken.idMetVar, "i", 0);
        metDebugPrint.insertarParametro(new ParametroFormal(tokenParam, new TipoInt()));
        metDebugPrint.insertarBloque(new NodoBloqueDebugPrint());
        claseObject.insertarConstructor(new Constructor(tokenClase));
        claseObject.insertarMetodo(metDebugPrint);
        TablaDeSimbolos.insertarClase(claseObject);
    }

    private void crearClaseString() throws ExcepcionSemantica {
        Token tokenClase = new Token(TipoDeToken.idClase, "String", 0);
        Token tokenAncestro = new Token(TipoDeToken.idClase, "Object", 0);
        ClaseConcreta claseString = new ClaseConcreta(tokenClase);
        claseString.insertarConstructor(new Constructor(tokenClase));
        claseString.setHeredaDe(tokenAncestro);
        TablaDeSimbolos.insertarClase(claseString);
    }

    private void crearClaseSystem() throws ExcepcionSemantica {
        Token tokenClase = new Token(TipoDeToken.idClase, "System", 0);
        Token tokenAncestro = new Token(TipoDeToken.idClase, "Object", 0);
        ClaseConcreta claseSystem = new ClaseConcreta(tokenClase);
        claseSystem.insertarConstructor(new Constructor(tokenClase));
        claseSystem.setHeredaDe(tokenAncestro);
        //static int read()
        Token tokenMet = new Token(TipoDeToken.idMetVar, "read", 0);
        Metodo met = new Metodo(tokenMet, true, new TipoInt(), tokenClase);
        met.insertarBloque(new NodoBloqueRead()); //Bloque
        claseSystem.insertarMetodo(met);
        //static void printB(boolean b)
        tokenMet = new Token(TipoDeToken.idMetVar, "printB", 0);
        met = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        ParametroFormal param = new ParametroFormal(new Token(TipoDeToken.idMetVar, "b", 0), new TipoBoolean());
        met.insertarParametro(param);
        met.insertarBloque(new NodoBloquePrintB()); //Bloque
        claseSystem.insertarMetodo(met);
        //static void printC(char c)
        tokenMet = new Token(TipoDeToken.idMetVar, "printC", 0);
        met = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        param = new ParametroFormal(new Token(TipoDeToken.idMetVar, "c", 0), new TipoChar());
        met.insertarParametro(param);
        met.insertarBloque(new NodoBloquePrintC()); //Bloque
        claseSystem.insertarMetodo(met);
        //static void printI(int i)
        tokenMet = new Token(TipoDeToken.idMetVar, "printI", 0);
        met = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        param = new ParametroFormal(new Token(TipoDeToken.idMetVar, "i", 0), new TipoInt());
        met.insertarParametro(param);
        met.insertarBloque(new NodoBloquePrintI()); //Bloque
        claseSystem.insertarMetodo(met);
        //static void printS(String s)
        tokenMet = new Token(TipoDeToken.idMetVar, "printS", 0);
        met = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        Token tipoString = new Token(TipoDeToken.idClase, "String", 0);
        param = new ParametroFormal(new Token(TipoDeToken.idMetVar, "s", 0), new TipoClase(tipoString));
        met.insertarParametro(param);
        met.insertarBloque(new NodoBloquePrintS()); //Bloque
        claseSystem.insertarMetodo(met);
        //static void println()
        tokenMet = new Token(TipoDeToken.idMetVar, "println", 0);
        met = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        met.insertarBloque(new NodoBloquePrintln()); //Bloque
        claseSystem.insertarMetodo(met);
        //static void printBln(boolean b)
        tokenMet = new Token(TipoDeToken.idMetVar, "printBln", 0);
        met = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        param = new ParametroFormal(new Token(TipoDeToken.idMetVar, "b", 0), new TipoBoolean());
        met.insertarParametro(param);
        met.insertarBloque(new NodoBloquePrintBln()); //Bloque
        claseSystem.insertarMetodo(met);
        //static void printCln(char c)
        tokenMet = new Token(TipoDeToken.idMetVar, "printCln", 0);
        met = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        param = new ParametroFormal(new Token(TipoDeToken.idMetVar, "c", 0), new TipoChar());
        met.insertarParametro(param);
        met.insertarBloque(new NodoBloquePrintCln()); //Bloque
        claseSystem.insertarMetodo(met);
        //static void printIln(int i)
        tokenMet = new Token(TipoDeToken.idMetVar, "printIln", 0);
        met = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        param = new ParametroFormal(new Token(TipoDeToken.idMetVar, "i", 0), new TipoInt());
        met.insertarParametro(param);
        met.insertarBloque(new NodoBloquePrintIln()); //Bloque
        claseSystem.insertarMetodo(met);
        //static void printSln(String s)
        tokenMet = new Token(TipoDeToken.idMetVar, "printSln", 0);
        met = new Metodo(tokenMet, true, new TipoVoid(), tokenClase);
        param = new ParametroFormal(new Token(TipoDeToken.idMetVar, "s", 0), new TipoClase(tipoString));
        met.insertarParametro(param);
        met.insertarBloque(new NodoBloquePrintSln()); //Bloque
        claseSystem.insertarMetodo(met);

        TablaDeSimbolos.insertarClase(claseSystem);
    }

    public static void chequearDeclaraciones() throws ExcepcionSemantica {
        for(Clase clase : clases.values()) {
            clase.chequearDeclaraciones();
        }
        chequearClaseConMain();
    }

    private static void chequearClaseConMain() throws ExcepcionSemantica {
        Token tokenMain = new Token(TipoDeToken.idMetVar, "main", 0);
        Metodo metMain = new Metodo(tokenMain, true, new TipoVoid(), tokenMain);
        boolean existeMainDec = false;
        for(Clase clase: clases.values()) {
            Metodo metMainDec = clase.getMetodoMismaSignatura(metMain);
            //nunca va a estar declarado en una interface porque es static
            if((metMainDec != null) && existeMainDec) {
                throw new ExcepcionSemantica(metMainDec.getTokenMetodo(), "hay más de un método main declarado");
            }
            if(metMainDec != null) {
                existeMainDec = true;
                metodoMain = metMainDec;
            }
        }
        if(!existeMainDec) {
            throw new ExcepcionSemantica(metMain.getTokenMetodo(), "no existe ningún método main declarado");
        }
    }

    public static void insertarClase(Clase clase) throws ExcepcionSemantica {
        String lexClase = clase.tokenClase.getLexema();
        if(clases.get(lexClase) == null) {
            clases.put(lexClase, clase);
        } else {
            throw new ExcepcionSemantica(clase.tokenClase, "la clase " + lexClase + " ya está declarada");
        }
    }

    public static void consolidar() throws ExcepcionSemantica {
        for(Clase clase : clases.values()) {
            if(!clase.consolidado) {
                clase.consolidar();
            }
        }
    }

    public static void chequearSentencias() throws ExcepcionSemantica {
        for(Clase clase : clases.values()) {
            clase.chequearSentencias();
        }
    }

    public static void apilarBloque(NodoBloque bloque) {
        pilaBloquesActuales.add(0, bloque);
    }

    public static void desapilarBloque() {
        pilaBloquesActuales.remove(0);
    }

    public void generar() {
        TablaDeSimbolos.codigo.add(".CODE");
        TablaDeSimbolos.codigo.add("PUSH simple_heap_init");
        TablaDeSimbolos.codigo.add("CALL");
        TablaDeSimbolos.codigo.add("PUSH " + metodoMain.getLabel());
        TablaDeSimbolos.codigo.add("CALL");
        TablaDeSimbolos.codigo.add("HALT");

        TablaDeSimbolos.codigo.add("simple_heap_init: RET 0	; Retorna inmediatamente");

        TablaDeSimbolos.codigo.add("simple_malloc: LOADFP	; Inicialización unidad");
        TablaDeSimbolos.codigo.add("LOADSP");
        TablaDeSimbolos.codigo.add("STOREFP ; Finaliza inicialización del RA");
        TablaDeSimbolos.codigo.add("LOADHL	; hl");
        TablaDeSimbolos.codigo.add("DUP	; hl");
        TablaDeSimbolos.codigo.add("PUSH 1	; 1");
        TablaDeSimbolos.codigo.add("ADD	; hl+1");
        TablaDeSimbolos.codigo.add("STORE 4 ; Guarda el resultado (un puntero a la primer celda de la región de memoria)");
        TablaDeSimbolos.codigo.add("LOAD 3	; Carga la cantidad de celdas a alojar (parámetro que debe ser positivo)");
        TablaDeSimbolos.codigo.add("ADD");
        TablaDeSimbolos.codigo.add("STOREHL ; Mueve el heap limit (hl). Expande el heap");
        TablaDeSimbolos.codigo.add("STOREFP");
        TablaDeSimbolos.codigo.add("RET 1	; Retorna eliminando el parámetro");
        TablaDeSimbolos.codigo.add("\n");

        for(Clase clase: clases.values()) {
            //if(clase.esClaseConcreta())
                clase.generar();
        }
    }
}
