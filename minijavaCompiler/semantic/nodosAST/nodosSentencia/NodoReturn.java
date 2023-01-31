package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.*;
import minijavaCompiler.semantic.nodosAST.nodosExpresion.NodoExpresion;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.tipos.TipoVoid;

public class NodoReturn extends NodoSentencia {

    Token tokenReturn;
    NodoExpresion expresionReturn;
    int cantVarLocalesALiberar;

    public NodoReturn(Token tokenReturn, NodoExpresion expresion) {
        this.tokenReturn = tokenReturn;
        this.expresionReturn = expresion;
    }

    @Override
    public void chequear() throws ExcepcionSemantica {
        MetodoOConstructor metodoOConstructor = TablaDeSimbolos.metodoOConstructorActual;
        TipoMetodo tipoRetorno = metodoOConstructor.getTipoRetorno();
        if(expresionReturn == null) {
            if(!(tipoRetorno.esIgualTipo(new TipoVoid())))
                throw new ExcepcionSemantica(tokenReturn, "no retorna ninguna expresión y se esperaba un retorno de tipo " + tipoRetorno.getNombreTipo());
        } else {
            if(tipoRetorno.esIgualTipo(new TipoVoid())) {
                throw new ExcepcionSemantica(tokenReturn, "el tipo es void e intenta retornar una expresión");
            }
            TipoMetodo tipoExpresion = expresionReturn.chequear();
            if(!(tipoExpresion.esSubtipo(tipoRetorno))) {
                throw new ExcepcionSemantica(tokenReturn, "no conforma el tipo de la expresión a retornar con el tipo de retorno del método");
            }
        }

        cantVarLocalesALiberar = TablaDeSimbolos.pilaBloquesActuales.get(0).getCantVarLocalesUsadas();
    }

    @Override
    public void generar() {
        MetodoOConstructor metodoOConstructor = TablaDeSimbolos.metodoOConstructorActual;
        TablaDeSimbolos.codigo.add("FMEM " + cantVarLocalesALiberar);
        if(metodoOConstructor.getTipoRetorno().esIgualTipo(new TipoVoid())) {
            TablaDeSimbolos.codigo.add("STOREFP  ; Actualizar el fp para que apunte al RA del llamador");
            TablaDeSimbolos.codigo.add("RET " + metodoOConstructor.getCantLiberarRetorno());
        } else {
            expresionReturn.generar();
            TablaDeSimbolos.codigo.add("STORE " + metodoOConstructor.getOffsetLugarRetorno());
            TablaDeSimbolos.codigo.add("STOREFP  ; Actualizar el fp para que apunte al RA del llamador");
            TablaDeSimbolos.codigo.add("RET " + metodoOConstructor.getCantLiberarRetorno());
        }
    }
}
