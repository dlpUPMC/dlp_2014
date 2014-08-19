package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2reference;

public interface IAST4reference
extends IAST4expression, IAST2reference<CEASTparseException> {
    /** Retourne la variable lue */
    IAST4variable getVariable();
}
