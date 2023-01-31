package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoClase;

import java.util.HashMap;
import java.util.List;

public class ClaseConcreta extends Clase {

    Constructor constructorClase;
    HashMap<String, Atributo> atributos;
    Token heredaDe;
    HashMap<String, Token> implementaA;
    HashMap<String, Token> ancestros;
    boolean verificadoHerenciaCircular;
    boolean consolidado;
    int offsetDispCIR;
    HashMap<Integer, Atributo> atributosCIR;


    public ClaseConcreta(Token clase) {
        this.tokenClase = clase;
        atributos = new HashMap<>();
        metodos = new HashMap<>();
        implementaA = new HashMap<>();
        verificadoHerenciaCircular = false;
        consolidado = false;
        ancestros = new HashMap<>();
        posiblesSubtipos = new HashMap<>();
        tipo = new TipoClase(tokenClase);
        metodosOrdenadosOffset = new HashMap<>();
        atributosCIR = new HashMap<>();
        offsetMetodoDisp = 0;
        offsetDispCIR = 0;
        if(tokenClase.getLexema().equals("Object")) {
            offsetDispCIR = 1;
            offsetMetodoDisp = 0;
        }
    }

    /////////////////////////////CHEQUEO DE DECLARACIONES
    public void chequearDeclaraciones() throws ExcepcionSemantica {
        if(!tokenClase.getLexema().equals("Object")) {
            if(constructorClase == null) {
                constructorClase = new Constructor(tokenClase);
            }
            constructorClase.chequearDeclaraciones();
            chequearHerencia();
            chequearImplementaciones();
            for (Atributo at : atributos.values()) {
                at.chequearDeclaraciones();
            }
            for (Metodo met : metodos.values()) {
                met.chequearDeclaraciones();
            }
        }
    }

    private void chequearHerencia() throws ExcepcionSemantica {
        if(!tokenClase.getLexema().equals("Object")) {
            if (TablaDeSimbolos.clases.get(heredaDe.getLexema()) == null) {
                throw new ExcepcionSemantica(heredaDe, "la clase " + heredaDe.getLexema() + " de la que " + tokenClase.getLexema() + " debe heredar no existe");
            } else if (!TablaDeSimbolos.clases.get(heredaDe.getLexema()).esClaseConcreta()) {
                throw new ExcepcionSemantica(heredaDe, tokenClase.getLexema() + " hereda de " + heredaDe.getLexema() + " pero es una interface");
            }
        }
    }

    private void chequearImplementaciones() throws ExcepcionSemantica {
        for(Token imp: implementaA.values()) {
            if(TablaDeSimbolos.clases.get(imp.getLexema()) == null) {
                throw new ExcepcionSemantica(imp, "la clase " + imp.getLexema() + " que " + tokenClase.getLexema() + " debe implementar no existe");
            } else if(TablaDeSimbolos.clases.get(imp.getLexema()).esClaseConcreta()) {
                throw new ExcepcionSemantica(imp, tokenClase.getLexema() + " implementa a " + imp.getLexema() + " pero es una clase concreta");
            }
        }
    }

    /////////////////////////////CONSOLIDACIÓN
    public void consolidar() throws ExcepcionSemantica {
        if(!consolidado) {
            chequearHerenciaCircular(new HashMap<>());
            consolidarHerencia();
            consolidarImplementaciones();
            consolidado = true;
        }
    }

    private void chequearHerenciaCircular(HashMap<String, Token> clasesAncestro) throws ExcepcionSemantica {
        if (!tokenClase.getLexema().equals("Object")) {
            if (!verificadoHerenciaCircular) {
                if (clasesAncestro.get(heredaDe.getLexema()) != null) {
                    throw new ExcepcionSemantica(heredaDe, "la clase " + heredaDe.getLexema() + " causa herencia circular");
                } else {
                    ClaseConcreta claseAncestro = (ClaseConcreta) TablaDeSimbolos.clases.get(heredaDe.getLexema());
                    clasesAncestro.put(heredaDe.getLexema(), heredaDe);
                    claseAncestro.chequearHerenciaCircular(clasesAncestro);
                }
                verificadoHerenciaCircular = true;
                this.ancestros.putAll(clasesAncestro);
            }
        }
    }

    private void consolidarHerencia() throws ExcepcionSemantica {
        if(!tokenClase.getLexema().equals("Object")) {
            ClaseConcreta claseConcretaAncestro = (ClaseConcreta) TablaDeSimbolos.clases.get(heredaDe.getLexema());
            if (!claseConcretaAncestro.consolidado) {
                claseConcretaAncestro.consolidar();
            }
            consolidarAtributosHeredados(claseConcretaAncestro);
            setOffsetAtributos();
            consolidarMetodosHeredados(claseConcretaAncestro);
            setOffsetMetodos();
        }
    }

