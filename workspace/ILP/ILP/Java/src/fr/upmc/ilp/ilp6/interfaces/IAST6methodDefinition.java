package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;

public interface IAST6methodDefinition
extends IAST4functionDefinition, IAST6visitable {

    String getMethodName ();
    void setDefiningClass (IAST6classDefinition classDefinition);
    int getRealArity ();
}

// end of IAST6methodDefinition.java
