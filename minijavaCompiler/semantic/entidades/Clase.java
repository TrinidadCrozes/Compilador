package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoClase;

import java.util.HashMap;
import java.util.List;

public abstract class Clase {

    public Token tokenClase;
    public boolean consolidado;
    HashMap<String, Metodo> metodos;
    HashMap<String, Token> posiblesSubtipos;
    public TipoClase tipo;
    HashMap<Integer, Metodo> metodosOrdenadosOffset;
    int offsetMetodoDisp;

    abstract public void chequearDeclaraciones() throws ExcepcionSemantica;
    abstract public void consolidar() throws ExcepcionSemantica;

    public void setHeredaDe(Token tokenAncestro){}

    abstract public void addInterface(Token tokenInterf) throws ExcepcionSemantica;

    abstract public void insertarMetodo(Metodo met) throws ExcepcionSemantica;

    public void insertarAtributo(Atributo at) throws ExcepcionSemantica {}

    abstract public boolean esClaseConcreta();

    abstract public Metodo getMetodoMismaSignatura(Metodo met);

    public void insertarConstructor(Constructor constructor) throws ExcepcionSemantica {
    }

    public void chequearSentencias() throws ExcepcionSemantica {}

    public TipoClase getTipo() {
        return tipo;
    }

    public void setTipo(TipoClase tipo) {
        this.tipo = tipo;
    }

    public abstract Metodo existeMetodo(String nombreMet, List<NodoExpresion> argsActuales) throws ExcepcionSemantica;

    public Constructor existeConstructor(List<NodoExpresion> argsActuales) throws ExcepcionSemantica {
        return null;
    }
    public abstract HashMap<String, Token> getPosiblesSubtipos();
    public abstract HashMap<String, Token> getAncestros();

    public abstract void generar();

    public String getLabelVT() {
        return "lblVT" + tokenClase.getLexema();
    }

    public abstract int getCantAtributos();

    public abstract int getOffsetMetodoDisp();
    public abstract void setOffsetMetodoDisp(int offset);

}
