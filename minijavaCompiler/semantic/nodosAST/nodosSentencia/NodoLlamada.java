package minijavaCompiler.semantic.nodosAST.nodosSentencia;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantic.entidades.ExcepcionSemantica;
import minijavaCompiler.semantic.entidades.TablaDeSimbolos;
import minijavaCompiler.semantic.nodosAST.nodosAcceso.NodoAcceso;
import minijavaCompiler.semantic.tipos.Tipo;
import minijavaCompiler.semantic.tipos.TipoMetodo;
import minijavaCompiler.semantic.tipos.TipoVoid;

public class NodoLlamada extends NodoSentencia {

    NodoAcceso acceso;
    TipoMetodo tipoLlamada;
    Token puntoYComa;
    public NodoLlamada(NodoAcceso nodoAcceso, Token tokenPuntoYComa) {
        this.acceso = nodoAcceso;
        this.puntoYComa = tokenPuntoYComa;
    }

    @Override
    public void chequear() throws ExcepcionSemantica {
        tipoLlamada = acceso.chequear();

        if(!acceso.esLlamable()) {
            throw new ExcepcionSemantica(puntoYComa, "se esperaba una llamada a un método o constructor");
        }
    }

    @Override
    public void generar() {
        acceso.generar();
        if(!tipoLlamada.esIgualTipo(new TipoVoid())) {
            TablaDeSimbolos.codigo.add("POP  ; La llamada devolvió algo distinto de void ->  se descarta");
        }
    }
}
