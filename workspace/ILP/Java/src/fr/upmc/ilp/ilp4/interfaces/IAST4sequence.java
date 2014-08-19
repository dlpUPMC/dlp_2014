package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2sequence;

public interface IAST4sequence
extends IAST4instruction, IAST2sequence<CEASTparseException> {
    IAST4expression   getInstruction (int i) throws CEASTparseException;
    IAST4expression[] getInstructions ();
}
