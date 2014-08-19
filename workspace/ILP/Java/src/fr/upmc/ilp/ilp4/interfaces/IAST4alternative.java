package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2alternative;

public interface IAST4alternative
extends IAST4instruction, IAST2alternative<CEASTparseException> {

    IAST4expression getCondition ();
    IAST4expression getConsequent ();
    IAST4expression getAlternant ();

}
