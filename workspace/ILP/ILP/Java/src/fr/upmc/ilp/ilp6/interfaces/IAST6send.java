package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4expression;

public interface IAST6send extends IAST6visitable, IAST6expression {
    String getMethodName ();
    IAST4expression getReceiver ();
    IAST4expression[] getArguments ();
}
