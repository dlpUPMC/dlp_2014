package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4expression;

public interface IAST6writeField 
extends IAST6visitable, IAST6expression {
    IAST4expression getObject ();
    String getFieldName ();
    IAST4expression getValue ();
}
