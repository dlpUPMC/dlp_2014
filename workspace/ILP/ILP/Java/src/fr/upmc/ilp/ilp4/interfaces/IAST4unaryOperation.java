package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryOperation;

public interface IAST4unaryOperation
extends IAST4operation, IAST2unaryOperation<CEASTparseException> {
    IAST4expression getOperand ();
}
