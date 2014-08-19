package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4expression;

public interface IAST6instantiation
extends IAST6visitable, IAST6expression {
    String getClassName ();
    IAST4expression[] getArguments ();
}
