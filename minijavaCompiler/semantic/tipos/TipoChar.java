package minijavaCompiler.semantic.tipos;

public class TipoChar extends TipoPrimitivo {

    @Override
    public String getNombreTipo() {
        return "char";
    }

    @Override
    public boolean esIgualTipo(TipoMetodo tipoComp) {
        return tipoComp.getNombreTipo().equals("char");
    }

    @Override
    public boolean esSubtipo(TipoMetodo tipoAncestro) {
        return tipoAncestro.visit(this);
    }

    @Override
    public boolean visit(TipoChar tipo) {
        return true;
    }

}
