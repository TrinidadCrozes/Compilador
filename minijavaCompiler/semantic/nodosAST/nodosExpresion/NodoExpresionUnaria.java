package minijavaCompiler.semantic.nodosAST.nodosExpresion;

import minijavaCompiler.lexical.TipoDeToken;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.tipos.TipoBoolean;
import minijavaCompiler.semantic.tipos.TipoInt;
import minijavaCompiler.semantic.tipos.TipoMetodo;

public class NodoExpresionUnaria extends NodoExpresion {

    Token tokenOperadorUnario;
    NodoOperando operando;

    public NodoExpresionUnaria(Token tokenOperadorUnario, NodoOperando operando) {
        this.tokenOperadorUnario = tokenOperadorUnario;
        this.operando = operando;
    }

    @Override
    public TipoMetodo chequear() throws ExcepcionSemantica {
        if(tokenOperadorUnario != null) {
            if (tokenOperadorUnario.getTipoDeToken().equals(TipoDeToken.opNot)) {
                if (operando.chequear().esIgualTipo(new TipoBoolean())) {
                    return new TipoBoolean();
                } else {
                    throw new ExcepcionSemantica(tokenOperadorUnario, "error de tipos: operador not con operando no booleano");
                }
            } else { //el operadorUnario va a ser + o -
                if (operando.chequear().esIgualTipo(new TipoInt())) {
                    return new TipoInt();
                } else {
                    throw new ExcepcionSemantica(tokenOperadorUnario, "error de tipos: operador " + tokenOperadorUnario.getLexema() + " con operando no entero");
                }
            }
        } else {
            return operando.chequear();
        }
    }

    @Override
    public void generar() {
        operando.generar();
        if(tokenOperadorUnario != null) {
            if (tokenOperadorUnario.getTipoDeToken().equals(TipoDeToken.opNot)) {
                TablaDeSimbolos.codigo.add("NOT");
            } else if (tokenOperadorUnario.getTipoDeToken().equals(TipoDeToken.opResta)) {
                TablaDeSimbolos.codigo.add("NEG");
            }
        }
    }
}
