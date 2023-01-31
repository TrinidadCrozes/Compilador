package minijavaCompiler.semantic.tipos;

public class TipoVoid extends TipoMetodo {

    @Override
    public String getNombreTipo() {
        return "void";
    }

    @Override
    public boolean esIgualTipo(TipoMetodo tipoComp) {
        return tipoComp.getNombreTipo().equals("void");
    }

    @Override
    public boolean esSubtipo(TipoMetodo tipoAncestro) {
        return tipoAncestro.visit(this);
    }

    @Override
    public boolean visit(TipoVoid tipo) {
        return tipo.esIgualTipo(new TipoVoid());
    }

}
