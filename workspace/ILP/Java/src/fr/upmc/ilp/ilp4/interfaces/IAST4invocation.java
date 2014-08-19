package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2invocation;

public interface IAST4invocation
extends IAST4expression, IAST2invocation<CEASTparseException> {
    IAST4expression   getFunction ();
    IAST4expression[] getArguments ();
    IAST4expression getArgument (int i);
}
