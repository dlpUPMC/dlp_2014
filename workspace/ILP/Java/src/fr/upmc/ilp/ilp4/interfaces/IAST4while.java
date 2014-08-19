package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2while;

public interface IAST4while
extends IAST4instruction, IAST2while<CEASTparseException> {
    IAST4expression getCondition ();
    IAST4expression getBody ();
}
