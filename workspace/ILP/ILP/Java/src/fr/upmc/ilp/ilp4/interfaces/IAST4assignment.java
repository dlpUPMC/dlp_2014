package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2assignment;

public interface IAST4assignment
extends IAST4expression, IAST2assignment<CEASTparseException> {
    IAST4variable   getVariable ();
    IAST4expression getValue ();
}
