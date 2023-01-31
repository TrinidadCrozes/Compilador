package minijavaCompiler.lexical;

public enum TipoDeToken {

    pcClass,
    pcInterface,
    pcExtends,
    pcImplements,
    pcPublic,
    pcPrivate,
    pcStatic,
    pcVoid,
    pcBoolean,
    pcChar,
    pcInt,
    pcIf,
    pcElse,
    pcWhile,
    pcReturn,
    pcVar,
    pcThis,
    pcNew,
    pcNull,
    pcTrue,
    pcFalse,

    idClase,
    idMetVar,

    litInt,
    litChar,
    litString,

    puntParentesisIzq,
    puntParentesisDer,
    puntLlaveIzq,
    puntLlaveDer,
    puntPuntoYComa,
    puntComa,
    puntPunto,

    opSuma,
    opResta,
    opProducto,
    opDivision,
    opModulo,
    opIgual,
    opDistinto,
    opNot,
    opMayor,
    opMayorIgual,
    opMenor,
    opMenorIgual,
    opOr,
    opAnd,

    asigAsignacion,
    asigIncremento,
    asigDecremento,

    DUMMY,
    EOF;

}
