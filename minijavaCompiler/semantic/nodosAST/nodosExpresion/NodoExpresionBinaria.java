package minijavaCompiler.semantic.nodosAST.nodosExpresion;

import minijavaCompiler.lexical.TipoDeToken;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.tipos.TipoBoolean;
import minijavaCompiler.semantic.tipos.TipoInt;
import minijavaCompiler.semantic.tipos.TipoMetodo;

import javax.swing.text.TabableView;

public class NodoExpresionBinaria extends NodoExpresion {

    Token tokenOperadorBinario;
    NodoExpresion nodoExpresionDer;
    NodoExpresion nodoExpresionIzq;

    public NodoExpresionBinaria(Token tokenOperadorBinario) {
        this.tokenOperadorBinario = tokenOperadorBinario;
    }

    public void setNodosExpresiones(NodoExpresion nodoExpresionDer, NodoExpresion nodoExpresionIzq) {
        this.nodoExpresionDer = nodoExpresionDer;
        this.nodoExpresionIzq = nodoExpresionIzq;
    }

    public TipoMetodo chequear() throws ExcepcionSemantica {
        if (tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opAnd) ||
                tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opOr)) {
            return chequearAndOr();
        } else if (tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opIgual) ||
                tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opDistinto)) {
            return chequearIgualDist();
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opMayor) ||
                tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opMayorIgual) ||
                tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opMenor) ||
                tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opMenorIgual)) {
            return chequearOpComparacion();
        } else { //operadores +, -, *, /, %
            return chequearOpNumericas();
        }
    }

    private TipoBoolean chequearIgualDist() throws ExcepcionSemantica {
        TipoMetodo tipoExpDer = nodoExpresionDer.chequear();
        TipoMetodo tipoExpIzq = nodoExpresionIzq.chequear();
        if (tipoExpDer.esSubtipo(tipoExpIzq) || tipoExpIzq.esSubtipo(tipoExpDer)) {
            return new TipoBoolean();
        } else {
            throw new ExcepcionSemantica(tokenOperadorBinario, "el tipo " + tipoExpDer.getNombreTipo() + " es incompatible con el tipo " + tipoExpIzq.getNombreTipo() + " para el operador " + tokenOperadorBinario.getLexema());
        }
    }

    private TipoBoolean chequearOpComparacion() throws ExcepcionSemantica {
        TipoMetodo tipoExpDer = nodoExpresionDer.chequear();
        TipoMetodo tipoExpIzq = nodoExpresionIzq.chequear();
        if(tipoExpDer.esIgualTipo(new TipoInt())) {
            if(tipoExpIzq.esIgualTipo(new TipoInt())) {
                return new TipoBoolean();
            } else {
                throw new ExcepcionSemantica(tokenOperadorBinario, "el tipo " + tipoExpIzq.getNombreTipo() + " es incompatible con el operador " + tokenOperadorBinario.getLexema());
            }
        } else {
            throw new ExcepcionSemantica(tokenOperadorBinario, "el tipo " + tipoExpDer.getNombreTipo() + " es incompatible con el operador " + tokenOperadorBinario.getLexema());
        }
    }

    private TipoInt chequearOpNumericas() throws ExcepcionSemantica {
        TipoMetodo tipoExpDer = nodoExpresionDer.chequear();
        TipoMetodo tipoExpIzq = nodoExpresionIzq.chequear();
        if(tipoExpDer.esIgualTipo(new TipoInt())) {
            if(tipoExpIzq.esIgualTipo(new TipoInt())) {
                return new TipoInt();
            } else {
                throw new ExcepcionSemantica(tokenOperadorBinario, "el tipo " + tipoExpIzq.getNombreTipo() + " es incompatible con el operador " + tokenOperadorBinario.getLexema());
            }
        } else {
            throw new ExcepcionSemantica(tokenOperadorBinario, "el tipo " + tipoExpDer.getNombreTipo() + " es incompatible con el operador " + tokenOperadorBinario.getLexema());
        }
    }

    private TipoBoolean chequearAndOr() throws ExcepcionSemantica {
        TipoMetodo tipoExpDer = nodoExpresionDer.chequear();
        TipoMetodo tipoExpIzq = nodoExpresionIzq.chequear();
        if (tipoExpDer.esIgualTipo(new TipoBoolean())) {
            if (tipoExpIzq.esIgualTipo(new TipoBoolean())) {
                return new TipoBoolean();
            } else {
                throw new ExcepcionSemantica(tokenOperadorBinario, "el tipo " + tipoExpIzq.getNombreTipo() + " es incompatible con el operador " + tokenOperadorBinario.getLexema());
            }
        } else {
            throw new ExcepcionSemantica(tokenOperadorBinario, "el tipo " + tipoExpDer.getNombreTipo() + " es incompatible con el operador " + tokenOperadorBinario.getLexema());
        }
    }

    @Override
    public void generar() {
        nodoExpresionIzq.generar();
        nodoExpresionDer.generar();
        if (tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opIgual)) {
            TablaDeSimbolos.codigo.add("EQ");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opDistinto)) {
            TablaDeSimbolos.codigo.add("NE");
        }else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opProducto)) {
            TablaDeSimbolos.codigo.add("MUL");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opSuma)) {
            TablaDeSimbolos.codigo.add("ADD");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opResta)) {
            TablaDeSimbolos.codigo.add("SUB");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opDivision)) {
            TablaDeSimbolos.codigo.add("DIV");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opModulo)) {
            TablaDeSimbolos.codigo.add("MOD");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opAnd)) {
            TablaDeSimbolos.codigo.add("AND");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opOr)) {
            TablaDeSimbolos.codigo.add("OR");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opMenor)) {
            TablaDeSimbolos.codigo.add("LT");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opMayor)) {
            TablaDeSimbolos.codigo.add("GT");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opMenorIgual)) {
            TablaDeSimbolos.codigo.add("LE");
        } else if(tokenOperadorBinario.getTipoDeToken().equals(TipoDeToken.opMayorIgual)) {
            TablaDeSimbolos.codigo.add("GE");
        }

    }

}
