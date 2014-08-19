package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2operation;

public interface IAST4operation
extends IAST4expression, IAST2operation<CEASTparseException> {
    IAST2operation<CEASTparseException> getDelegate ();
}
