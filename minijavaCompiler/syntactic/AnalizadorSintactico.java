package minijavaCompiler.syntactic;

import minijavaCompiler.lexical.AnalizadorLexico;
import minijavaCompiler.lexical.ExcepcionLexica;
import minijavaCompiler.lexical.TipoDeToken;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosAcceso.*;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.*;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoAsignacion;
import minijavaCompiler.semantic.nodosAST.nodosLiteral.*;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.*;
import minijavaCompiler.semantic.tipos.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalizadorSintactico {

    AnalizadorLexico analizadorLexico;
    Token tokenActual;


    public AnalizadorSintactico(AnalizadorLexico analizadorLexico) throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        this.analizadorLexico = analizadorLexico;
        tokenActual = analizadorLexico.nextToken();
        Inicial();
    }

    public void match(TipoDeToken tipoTokenEsperado, String textTokenEsperado) throws ExcepcionLexica, IOException, ExcepcionSintactica {
        if(tipoTokenEsperado.equals(tokenActual.getTipoDeToken())) {
            tokenActual = analizadorLexico.nextToken();
        } else {
            throw new ExcepcionSintactica(tokenActual, textTokenEsperado);
        }
    }

    private void Inicial() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        ListaClases();
        match(TipoDeToken.EOF, "EOF");
    }

    private void ListaClases() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        // primerosClase = {class, interface}
        List<TipoDeToken> primerosClase = Arrays.asList(TipoDeToken.pcClass, TipoDeToken.pcInterface);
        if(primerosClase.contains(tokenActual.getTipoDeToken())) {
            Clase();
            ListaClases();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.EOF)) { // siguientesListaClases = {EOF}
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "Av1");
        }
    }

    private void Clase() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcClass)) {
            ClaseConcreta();
        } else if (tokenActual.getTipoDeToken().equals(TipoDeToken.pcInterface)) {
            Interface();
        } else {
            throw new ExcepcionSintactica(tokenActual, "alguna palabra clave class o interface");
        }
    }

    private void ClaseConcreta() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        match(TipoDeToken.pcClass, "la palabra clave class");
        Token tokenClase = tokenActual;
        match(TipoDeToken.idClase, "un id de clase");
        ClaseConcreta claseConcreta = new ClaseConcreta(tokenClase);
        TablaDeSimbolos.claseActual = claseConcreta;
        Token tokenAncestro = HeredaDe();
        TablaDeSimbolos.claseActual.setHeredaDe(tokenAncestro);
        ImplementaA();
        match(TipoDeToken.puntLlaveIzq, "{");
        ListaMiembros();
        match(TipoDeToken.puntLlaveDer, "}");
        TablaDeSimbolos.insertarClase(TablaDeSimbolos.claseActual);
    }

    private void Interface() throws ExcepcionSintactica, ExcepcionLexica, IOException, ExcepcionSemantica {
        match(TipoDeToken.pcInterface, "la palabra clave interface");
        Token tokenInterface = tokenActual;
        match(TipoDeToken.idClase, "un id de clase");
        Interface interf = new Interface(tokenInterface);
        TablaDeSimbolos.claseActual = interf;
        ExtiendeA();
        match(TipoDeToken.puntLlaveIzq, "{");
        ListaEncabezados();
        match(TipoDeToken.puntLlaveDer, "}");
        TablaDeSimbolos.insertarClase(TablaDeSimbolos.claseActual);
    }

    private Token HeredaDe() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        //siguientesHeredaDe = { implements, { }
        List<TipoDeToken> siguientesHeredaDe = Arrays.asList(TipoDeToken.pcImplements, TipoDeToken.puntLlaveIzq);
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcExtends)) {
            match(TipoDeToken.pcExtends, "la palabra clave extends");
            Token tokenAncestro = tokenActual;
            match(TipoDeToken.idClase, "un id de clase");
            return tokenAncestro;
        } else if(siguientesHeredaDe.contains(tokenActual.getTipoDeToken())) {
            return new Token(TipoDeToken.idClase, "Object", 0);
        } else {
            throw new ExcepcionSintactica(tokenActual, "la palabra clave extends, implements o {");
        }
    }

    private void ImplementaA() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcImplements)) {
            match(TipoDeToken.pcImplements, "la palabra clave implements");
            ListaTipoReferencia();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntLlaveIzq)) { //siguientesImplementaA = { { }
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "la palabra clave implements o {");
        }
    }

    private void ExtiendeA() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcExtends)) {
            match(TipoDeToken.pcExtends,"la palabra clave extends");
            ListaTipoReferencia();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntLlaveIzq)) { // siguientesExtiendeA = { { }
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "la palabra clave extends o {");
        }
    }

    private void ListaTipoReferencia() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        Token tokenClase = tokenActual;
        match(TipoDeToken.idClase, "un id de clase");
        TablaDeSimbolos.claseActual.addInterface(tokenClase);
        ListaTipoReferenciaFact();
    }

    private void ListaTipoReferenciaFact() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntComa)) {
            match(TipoDeToken.puntComa, ",");
            ListaTipoReferencia();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntLlaveIzq)) { // siguientesListaTipoReferenciaFact = { { }
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "una , o {");
        }
    }

    private void ListaMiembros() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        // primerosMiembro = {public, private, static, boolean, char, int, idClase, void}
        List<TipoDeToken> primerosMiembro = Arrays.asList(TipoDeToken.pcPublic, TipoDeToken.pcPrivate, TipoDeToken.pcStatic,
                TipoDeToken.pcBoolean, TipoDeToken.pcInt, TipoDeToken.pcChar, TipoDeToken.pcVoid, TipoDeToken.idClase);
        if(primerosMiembro.contains(tokenActual.getTipoDeToken())) {
            Miembro();
            ListaMiembros();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntLlaveDer)) { // siguientesListaMiembros = { } }
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "una declaración de método o atributo o }");
        }
    }

    private void Miembro() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        // primerosMetodoOConstructor = {static, boolean, char, int, idClase, void}
        List<TipoDeToken> primerosMetodo = Arrays.asList(TipoDeToken.pcStatic, TipoDeToken.pcBoolean, TipoDeToken.pcInt,
                TipoDeToken.pcChar, TipoDeToken.pcVoid, TipoDeToken.idClase);
        // primerosAtributo = {public, private}
        List<TipoDeToken> primerosAtributo = Arrays.asList(TipoDeToken.pcPrivate, TipoDeToken.pcPublic);
        if(primerosAtributo.contains(tokenActual.getTipoDeToken())) {
            Atributo();
        } else if (primerosMetodo.contains(tokenActual.getTipoDeToken())){
            MetodoOConstructor();
        } else {
            throw new ExcepcionSintactica(tokenActual, "una declaración de método, atributo o constructor");
        }
    }

    private void Atributo() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        TipoDeToken visibilidad = Visibilidad();
        Tipo tipo = Tipo();
        ListaDecAtrs(visibilidad, tipo);
        match(TipoDeToken.puntPuntoYComa, ";");
    }

    private void MetodoOConstructor() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        List<TipoDeToken> primerosMetodo = Arrays.asList(TipoDeToken.pcStatic, TipoDeToken.pcBoolean, TipoDeToken.pcInt,
                TipoDeToken.pcChar, TipoDeToken.pcVoid);
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.idClase)) {
            Token tokenIdClase = tokenActual;
            match(TipoDeToken.idClase, "un id de clase");
            MetodoOConstructorFact(tokenIdClase);
        } else if (primerosMetodo.contains(tokenActual.getTipoDeToken())) {
            Metodo();
        } else {
            throw new ExcepcionSintactica(tokenActual, "una declaración de método o constructor");
        }
    }

    private void MetodoOConstructorFact(Token tokenIdClase) throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.idMetVar)) {
            Token tokenMetodo = tokenActual;
            match(TipoDeToken.idMetVar, "un id de método");
            Metodo metodo = new Metodo(tokenMetodo, false, new TipoClase(tokenIdClase), TablaDeSimbolos.claseActual.tokenClase);;
            TablaDeSimbolos.metodoOConstructorActual = metodo;
            MetodoFact();
            TablaDeSimbolos.claseActual.insertarMetodo(metodo);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntParentesisIzq)) {
            Constructor constructor = new Constructor(tokenIdClase);
            TablaDeSimbolos.metodoOConstructorActual = constructor;
            MetodoFact();
            TablaDeSimbolos.claseActual.insertarConstructor(constructor);
        } else {
            throw new ExcepcionSintactica(tokenActual, "una declaración de método o constructor");
        }
    }

    private void Metodo() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        // primerosTipoPrimitivo = {boolean, char, int}
        List<TipoDeToken> primerosTipoPrimitivo = Arrays.asList(TipoDeToken.pcBoolean, TipoDeToken.pcChar, TipoDeToken.pcInt);
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcVoid)) {
            match(TipoDeToken.pcVoid, "la palabra clave void");
            Token tokenMetodo = tokenActual;
            match(TipoDeToken.idMetVar, "un id de método");
            Metodo metodo = new Metodo(tokenMetodo, false, new TipoVoid(), TablaDeSimbolos.claseActual.tokenClase);
            TablaDeSimbolos.metodoOConstructorActual = metodo;
            MetodoFact();
            TablaDeSimbolos.claseActual.insertarMetodo(metodo);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcStatic)) {
            match(TipoDeToken.pcStatic, "la palabra clave static");
            TipoMetodo tipoRetornoMet = TipoMetodo();
            Token tokenMetodo = tokenActual;
            match(TipoDeToken.idMetVar, "un id de método");
            Metodo metodo = new Metodo(tokenMetodo, true, tipoRetornoMet, TablaDeSimbolos.claseActual.tokenClase);
            TablaDeSimbolos.metodoOConstructorActual = metodo;
            MetodoFact();
            TablaDeSimbolos.claseActual.insertarMetodo(metodo);
        } else if(primerosTipoPrimitivo.contains(tokenActual.getTipoDeToken())) {
            TipoPrimitivo tipoRetornoMet = TipoPrimitivo();
            Token tokenMetodo = tokenActual;
            match(TipoDeToken.idMetVar, "un id de método");
            Metodo metodo = new Metodo(tokenMetodo, false, tipoRetornoMet, TablaDeSimbolos.claseActual.tokenClase);
            TablaDeSimbolos.metodoOConstructorActual = metodo;
            MetodoFact();
            TablaDeSimbolos.claseActual.insertarMetodo(metodo);
        } else {
            throw new ExcepcionSintactica(tokenActual, "una declaración de método");
        }
    }

    private void MetodoFact() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        ArgsFormales();
        NodoBloque nodoBloque = Bloque();
        TablaDeSimbolos.metodoOConstructorActual.insertarBloque(nodoBloque);
    }

    private void ListaEncabezados() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        // primerosEncabezadoMetodo = {static, boolean, char, int, idClase, void}
        List<TipoDeToken> primerosEncabezadoMetodo = Arrays.asList(TipoDeToken.pcStatic, TipoDeToken.pcBoolean, TipoDeToken.pcChar,
                TipoDeToken.pcInt, TipoDeToken.idClase, TipoDeToken.pcVoid);
        if(primerosEncabezadoMetodo.contains(tokenActual.getTipoDeToken())) {
            EncabezadoMetodo();
            match(TipoDeToken.puntPuntoYComa, ";");
            ListaEncabezados();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntLlaveDer)) { // siguientesListaEncabezados = { } }
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "un encabezado de método o }");
        }
    }

    private void EncabezadoMetodo() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        boolean metEstatico = EstaticoOpt();
        TipoMetodo tipoRetorno = TipoMetodo();
        Token tokenMetodo = tokenActual;
        match(TipoDeToken.idMetVar, "un id de método o variable");
        Metodo met = new Metodo(tokenMetodo, metEstatico, tipoRetorno, TablaDeSimbolos.claseActual.tokenClase);
        TablaDeSimbolos.metodoOConstructorActual = met;
        ArgsFormales();
        TablaDeSimbolos.claseActual.insertarMetodo(met);
    }

    private TipoDeToken Visibilidad() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcPublic)) {
            TipoDeToken visbilidad = tokenActual.getTipoDeToken();
            match(TipoDeToken.pcPublic, "la palabra clave public");
            return visbilidad;
        } else if (tokenActual.getTipoDeToken().equals(TipoDeToken.pcPrivate)) {
            TipoDeToken visbilidad = tokenActual.getTipoDeToken();
            match(TipoDeToken.pcPrivate, "la palabra clave private");
            return visbilidad;
        } else {
            throw new ExcepcionSintactica(tokenActual, "una palabra clave public o private");
        }
    }

    private Tipo Tipo() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // primerosTipoPrimitivo = {boolean, char, int}
        List<TipoDeToken> primerosTipoPrimitivo = Arrays.asList(TipoDeToken.pcBoolean, TipoDeToken.pcChar, TipoDeToken.pcInt);
        if(primerosTipoPrimitivo.contains(tokenActual.getTipoDeToken())) {
            TipoPrimitivo tipoPrimitivo = TipoPrimitivo();
            return tipoPrimitivo;
        } else if (tokenActual.getTipoDeToken().equals(TipoDeToken.idClase)) {
            Token tipoClase = tokenActual;
            match(TipoDeToken.idClase, "un id de clase");
            return new TipoClase(tipoClase);
        } else {
            throw new ExcepcionSintactica(tokenActual, "un id de clase o un tipo primitivo");
        }
    }

    private TipoPrimitivo TipoPrimitivo() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcBoolean)) {
            match(TipoDeToken.pcBoolean, "la palabra clave boolean");
            return new TipoBoolean();
        } else if (tokenActual.getTipoDeToken().equals(TipoDeToken.pcChar)) {
            match(TipoDeToken.pcChar, "la palabra clave char");
            return new TipoChar();
        } else if (tokenActual.getTipoDeToken().equals(TipoDeToken.pcInt)) {
            match(TipoDeToken.pcInt, "la palabra clave int");
            return new TipoInt();
        } else {
            throw new ExcepcionSintactica(tokenActual, "una palabra clave boolean, char o int");
        }
    }

    private void ListaDecAtrs(TipoDeToken visibilidad, Tipo tipo) throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        Token tokenAt = tokenActual;
        match(TipoDeToken.idMetVar, "un id de método o variable");
        Atributo at = new Atributo(tokenAt, visibilidad, tipo, TablaDeSimbolos.claseActual.tokenClase);
        TablaDeSimbolos.claseActual.insertarAtributo(at);
        ListaDecAtrsFact(visibilidad, tipo);
    }

    private void ListaDecAtrsFact(TipoDeToken visibilidad, Tipo tipo) throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntComa)) {
            match(TipoDeToken.puntComa, ",");
            ListaDecAtrs(visibilidad, tipo);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntPuntoYComa)) { // siguientesListaDecAtrsFact = { ; }
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "una , o ;");
        }
    }

    private boolean EstaticoOpt() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // siguientesEstaticoOpt = {boolean, char, int, idClase, void}
        List<TipoDeToken> siguientesEstaticoOpt = Arrays.asList(TipoDeToken.pcBoolean, TipoDeToken.pcChar, TipoDeToken.pcInt, TipoDeToken.idClase,
                TipoDeToken.pcVoid);
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcStatic)) {
            match(TipoDeToken.pcStatic, "la palabra clave static");
            return true;
        } else if(siguientesEstaticoOpt.contains(tokenActual.getTipoDeToken())) {
            return false;
        } else {
            throw new ExcepcionSintactica(tokenActual, "la palabra clave static o un tipo para un método");
        }
    }

    private TipoMetodo TipoMetodo() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // primerosTipo =  {boolean, char, int, idClase}
        List<TipoDeToken> primerosTipo = Arrays.asList(TipoDeToken.pcBoolean, TipoDeToken.pcChar, TipoDeToken.pcInt, TipoDeToken.idClase);
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcVoid)) {
            match(TipoDeToken.pcVoid, "la palabra clave void");
            return new TipoVoid();
        } else if (primerosTipo.contains(tokenActual.getTipoDeToken())) {
            Tipo tipo = Tipo();
            return tipo;
        } else {
            throw new ExcepcionSintactica(tokenActual, "un tipo para un método");
        }
    }

    private void ArgsFormales() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        match(TipoDeToken.puntParentesisIzq, "(");
        ListaArgsFormalesOpt();
        match(TipoDeToken.puntParentesisDer, ")");
    }

    private void ListaArgsFormalesOpt() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        // primerosListaArgsFormales = {boolean, char, int, idClase}
        List<TipoDeToken> primerosListaArgsFormales = Arrays.asList(TipoDeToken.pcBoolean, TipoDeToken.pcChar, TipoDeToken.pcInt, TipoDeToken.idClase);
        if(primerosListaArgsFormales.contains(tokenActual.getTipoDeToken())) {
            ListaArgsFormales();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntParentesisDer)) { // siguientesListaArgsFormalesOpt =  { ) }
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "un tipo para un argumento o )");
        }
    }

    private void ListaArgsFormales() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        ArgFormal();
        ListaArgsFormalesFact();
    }

    private void ListaArgsFormalesFact() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntComa)) {
            match(TipoDeToken.puntComa, ",");
            ListaArgsFormales();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntParentesisDer)) { // siguientesListaArgsFormalesFact = { ) }
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "una , o )");
        }
    }

    private void ArgFormal() throws ExcepcionLexica, IOException, ExcepcionSintactica, ExcepcionSemantica {
        Tipo tipoParam = Tipo();
        Token tokenParam = tokenActual;
        match(TipoDeToken.idMetVar, "un id de método o variable");
        ParametroFormal param = new ParametroFormal(tokenParam, tipoParam);
        TablaDeSimbolos.metodoOConstructorActual.insertarParametro(param);
    }

    private NodoBloque Bloque() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        match(TipoDeToken.puntLlaveIzq, "{");
        NodoBloque nodoBloque = new NodoBloque();
        ListaSentencias(nodoBloque);
        match(TipoDeToken.puntLlaveDer, "}");
        return nodoBloque;
    }

    private void ListaSentencias(NodoBloque nodoBloque) throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // primerosSentencia = { ;, this, idMetVar, new, idClase, (, var , return,  if, while, { }
        List<TipoDeToken> primerosSentencia = Arrays.asList(TipoDeToken.puntPuntoYComa, TipoDeToken.pcThis, TipoDeToken.idClase, TipoDeToken.idMetVar,
                TipoDeToken.pcNew, TipoDeToken.puntParentesisIzq, TipoDeToken.puntLlaveIzq, TipoDeToken.pcVar, TipoDeToken.pcReturn, TipoDeToken.pcIf,
                TipoDeToken.pcWhile);
        if(primerosSentencia.contains(tokenActual.getTipoDeToken())) {
            NodoSentencia nodoSentencia = Sentencia();
            nodoBloque.insertarSentencia(nodoSentencia);
            ListaSentencias(nodoBloque);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntLlaveDer)) { //siguientesListaSentencias = { } }
            //𝜖
        } else {
            throw new ExcepcionSintactica(tokenActual, "una sentencia o }");
        }
    }

    private NodoSentencia Sentencia() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // primerosAcceso = {this, idMetVar, new, idClase, (}
        List<TipoDeToken> primerosAcceso = Arrays.asList(TipoDeToken.pcThis, TipoDeToken.pcNew, TipoDeToken.idMetVar, TipoDeToken.idClase, TipoDeToken.puntParentesisIzq);
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntPuntoYComa)) {
            match(TipoDeToken.puntPuntoYComa, ";");
            return null;
        } else if(primerosAcceso.contains(tokenActual.getTipoDeToken())) {
            NodoAcceso nodoAcceso = Acceso();
            return SentenciaFact(nodoAcceso);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcVar)) {
            NodoVarLocal nodoVarLocal = VarLocal();
            match(TipoDeToken.puntPuntoYComa, ";");
            return nodoVarLocal;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcReturn)) {
            NodoReturn nodoReturn = Return();
            match(TipoDeToken.puntPuntoYComa, ";");
            return nodoReturn;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcIf)) {
            return If();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcWhile)) {
            return While();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntLlaveIzq)) {
            return Bloque();
        } else {
            throw new ExcepcionSintactica(tokenActual, "un ;, una forma de acceso, una declaración de variable, if, while o un bloque");
        }
    }

    private NodoSentencia SentenciaFact(NodoAcceso nodoAcceso) throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // primerosTipoDeAsignacion = {=, +=, -=}
        List<TipoDeToken> primerosTipoDeAsignacion = Arrays.asList(TipoDeToken.asigAsignacion, TipoDeToken.asigIncremento, TipoDeToken.asigDecremento);
        if(primerosTipoDeAsignacion.contains(tokenActual.getTipoDeToken())) {
            NodoAsignacion nodoTipoAsig = TipoDeAsignacion();
            NodoExpresion expresion = Expresion();
            match(TipoDeToken.puntPuntoYComa, ";");
            nodoTipoAsig.setAcceso(nodoAcceso);
            nodoTipoAsig.setExpresion(expresion);
            return nodoTipoAsig;
        } else {
            Token token = tokenActual;
            match(TipoDeToken.puntPuntoYComa, ";");
            return new NodoLlamada(nodoAcceso, token);
        }
    }

    private NodoAsignacion TipoDeAsignacion() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.asigAsignacion)) {
            Token tokenAsig = tokenActual;
            match(TipoDeToken.asigAsignacion, "=");
            return new NodoAsigAsignacion(tokenAsig);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.asigDecremento)) {
            Token tokenAsig = tokenActual;
            match(TipoDeToken.asigDecremento, "-=");
            return new NodoAsigDecremento(tokenAsig);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.asigIncremento)) {
            Token tokenAsig = tokenActual;
            match(TipoDeToken.asigIncremento, "+=");
            return new NodoAsigIncremento(tokenAsig);
        } else {
            throw new ExcepcionSintactica(tokenActual, "un operador de asignación =, += o -=");
        }
    }

    private NodoVarLocal VarLocal() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        match(TipoDeToken.pcVar, "la palabra clave var");
        Token idVar = tokenActual;
        match(TipoDeToken.idMetVar, "un id de método o variable");
        Token asig = tokenActual;
        match(TipoDeToken.asigAsignacion, "el operador de asignación =");
        NodoExpresion expresion = Expresion();
        return new NodoVarLocal(idVar, asig, expresion);
    }

    private NodoReturn Return() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        Token tokenReturn = tokenActual;
        match(TipoDeToken.pcReturn, "la palabra clave return");
        NodoExpresion expresion = ExpresionOpt();
        return new NodoReturn(tokenReturn, expresion);
    }

    private NodoExpresion ExpresionOpt() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // primerosExpresion = {+, -, !, null, true, false, intLiteral, charLiteral, stringLiteral, this, idMetVar, new, idClase, (}
        List<TipoDeToken> primerosExpresion = Arrays.asList(TipoDeToken.puntParentesisIzq, TipoDeToken.pcNull, TipoDeToken.pcTrue, TipoDeToken.pcFalse,
                TipoDeToken.opSuma, TipoDeToken.opResta, TipoDeToken. opNot, TipoDeToken.litInt, TipoDeToken.litChar, TipoDeToken.litString, TipoDeToken.pcThis,
                TipoDeToken.idMetVar, TipoDeToken.idClase, TipoDeToken.pcNew);
        if(primerosExpresion.contains(tokenActual.getTipoDeToken())) {
            return Expresion();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntPuntoYComa)) { // siguientesExpresionOpt = { ; }
            //𝜖
            return null;
        } else {
            throw new ExcepcionSintactica(tokenActual, "una expresión o ;");
        }
    }

    private NodoIf If() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        Token tokenIf = tokenActual;
        match(TipoDeToken.pcIf, "la palabra clave if");
        match(TipoDeToken.puntParentesisIzq, "(");
        NodoExpresion expresion = Expresion();
        match(TipoDeToken.puntParentesisDer, ")");
        NodoSentencia sentencia = Sentencia();
        NodoSentencia sentenciaElse = Else();
        return new NodoIf(tokenIf, expresion, sentencia, sentenciaElse);
    }

    private NodoSentencia Else() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // siguientesElse = {else, ; , this, idMetVar, new, idClase, ( , var , return,  if, while, { , } }
        List<TipoDeToken> siguientesElse = Arrays.asList(TipoDeToken.puntPuntoYComa, TipoDeToken.pcThis, TipoDeToken.idMetVar, TipoDeToken.pcNew,
                TipoDeToken.idClase, TipoDeToken.puntParentesisIzq, TipoDeToken.pcVar, TipoDeToken.pcReturn, TipoDeToken.pcIf, TipoDeToken.pcWhile,
                TipoDeToken.puntLlaveIzq, TipoDeToken.puntLlaveDer, TipoDeToken.pcElse);
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcElse)) {
            match(TipoDeToken.pcElse, "la palabra clave else");
            return Sentencia();
        } else if(siguientesElse.contains(tokenActual.getTipoDeToken())) {
            //𝜖
            return null;
        } else {
            throw new ExcepcionSintactica(tokenActual, "else, una sentencia o }");
        }
    }

    private NodoWhile While() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        Token tokenWhile = tokenActual;
        match(TipoDeToken.pcWhile, "la palabra clave while");
        match(TipoDeToken.puntParentesisIzq, "(");
        NodoExpresion expresion = Expresion();
        match(TipoDeToken.puntParentesisDer, ")");
        NodoSentencia sentencia = Sentencia();
        return new NodoWhile(tokenWhile, expresion, sentencia);
    }

    private NodoExpresion Expresion() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        NodoExpresion nodoIzq = ExpresionUnaria();
        return ExpresionRec(nodoIzq);
    }

    private NodoExpresion ExpresionRec(NodoExpresion nodoIzq) throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // primerosOperadorBinario = {|| , && , == , != , < , > , <= , >= , + , - , * , / , %}+
        List<TipoDeToken> primerosOperadorBinario = Arrays.asList(TipoDeToken.opOr, TipoDeToken.opAnd, TipoDeToken.opIgual, TipoDeToken.opDistinto,
                TipoDeToken.opSuma, TipoDeToken.opResta, TipoDeToken. opProducto, TipoDeToken.opDivision, TipoDeToken.opModulo, TipoDeToken.opMayor,
                TipoDeToken.opMenor, TipoDeToken.opMayorIgual, TipoDeToken.opMenorIgual);
        // siguientesExpresionRec = { ) , ; , , }
        List<TipoDeToken> siguientesExpresionRec = Arrays.asList(TipoDeToken.puntParentesisDer, TipoDeToken.puntPuntoYComa, TipoDeToken.puntComa);
        if(primerosOperadorBinario.contains(tokenActual.getTipoDeToken())) {
            Token token = OperadorBinario();
            NodoExpresionBinaria nodoExpresionBinaria = new NodoExpresionBinaria(token);
            NodoExpresionUnaria nodoDer = ExpresionUnaria();
            nodoExpresionBinaria.setNodosExpresiones(nodoDer, nodoIzq);
            return ExpresionRec(nodoExpresionBinaria);
        } else if(siguientesExpresionRec.contains(tokenActual.getTipoDeToken())) {
            //𝜖
            return nodoIzq;
        } else {
            throw new ExcepcionSintactica(tokenActual, "un operador binario o ), ; o ,");
        }
    }

    private Token OperadorBinario() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.opOr)) {
            Token token = tokenActual;
            match(TipoDeToken.opOr, "||");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opAnd)) {
            Token token = tokenActual;
            match(TipoDeToken.opAnd, "&&");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opIgual)) {
            Token token = tokenActual;
            match(TipoDeToken.opIgual, "==");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opDistinto)) {
            Token token = tokenActual;
            match(TipoDeToken.opDistinto, "!=");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opMayor)) {
            Token token = tokenActual;
            match(TipoDeToken.opMayor, ">");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opMenor)) {
            Token token = tokenActual;
            match(TipoDeToken.opMenor, "<");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opMayorIgual)) {
            Token token = tokenActual;
            match(TipoDeToken.opMayorIgual, ">=");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opMenorIgual)) {
            Token token = tokenActual;
            match(TipoDeToken.opMenorIgual, "<=");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opSuma)) {
            Token token = tokenActual;
            match(TipoDeToken.opSuma, "+");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opResta)) {
            Token token = tokenActual;
            match(TipoDeToken.opResta, "-");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opProducto)) {
            Token token = tokenActual;
            match(TipoDeToken.opProducto, "*");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opDivision)) {
            Token token = tokenActual;
            match(TipoDeToken.opDivision, "/");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opModulo)) {
            Token token = tokenActual;
            match(TipoDeToken.opModulo, "%");
            return token;
        } else {
            throw new ExcepcionSintactica(tokenActual, "un operador binario");
        }
    }

    private NodoExpresionUnaria ExpresionUnaria() throws ExcepcionSintactica, ExcepcionLexica, IOException {
        // primerosOperadorUnario = {+, -, !}
        List<TipoDeToken> primerosOperadorUnario = Arrays.asList(TipoDeToken.opSuma, TipoDeToken.opResta, TipoDeToken. opNot);
        // primerosOperando = {null, true, false, intLiteral, charLiteral, stringLiteral, this, idMetVar, new, idClase, (}
        List<TipoDeToken> primerosOperando = Arrays.asList(TipoDeToken.puntParentesisIzq, TipoDeToken.pcNull, TipoDeToken.pcTrue, TipoDeToken.pcFalse,
                TipoDeToken.litInt, TipoDeToken.litChar, TipoDeToken.litString, TipoDeToken.pcThis, TipoDeToken.idMetVar, TipoDeToken.idClase, TipoDeToken.pcNew);
        if(primerosOperadorUnario.contains(tokenActual.getTipoDeToken())) {
            Token operador = OperadorUnario();
            NodoOperando operando = Operando();
            return new NodoExpresionUnaria(operador, operando);
        } else if(primerosOperando.contains(tokenActual.getTipoDeToken())) {
            NodoOperando operando = Operando();
            return new NodoExpresionUnaria(null, operando);
        } else {
            throw new ExcepcionSintactica(tokenActual, "una operador unario o un operando");
        }
    }

    private Token OperadorUnario() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.opSuma)) {
            Token token = tokenActual;
            match(TipoDeToken.opSuma, "+");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opResta)) {
            Token token = tokenActual;
            match(TipoDeToken.opResta, "-");
            return token;
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.opNot)) {
            Token token = tokenActual;
            match(TipoDeToken.opNot, "!");
            return token;
        } else {
            throw new ExcepcionSintactica(tokenActual, "un operador unario +, - o !");
        }
    }

    private NodoOperando Operando() throws ExcepcionSintactica, ExcepcionLexica, IOException {
        // primerosLiteral = {null, true, false, intLiteral, charLiteral, stringLiteral}
        List<TipoDeToken> primerosLiteral = Arrays.asList(TipoDeToken.pcNull, TipoDeToken.pcTrue, TipoDeToken.pcFalse, TipoDeToken.litInt, TipoDeToken.litChar, TipoDeToken.litString);
        // primerosAcceso = {this, idMetVar, new, idClase, (}
        List<TipoDeToken> primerosAcceso = Arrays.asList(TipoDeToken.pcThis, TipoDeToken.pcNew, TipoDeToken.idMetVar, TipoDeToken.idClase, TipoDeToken.puntParentesisIzq);
        if(primerosLiteral.contains(tokenActual.getTipoDeToken())){
            return Literal();
        } else if (primerosAcceso.contains(tokenActual.getTipoDeToken())) {
            return Acceso();
        } else {
            throw new ExcepcionSintactica(tokenActual, "un literal o un acceso");
        }
    }

    private NodoOperando Literal() throws ExcepcionSintactica, ExcepcionLexica, IOException {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcNull)) {
            Token tokenNull = tokenActual;
            match(TipoDeToken.pcNull, "la palabra clave null");
            return new NodoNull(tokenNull);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcTrue)) {
            Token tokenTrue = tokenActual;
            match(TipoDeToken.pcTrue, "la palabra clave true");
            return new NodoTrue(tokenTrue);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcFalse)) {
            Token tokenFalse = tokenActual;
            match(TipoDeToken.pcFalse, "la palabra clave false");
            return new NodoFalse(tokenFalse);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.litInt)) {
            Token tokenInt = tokenActual;
            match(TipoDeToken.litInt, "un literal int");
            return new NodoInt(tokenInt);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.litChar)) {
            Token tokenChar = tokenActual;
            match(TipoDeToken.litChar, "un literal char");
            return new NodoChar(tokenChar);
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.litString)) {
            Token tokenString = tokenActual;
            match(TipoDeToken.litString, "un literal String");
            return new NodoString(tokenString);
        } else {
            throw new ExcepcionSintactica(tokenActual, "un literal");
        }
    }

    private NodoAcceso Acceso() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        NodoPrimario nodoPrimario = Primario();
        NodoEncadenado encadenado = EncadenadoOpt();
        nodoPrimario.setEncadenado(encadenado);
        return nodoPrimario;
    }

    private NodoPrimario Primario() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.pcThis)) {
            return AccesoThis();
        } else if (tokenActual.getTipoDeToken().equals(TipoDeToken.idMetVar)) {
            Token tokenMetVar = tokenActual;
            match(TipoDeToken.idMetVar, "id de método o variable");
            return AccesoMetodoOVar(tokenMetVar);
        } else if (tokenActual.getTipoDeToken().equals(TipoDeToken.pcNew)) {
            return AccesoConstructor();
        } else if (tokenActual.getTipoDeToken().equals(TipoDeToken.idClase)) {
            return AccesoMetodoEstatico();
        } else if (tokenActual.getTipoDeToken().equals(TipoDeToken.puntParentesisIzq)) {
            return ExpresionParentizada();
        } else {
            throw new ExcepcionSintactica(tokenActual, "la palabra clave this, new, un id de clase o de método o variable o (");
        }
    }

    private NodoAccesoThis AccesoThis() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        Token tokenAccThis = tokenActual;
        match(TipoDeToken.pcThis, "this");
        return new NodoAccesoThis(tokenAccThis);
    }

    private NodoPrimario AccesoMetodoOVar(Token tokenMetVar) throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // siguientesAccesoMetodoOVar = { . , =, +=, -=, || , && , == , != , < , > , <= , >= , + , - , * , / , %, ) , ; , , }
        List<TipoDeToken> siguientesAccesoMetodoOVar = Arrays.asList(TipoDeToken.puntPunto, TipoDeToken.asigAsignacion, TipoDeToken.asigDecremento,
                TipoDeToken.asigIncremento, TipoDeToken.opOr, TipoDeToken.opAnd, TipoDeToken.opIgual, TipoDeToken.opDistinto, TipoDeToken.opMenor,
                TipoDeToken.opMayor, TipoDeToken.opMenorIgual, TipoDeToken.opMayorIgual, TipoDeToken.opSuma, TipoDeToken.opResta, TipoDeToken.opProducto,
                TipoDeToken.opDivision, TipoDeToken.opModulo, TipoDeToken.puntParentesisDer, TipoDeToken.puntPuntoYComa, TipoDeToken.puntComa);
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntParentesisIzq)) {
            List<NodoExpresion> args = ArgsActuales();
            return new NodoAccesoMetodo(tokenMetVar, args);
        } else if(siguientesAccesoMetodoOVar.contains(tokenActual.getTipoDeToken())) {
            //𝜖
            return new NodoAccesoVar(tokenMetVar);
        } else {
            throw new ExcepcionSintactica(tokenActual, "un (, ), ;, ,, un operador de asignación u operador binario");
        }
    }

    private NodoAccesoConstructor AccesoConstructor() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        match(TipoDeToken.pcNew, "la palabra clave new");
        Token idClase = tokenActual;
        match(TipoDeToken.idClase, "un id de clase");
        List<NodoExpresion> args = ArgsActuales();
        return new NodoAccesoConstructor(idClase, args);
    }

    private NodoExpresionParentizada ExpresionParentizada() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        Token token = tokenActual;
        match(TipoDeToken.puntParentesisIzq, "(");
        NodoExpresion expresion = Expresion();
        match(TipoDeToken.puntParentesisDer, ")");
        return new NodoExpresionParentizada(token, expresion);
    }

    private NodoAccesoMetEstatico AccesoMetodoEstatico() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        Token tokenClase = tokenActual;
        match(TipoDeToken.idClase, "un id de clase");
        match(TipoDeToken.puntPunto, ".");
        Token tokenMetVar = tokenActual;
        match(TipoDeToken.idMetVar, "un id de método o variable");
        List<NodoExpresion> args = ArgsActuales();
        return new NodoAccesoMetEstatico(tokenClase, tokenMetVar, args);
    }

    private List<NodoExpresion> ArgsActuales() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        match(TipoDeToken.puntParentesisIzq, "(");
        List<NodoExpresion> listaExps = ListaExpsOpt();
        match(TipoDeToken.puntParentesisDer, ")");
        return listaExps;
    }

    private List<NodoExpresion> ListaExpsOpt() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        // primerosListaExps = {+, -, !, null, true, false, intLiteral, charLiteral, stringLiteral, this, idMetVar, new, idClase, (}
        List<TipoDeToken> primerosListaExps = Arrays.asList(TipoDeToken.puntParentesisIzq, TipoDeToken.pcNull, TipoDeToken.pcTrue, TipoDeToken.pcFalse,
                TipoDeToken.opSuma, TipoDeToken.opResta, TipoDeToken. opNot, TipoDeToken.litInt, TipoDeToken.litChar, TipoDeToken.litString, TipoDeToken.pcThis,
                TipoDeToken.idMetVar, TipoDeToken.idClase, TipoDeToken.pcNew);
        if(primerosListaExps.contains(tokenActual.getTipoDeToken())) {
            return ListaExps();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntParentesisDer)) { // siguientesListaExpsOpt = { ) }
            //𝜖
            return new ArrayList<NodoExpresion>();
        } else {
            throw new ExcepcionSintactica(tokenActual, "un ) o una expresión");
        }
    }

    private List<NodoExpresion> ListaExps() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        NodoExpresion expresion = Expresion();
        List<NodoExpresion> listaArgsActuales = ListaExpsFact();
        listaArgsActuales.add(0, expresion);
        return listaArgsActuales;
    }

    private List<NodoExpresion> ListaExpsFact() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntComa)) {
            match(TipoDeToken.puntComa, ",");
            return ListaExps();
        } else if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntParentesisDer)) { // siguientesListaExpsFact = { ) }
            //𝜖
            return new ArrayList<NodoExpresion>();
        } else {
            throw new ExcepcionSintactica(tokenActual, "un ) o ,");
        }
    }

    private NodoEncadenado EncadenadoOpt() throws ExcepcionLexica, IOException, ExcepcionSintactica {
        //siguientesEncadenadoOpt = {=, +=, -=, || , && , == , != , < , > , <= , >= , + , - , * , / , %, ) , ; , , }
        List<TipoDeToken> siguientesEncadenadoOpt = Arrays.asList(TipoDeToken.asigAsignacion, TipoDeToken.asigDecremento, TipoDeToken.asigIncremento,
                TipoDeToken.opOr, TipoDeToken.opAnd, TipoDeToken.opIgual, TipoDeToken.opDistinto, TipoDeToken.opMenor, TipoDeToken.opMayor,
                TipoDeToken.opMenorIgual, TipoDeToken.opMayorIgual, TipoDeToken.opSuma, TipoDeToken.opResta, TipoDeToken.opProducto, TipoDeToken.opDivision,
                TipoDeToken.opModulo, TipoDeToken.puntParentesisDer, TipoDeToken.puntPuntoYComa, TipoDeToken.puntComa);
        if(tokenActual.getTipoDeToken().equals(TipoDeToken.puntPunto)) {
            match(TipoDeToken.puntPunto, ".");
            Token tokenMetVar = tokenActual;
            match(TipoDeToken.idMetVar, "un id de método o variable");
            return EncadenadoVarOMetodo(tokenMetVar);
        } else if(siguientesEncadenadoOpt.contains(tokenActual.getTipoDeToken())) {
            //𝜖
            return null;
        } else {
            throw new ExcepcionSintactica(tokenActual, "un ., ), ;, ,, un operador de asignación u operador binario");
        }
    }

    private NodoEncadenado EncadenadoVarOMetodo(Token tokenMetVar) throws ExcepcionLexica, IOException, ExcepcionSintactica {
        if (tokenActual.getTipoDeToken().equals(TipoDeToken.puntParentesisIzq)) {
            List<NodoExpresion> args = ArgsActuales();
            NodoEncadenado encadenado = EncadenadoOpt();
            return new NodoAccesoMetEncadenado(tokenMetVar, args, encadenado);
        } else { //primerosEncadenadoOpt contiene 𝜖
            NodoEncadenado encadenado = EncadenadoOpt();
            return new NodoAccesoVarEncadenada(tokenMetVar, encadenado);
        }
    }

}