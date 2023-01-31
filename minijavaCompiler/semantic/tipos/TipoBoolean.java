package minijavaCompiler.semantic.tipos;

public class TipoBoolean extends TipoPrimitivo {

    @Override
    public String getNombreTipo() {
        return "boolean";
    }

    @Override
    public boolean esIgualTipo(TipoMetodo tipoComp) {
        return tipoComp.getNombreTipo().equals("boolean");
    }

    @Override
    public boolean esSubtipo(TipoMetodo tipoAncestro) {
        return tipoAncestro.visit(this);
    }

    @Override
    public boolean visit(TipoBoolean tipo) {
        return true;
    }

}
