package fr.upmc.ilp.ilp3;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;

public interface IAST3try<Exc extends Exception>
extends IAST2instruction<Exc> {
    IAST2instruction<CEASTparseException> getBody ();
    @OrNull IAST2variable getCaughtExceptionVariable ();
    @OrNull IAST2instruction<CEASTparseException> getCatcher ();
    @OrNull IAST2instruction<CEASTparseException> getFinallyer ();
}