    private void consolidarAtributosHeredados(ClaseConcreta claseConcretaAncestro) throws ExcepcionSemantica {
        for (Atributo atAncestro : claseConcretaAncestro.atributos.values()) {
            String lexemaAtAncestro = atAncestro.tokenAtributo.getLexema();
            if (atributos.get(lexemaAtAncestro) == null) {
                atributos.put(lexemaAtAncestro, atAncestro);
            } else {
                throw new ExcepcionSemantica(atributos.get(lexemaAtAncestro).getToken(), "el atributo " + lexemaAtAncestro + " está sobrescrito con distinta signatura");
            }
        }
    }

    private void setOffsetAtributos() {
        offsetDispCIR = ((ClaseConcreta)TablaDeSimbolos.getClase(heredaDe.getLexema())).getOffsetDispCIR();
        for(Atributo atributo: atributos.values()) {
            if(!atributo.offsetAsignado()) {
                atributo.setOffset(offsetDispCIR);
                offsetDispCIR++;
                atributosCIR.put(atributo.offset(), atributo);
            }
        }
    }

    private void consolidarMetodosHeredados(ClaseConcreta claseConcretaAncestro) throws ExcepcionSemantica {
        for (Metodo metAncestro : claseConcretaAncestro.metodos.values()) {
            String lexemaMetAncestro = metAncestro.getTokenMetodo().getLexema();
            if (metodos.get(lexemaMetAncestro) == null) {
                metodos.put(lexemaMetAncestro, metAncestro);
            } else if (!metodos.get(lexemaMetAncestro).mismaSignatura(metAncestro)) {
                throw new ExcepcionSemantica(metodos.get(lexemaMetAncestro).getTokenMetodo(), "el método " + lexemaMetAncestro + " está sobrescrito con distinta signatura");
            }
        }
    }

    private void setOffsetMetodos() {
        if(!tokenClase.getLexema().equals("Object")) {
            ClaseConcreta claseAncestro = (ClaseConcreta) TablaDeSimbolos.getClase(heredaDe.getLexema());
            offsetMetodoDisp = claseAncestro.getOffsetDispVT();
            for (Metodo metodo : metodos.values()) {
                if (!metodo.isMetEstatico()) {
                    Metodo metAncestro = claseAncestro.metodos.get(metodo.getTokenMetodo().getLexema());
                    if (metAncestro == null) {
                        metodo.setOffset(offsetMetodoDisp);
                        offsetMetodoDisp++;
                    } else if (metAncestro != null) {
                        metodo.setOffset(metAncestro.offset);
                    }
                    metodosOrdenadosOffset.put(metodo.offset(), metodo);
                }
            }
        }
    }

    private void consolidarImplementaciones() throws ExcepcionSemantica {
        for(Token tImpl: implementaA.values()) {
            Interface interfaceImpl = (Interface) TablaDeSimbolos.clases.get(tImpl.getLexema());
            if(!interfaceImpl.consolidado) {
                interfaceImpl.consolidar();
            }
            for(Metodo metImpl: interfaceImpl.metodos.values()) {
                String lexemaMetImpl = metImpl.getTokenMetodo().getLexema();
                if(metodos.get(lexemaMetImpl) == null) {
                    throw new ExcepcionSemantica(tImpl, "el método " + lexemaMetImpl + " no fue implementado en " + tokenClase.getLexema());
                } else if(!metodos.get(lexemaMetImpl).mismaSignatura(metImpl)) {
                    if(TablaDeSimbolos.clases.get(heredaDe.getLexema()).getMetodoMismaSignatura(metodos.get(lexemaMetImpl)) == null)
                        throw new ExcepcionSemantica(metodos.get(lexemaMetImpl).getTokenMetodo(), "el método " + lexemaMetImpl + " fue declarado con distinta signatura a la de la interface " + interfaceImpl.tokenClase.getLexema() + " en " + tokenClase.getLexema());
                    else
                        throw new ExcepcionSemantica(metodos.get(lexemaMetImpl).getTokenMetodo(), "el método " + lexemaMetImpl + " fue declarado con distinta signatura a la de la interface " + interfaceImpl.tokenClase.getLexema() + " en alguna clase ancestro");
                }
            }
        }
    }

    private void acomodarOffsetMetodosImpl() {
        for (Token t : implementaA.values()) {
            Interface interf = (Interface) TablaDeSimbolos.getClase(t.getLexema());
            for (Metodo metInterf : interf.metodos.values()) {
                Metodo met = metodos.get(metInterf.getTokenMetodo().getLexema());
                if (met != null) {
                    if (metInterf.offset != met.offset) {
                        met.modificarOffsetMetodoEnClases();
                    }
                }
            }
        }
    }

