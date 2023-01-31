package minijavaCompiler.lexical;

import java.util.HashMap;

public class PalabrasClave {

    HashMap<String, TipoDeToken> mapPalabrasClave;

    public PalabrasClave() {
        mapPalabrasClave = new HashMap<>();
        mapPalabrasClave.put("class", TipoDeToken.pcClass);
        mapPalabrasClave.put("interface", TipoDeToken.pcInterface);
        mapPalabrasClave.put("extends", TipoDeToken.pcExtends);
        mapPalabrasClave.put("implements", TipoDeToken.pcImplements);
        mapPalabrasClave.put("public", TipoDeToken.pcPublic);
        mapPalabrasClave.put("private", TipoDeToken.pcPrivate);
        mapPalabrasClave.put("static", TipoDeToken.pcStatic);
        mapPalabrasClave.put("void", TipoDeToken.pcVoid);
        mapPalabrasClave.put("boolean", TipoDeToken.pcBoolean);
        mapPalabrasClave.put("char", TipoDeToken.pcChar);
        mapPalabrasClave.put("int", TipoDeToken.pcInt);
        mapPalabrasClave.put("if", TipoDeToken.pcIf);
        mapPalabrasClave.put("else", TipoDeToken.pcElse);
        mapPalabrasClave.put("while", TipoDeToken.pcWhile);
        mapPalabrasClave.put("return", TipoDeToken.pcReturn);
        mapPalabrasClave.put("var", TipoDeToken.pcVar);
        mapPalabrasClave.put("this", TipoDeToken.pcThis);
        mapPalabrasClave.put("new", TipoDeToken.pcNew);
        mapPalabrasClave.put("null", TipoDeToken.pcNull);
        mapPalabrasClave.put("true", TipoDeToken.pcTrue);
        mapPalabrasClave.put("false", TipoDeToken.pcFalse);
    }

    public TipoDeToken getPalabraClave(String token) {
        return mapPalabrasClave.get(token);
    }

}
