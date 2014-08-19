package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2binaryOperation;

public interface IAST4binaryOperation
extends IAST4operation, IAST2binaryOperation<CEASTparseException> {
    IAST4expression getLeftOperand ();
    IAST4expression getRightOperand ();
}