    /////////////////////////////CHEQUEO DE SENTENCIAS
    public void chequearSentencias() throws ExcepcionSemantica {
        acomodarOffsetMetodosImpl();
        TablaDeSimbolos.claseActual = this;
        constructorClase.chequearSentencias();
        for(Metodo met: metodos.values()) {
            if(met.getTokenClaseDecMetodo().getLexema().equals(tokenClase.getLexema())) {
                met.chequearSentencias();
            }
        }
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

    public Constructor existeConstructor(List<NodoExpresion> argsActuales) throws ExcepcionSemantica {
        if(constructorClase.conformanParametros(argsActuales)) {
            return constructorClase;
        }
        return null;
    }

    @Override
    public HashMap<String, Token> getPosiblesSubtipos() {
        for(Token t: implementaA.values()) {
            posiblesSubtipos.put(t.getLexema(), t);
            posiblesSubtipos.putAll(TablaDeSimbolos.clases.get(t.getLexema()).getPosiblesSubtipos());
        }
        if(heredaDe != null) {
            posiblesSubtipos.put(heredaDe.getLexema(), heredaDe);
            posiblesSubtipos.putAll(TablaDeSimbolos.clases.get(heredaDe.getLexema()).getPosiblesSubtipos());
        }
        return posiblesSubtipos;
    }


    /////////////////////////////GENERACIÓN DE CÓDIGO INTERMEDIO
    public void generar() {
        TablaDeSimbolos.claseActual = this;
        //VTable
        TablaDeSimbolos.codigo.add("\n.DATA");
        if(metodosOrdenadosOffset.size() == 0) {
            TablaDeSimbolos.codigo.add(getLabelVT() + ": NOP");
        } else {
            String etiquetasMetVT = " ";
            for(int i = 0; i < offsetMetodoDisp; i++) {
                if(metodosOrdenadosOffset.get(i) != null) {
                    etiquetasMetVT += metodosOrdenadosOffset.get(i).getLabel() + ",";
                }
                else
                    etiquetasMetVT += "0,";
            }
            etiquetasMetVT = etiquetasMetVT.substring(0, etiquetasMetVT.length()-1);
            TablaDeSimbolos.codigo.add(getLabelVT() + ": DW" + etiquetasMetVT);
        }

        TablaDeSimbolos.codigo.add("\n.CODE");
        constructorClase.generar();
        for(Metodo met: metodos.values()) {
            if(met.getTokenClaseDecMetodo().getLexema().equals(tokenClase.getLexema())) {
                met.generar();
            }
        }
    }

    /////////////////////////////GETTERS Y SETTERS
    public int getOffsetDispCIR() {
        return offsetDispCIR;
    }

    public int getOffsetDispVT() {
        return offsetMetodoDisp;
    }

    public int getCantAtributos() {
        return atributos.size();
    }

    @Override
    public int getOffsetMetodoDisp() {
        return offsetMetodoDisp;
    }

    @Override
    public void setOffsetMetodoDisp(int offset) {
        offsetMetodoDisp = offset;
    }

    public void setHeredaDe(Token tokenAncestro) {
        this.heredaDe = tokenAncestro;
    }

    public void addInterface(Token tokenInterf) throws ExcepcionSemantica {
        if(implementaA.get(tokenInterf.getLexema()) == null) {
            implementaA.put(tokenInterf.getLexema(), tokenInterf);
        } else {
            throw new ExcepcionSemantica(tokenInterf, "la interface " + tokenInterf.getLexema() + " ya se encuentra en la signatura de la clase " + tokenClase.getLexema());
        }
    }

    @Override
    public HashMap<String, Token> getAncestros() {
        return ancestros;
    }

    public Atributo getAtributo(Token token) {
        if(atributos.get(token.getLexema()) != null) {
            return atributos.get(token.getLexema());
        } else {
            return null;
        }
    }

    @Override
    public boolean esClaseConcreta() {
        return true;
    }

    public void insertarAtributo(Atributo at) throws ExcepcionSemantica {
        if(atributos.get(at.tokenAtributo.getLexema()) == null) {
            atributos.put(at.tokenAtributo.getLexema(), at);
        } else {
            throw new ExcepcionSemantica(at.tokenAtributo, "el atributo " + at.tokenAtributo.getLexema() + " ya está declarado en la clase " + tokenClase.getLexema());
        }
    }

    public void insertarMetodo(Metodo met) throws ExcepcionSemantica {
        if(metodos.get(met.getTokenMetodo().getLexema()) == null) {
            metodos.put(met.getTokenMetodo().getLexema(), met);
        } else {
            throw new ExcepcionSemantica(met.getTokenMetodo(), "el método " + met.getTokenMetodo().getLexema() + " ya está declarado en la clase " + tokenClase.getLexema());
        }
    }

    public void insertarConstructor(Constructor constructor) throws ExcepcionSemantica {
        if(constructorClase == null) {
            if (constructor.token.getLexema().equals(tokenClase.getLexema())) {
                constructorClase = constructor;
            } else {
                throw new ExcepcionSemantica(constructor.token, "no coincide el nombre del constructor " + constructor.token.getLexema() + " con el de la clase " + tokenClase.getLexema());
            }
        } else {
            throw new ExcepcionSemantica(constructor.token, "la clase " + tokenClase.getLexema() + " ya tenía un constructor definido");
        }
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
}
