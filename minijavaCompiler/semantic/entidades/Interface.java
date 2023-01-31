package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoClase;

import java.util.HashMap;
import java.util.List;

public class Interface extends Clase {

    HashMap<String, Token> extiendeA;
    HashMap<String, Token> clasesExt;
    boolean verificadaExtensionCircular;
    boolean consolidado;

    public Interface(Token tokenInterface) {
        this.tokenClase = tokenInterface;
        metodos = new HashMap<>();
        extiendeA = new HashMap<>();
        consolidado = false;
        clasesExt = new HashMap<>();
        posiblesSubtipos = new HashMap<>();
        tipo = new TipoClase(tokenInterface);
        metodosOrdenadosOffset = new HashMap<>();
        offsetMetodoDisp = 0;
    }

    public void chequearDeclaraciones() throws ExcepcionSemantica {
        chequearExtensiones();
        for(Metodo met: metodos.values()) {
            met.chequearDeclaraciones();
        }
    }

    private void chequearExtensiones() throws ExcepcionSemantica {
        for(Token ext: extiendeA.values()) {
            if(TablaDeSimbolos.clases.get(ext.getLexema()) == null) {
                throw new ExcepcionSemantica(ext, "la clase " + ext.getLexema() + " que " + tokenClase.getLexema() + " debe extender no existe");
            } else if(TablaDeSimbolos.clases.get(ext.getLexema()).esClaseConcreta()) {
                throw new ExcepcionSemantica(ext, tokenClase.getLexema() + " extiende a " + ext.getLexema() + " pero es una clase concreta");
            }
        }
    }

    @Override
    public void addInterface(Token tokenInterf) throws ExcepcionSemantica {
        if(extiendeA.get(tokenInterf.getLexema()) == null) {
            extiendeA.put(tokenInterf.getLexema(), tokenInterf);
        } else {
            throw new ExcepcionSemantica(tokenInterf, "la interface " + tokenInterf.getLexema() + " ya se encuentra en la signatura de la interface " + tokenClase.getLexema());
        }
    }

    @Override
    public void insertarMetodo(Metodo met) throws ExcepcionSemantica {
        if(metodos.get(met.getTokenMetodo().getLexema()) == null) {
            //chequeo que ningún método sea static
            if(met.getMetEstatico()) {
                throw new ExcepcionSemantica(met.getTokenMetodo(), "el método estático " + met.getTokenMetodo().getLexema() + " no puede estar en una interface");
            }
            metodos.put(met.getTokenMetodo().getLexema(), met);
        } else {
            throw new ExcepcionSemantica(met.getTokenMetodo(), "el método " + met.getTokenMetodo().getLexema() + " ya está declarado en la interface " + tokenClase.getLexema());
        }
    }

    @Override
    public boolean esClaseConcreta() {
        return false;
    }

    @Override
    public Metodo getMetodoMismaSignatura(Metodo met) {
        Metodo metDec = metodos.get(met.getTokenMetodo().getLexema());
        if(metDec != null) {
            if(metDec.mismaSignatura(met)) {
                return metDec;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public HashMap<String, Token> getAncestros() {
        return clasesExt;
    }

    @Override
    public int getCantAtributos() {
        return 0;
    }

    @Override
    public int getOffsetMetodoDisp() {
        return offsetMetodoDisp;
    }

    @Override
    public void setOffsetMetodoDisp(int offset) {
        offsetMetodoDisp = offset;
    }

    @Override
    public Metodo existeMetodo(String nombreMet, List<NodoExpresion> argsActuales) throws ExcepcionSemantica {
        for(Metodo met: metodos.values()) {
            if(nombreMet.equals(met.getTokenMetodo().getLexema())) {
                if (met.conformanParametros(argsActuales)) {
                    return met;
                }
            }
        }
        return null;
    }

    @Override
    public HashMap<String, Token> getPosiblesSubtipos() {
        for(Token t: extiendeA.values()) {
            posiblesSubtipos.put(t.getLexema(), t);
            posiblesSubtipos.putAll(TablaDeSimbolos.clases.get(t.getLexema()).getPosiblesSubtipos());
        }
        return posiblesSubtipos;
    }

    public void consolidar() throws ExcepcionSemantica {
        if(!consolidado) {
            chequearExtensionCircular(new HashMap<>());
            consolidarExtensiones();
            setOffsetMetodos();
            consolidado = true;
        }
    }

    private void consolidarExtensiones() throws ExcepcionSemantica {
        for(Token tExt: extiendeA.values()) {
            Interface interfaceExt = (Interface) TablaDeSimbolos.clases.get(tExt.getLexema());
            if(!interfaceExt.consolidado) {
                interfaceExt.consolidar();
            }
            for(Metodo metExt: interfaceExt.metodos.values()) {
                String lexemaMetExt = metExt.getTokenMetodo().getLexema();
                if(metodos.get(lexemaMetExt) == null) {
                    metodos.put(lexemaMetExt, metExt);
                } else if(!metodos.get(lexemaMetExt).mismaSignatura(metExt)) {
                    throw new ExcepcionSemantica(metodos.get(lexemaMetExt).getTokenMetodo(), "el método " + lexemaMetExt + " fue declarado con distinta signatura a la de " + interfaceExt.tokenClase.getLexema());
                }
            }
        }
    }

    private void setOffsetMetodos() {
        for(Token t: extiendeA.values()) {
            Interface intExt = ((Interface)TablaDeSimbolos.getClase(t.getLexema()));
            offsetMetodoDisp += intExt.getOffsetDispVT();
        }
        for(Metodo met: metodos.values()) {
            Metodo metExt = null;
            for(Token t: extiendeA.values()) {
                Interface intExt = ((Interface)TablaDeSimbolos.getClase(t.getLexema()));
                metExt = intExt.metodos.get(met.getTokenMetodo().getLexema());
                if(metExt != null) {
                    break;
                }
            }
            if(metExt == null) {
                met.setOffset(offsetMetodoDisp);
                offsetMetodoDisp++;
            } else {
                met.setOffset(metExt.offset());
            }
            metodosOrdenadosOffset.put(met.offset(), met);
        }
    }

    private void chequearExtensionCircular(HashMap<String, Token> clasesE) throws ExcepcionSemantica {
        if (!verificadaExtensionCircular) {
            for (Token ext : extiendeA.values()) {
                if (clasesE.get(ext.getLexema()) != null) {
                    throw new ExcepcionSemantica(extiendeA.get(ext.getLexema()), "la interface " + extiendeA.get(ext.getLexema()).getLexema() + " causa extensión circular");
                } else {
                    Interface claseAncestro = (Interface) TablaDeSimbolos.clases.get(ext.getLexema());
                    clasesE.put(tokenClase.getLexema(), tokenClase);
                    claseAncestro.chequearExtensionCircular(clasesE);
                }
                verificadaExtensionCircular = true;
                this.clasesExt.putAll(clasesE);
                clasesE.remove(this.tokenClase.getLexema());
            }
        }
    }

    public int getOffsetDispVT() {
        return offsetMetodoDisp;
    }

    @Override
    public void generar() {}
}
