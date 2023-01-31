package minijavaCompiler.semantic.tipos;

import minijavaCompiler.semantic.entidades.ExcepcionSemantica;

public abstract class TipoMetodo {

    public abstract String getNombreTipo();

    public void verificarExistenciaTipo() throws ExcepcionSemantica {
    }

    public abstract boolean esIgualTipo(TipoMetodo tipoComp);

    public abstract boolean esSubtipo(TipoMetodo tipoAncestro);

    public boolean visit(TipoVoid tipo){return false;}

    public boolean visit(TipoClase tipo){return false;}

    public boolean visit(TipoChar tipo){return false;}

    public boolean visit(TipoBoolean tipo){return false;}

    public boolean visit(TipoInt tipo){return false;}

}
