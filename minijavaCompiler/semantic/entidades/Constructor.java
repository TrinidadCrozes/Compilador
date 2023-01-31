package minijavaCompiler.semantic.entidades;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.nodosAST.nodosSentencia.NodoBloque;
import minijavaCompiler.semantic.tipos.TipoVoid;

import javax.swing.text.TabableView;
import java.util.ArrayList;
import java.util.HashMap;

public class Constructor extends MetodoOConstructor {

    public Constructor(Token token) {
        super();
        this.token = token;
        this.tipoRetorno = new TipoVoid();
        this.metEstatico = false;
        this.mapParametros = new HashMap<>();
        this.parametros = new ArrayList<>();
        this.bloque = new NodoBloque();
        this.offsetParamsDisp = 4;
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

    public void insertarBloque(NodoBloque bloque) {
        this.bloque = bloque;
    }

    @Override
    public String getLabel() {
        return "lblConstructor@" + token.getLexema();
    }

    public void chequearDeclaraciones() throws ExcepcionSemantica {
        for(int i = parametros.size()-1; i > -1; i--) {
            parametros.get(i).chequearDeclaraciones();
            parametros.get(i).setOffset(offsetParamsDisp);
            offsetParamsDisp++;
        }
    }

}
