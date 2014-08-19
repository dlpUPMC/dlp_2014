package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryBlock;

public interface IAST4unaryBlock
extends IAST4instruction, IAST2unaryBlock<CEASTparseException> {
    IAST4variable       getVariable ();
    // Version mieux typee de la precedente:
    IAST4localVariable  getLocalVariable ();
    IAST4expression     getInitialization ();
    IAST4expression     getBody ();

}
