package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2localBlock;

public interface IAST4localBlock
extends IAST4instruction, IAST2localBlock<CEASTparseException> {
    IAST4variable[]       getVariables ();
    // La version mieux typee de la precedente:
    IAST4localVariable[]  getLocalVariables ();
    IAST4expression[]     getInitializations ();
    IAST4expression       getBody ();
}
