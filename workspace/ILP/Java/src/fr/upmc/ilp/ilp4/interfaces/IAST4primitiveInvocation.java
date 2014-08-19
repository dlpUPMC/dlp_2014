package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2primitiveInvocation;

public interface IAST4primitiveInvocation
extends IAST4invocation,
        IAST2primitiveInvocation<CEASTparseException> {
    IAST4globalVariable getFunctionGlobalVariable ();
}
