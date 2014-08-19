package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp3.IAST3try;

public interface IAST4try
extends IAST4instruction, IAST3try<CEASTparseException> {
    IAST4expression getBody ();
    IAST4variable   getCaughtExceptionVariable ();
    IAST4expression getCatcher ();
    IAST4expression getFinallyer ();
}
