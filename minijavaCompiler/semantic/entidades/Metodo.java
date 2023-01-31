package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoBloque;
import minijavaCompiler.semantic.tipos.TipoMetodo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Metodo extends MetodoOConstructor {

    Token tokenClaseDecMetodo;
    int offset;
    HashMap<String, Clase> clasesDondeSeImplementa;

    public Metodo(Token tokenMetodo, boolean metEstatico, TipoMetodo tipoRetorno, Token claseMetodo) {
        super();
        this.token = tokenMetodo;
        this.metEstatico = metEstatico;
        this.tipoRetorno = tipoRetorno;
        this.mapParametros = new HashMap<>();
        this.parametros = new ArrayList<>();
        this.tokenClaseDecMetodo = claseMetodo;
        this.bloque = new NodoBloque();
        this.offset = -1;
        this.clasesDondeSeImplementa = new HashMap<>();
        if(this.metEstatico) {
            this.offsetParamsDisp = 3;
        } else {
            this.offsetParamsDisp = 4;
        }
    }

    public void chequearDeclaraciones() throws ExcepcionSemantica {
        tipoRetorno.verificarExistenciaTipo();
        chequearParametros();
    }

    private void chequearParametros() throws ExcepcionSemantica {
        for(int i = parametros.size()-1; i > -1; i--) {
            parametros.get(i).chequearDeclaraciones();
            parametros.get(i).setOffset(offsetParamsDisp);
            offsetParamsDisp++;
        }
    }

    public boolean mismaSignatura(Metodo met) {
        boolean mismoNombre = token.getLexema().equals(met.getTokenMetodo().getLexema());
        boolean mismaForma = metEstatico == met.getMetEstatico();
        boolean mismoRetorno = tipoRetorno.getNombreTipo().equals(met.getTipoRetorno().getNombreTipo());
        boolean mismosParametros = compararParametros(met.getParametros());
        return mismoNombre && mismaForma && mismoRetorno && mismosParametros;
    }

    private boolean compararParametros(List<ParametroFormal> parametrosComp) {
        boolean iguales = false;
        if(parametros.size() == parametrosComp.size()) {
            if(parametros.size() == 0)
                iguales = true;
            else {
                int i = 0;
                for (ParametroFormal p1 : parametros) {
                    ParametroFormal p2 = parametrosComp.get(i);
                    i++;
                    if (!(p1.getTipoParametro().getNombreTipo().equals(p2.getTipoParametro().getNombreTipo()))) {
                        iguales = false;
                        break;
                    } else {
                        iguales = true;
                    }
                }
            }
        }
        return iguales;
    }

    public Token getTokenMetodo() {
        return token;
    }

    public boolean getMetEstatico() {
        return metEstatico;
    }

    public Token getTokenClaseDecMetodo() {
        return tokenClaseDecMetodo;
    }

    public void insertarBloque(NodoBloque bloque) {
        this.bloque = bloque;
    }

    public void insertarParametro(ParametroFormal param) throws ExcepcionSemantica {
        String lexParam = param.tokenParametro.getLexema();
        if(mapParametros.get(lexParam) == null) {
            mapParametros.put(lexParam, param);
            parametros.add(param);
        } else {
            throw new ExcepcionSemantica(param.getToken(), "el parámetro " + lexParam + " del método " + token.getLexema() + " ya estaba declarado");
        }
    }

    @Override
    public String getLabel() {
        return "lblMet" + token.getLexema() + "@" + tokenClaseDecMetodo.getLexema();
    }

    public boolean offsetAsignado() {
        return offset != -1;
    }

    public int offset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void insertarClaseDondeSeImplementa() {
        for(Clase clase: TablaDeSimbolos.clases.values()) {
            Metodo met = clase.metodos.get(token.getLexema());
            if(met != null) {
                if (met.mismaSignatura(this))
                    clasesDondeSeImplementa.put(clase.tokenClase.getLexema(), clase);
            }
        }
    }

    public void modificarOffsetMetodoEnClases(){
        this.insertarClaseDondeSeImplementa();
        int mayorOffset = 0;
        for(Clase clase: clasesDondeSeImplementa.values()) {
           if (clase.offsetMetodoDisp > mayorOffset)
               mayorOffset = clase.offsetMetodoDisp;
        }
        for(Clase clase: clasesDondeSeImplementa.values()) {
            Metodo metEnClase = clase.metodos.get(token.getLexema());
            if(metEnClase != null) {
                clase.metodosOrdenadosOffset.remove(metEnClase.offset());
                metEnClase.setOffset(mayorOffset);
                clase.metodosOrdenadosOffset.put(metEnClase.offset(), metEnClase);
                clase.setOffsetMetodoDisp(mayorOffset + 1);
            }
        }
    }
}
