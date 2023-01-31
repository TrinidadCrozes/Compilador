package minijavaCompiler.semantic.tipos;

public class TipoInt extends TipoPrimitivo {

    @Override
    public String getNombreTipo() {
        return "int";
    }

    @Override
    public boolean esIgualTipo(TipoMetodo tipoComp) {
        return tipoComp.getNombreTipo().equals("int");
    }

    @Override
    public boolean esSubtipo(TipoMetodo tipoAncestro) {
        return tipoAncestro.visit(this);
    }

    @Override
    public boolean visit(TipoInt tipo) {
        return true;
    }

}
