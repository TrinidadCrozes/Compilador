package minijavaCompiler.semantic.tipos;

public class TipoNull extends TipoPrimitivo {

    @Override
    public String getNombreTipo() {
        return "null";
    }

    @Override
    public boolean esIgualTipo(TipoMetodo tipoComp) {
        return tipoComp instanceof TipoClase;
    }

    @Override
    public boolean esSubtipo(TipoMetodo tipoAncestro) {
        return tipoAncestro instanceof TipoClase;
    }

}
