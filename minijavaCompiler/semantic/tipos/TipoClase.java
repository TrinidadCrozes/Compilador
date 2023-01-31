package minijavaCompiler.semantic.tipos;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;

import javax.print.DocFlavor;
import java.util.HashMap;

public class TipoClase extends Tipo {

    Token tipo;

    public TipoClase(Token tipo) {
        this.tipo = tipo;
    }

    public Token getTipo() {
        return tipo;
    }

    @Override
    public String getNombreTipo() {
        return this.tipo.getLexema();
    }

    @Override
    public boolean esIgualTipo(TipoMetodo tipoComp) {
        return tipoComp.getNombreTipo().equals(this.tipo.getLexema());
    }

    public void verificarExistenciaTipo() throws ExcepcionSemantica {
        if(TablaDeSimbolos.clases.get(tipo.getLexema()) == null) {
            throw new ExcepcionSemantica(tipo, "el tipo clase " + tipo.getLexema() + " no fue declarado");
        }
    }

    @Override
    public boolean esSubtipo(TipoMetodo tipoAncestro) {
        return tipoAncestro.visit(this);
    }

    public boolean visit(TipoClase tipoSubtipo) {
        if(tipoSubtipo.getNombreTipo().equals(this.tipo.getLexema())) {
            return true; //todos los tipos son subtipos de si mismos
        } else {
            Clase claseSubtipo = TablaDeSimbolos.clases.get(tipoSubtipo.getNombreTipo());
            HashMap<String, Token> clasesAncestroSubtipo = claseSubtipo.getPosiblesSubtipos();
            for(Token c: clasesAncestroSubtipo.values()) {
                if (c.getLexema().equals(tipo.getLexema())) {
                    return true;
                }
            }
        }
        return false;
    }

}
