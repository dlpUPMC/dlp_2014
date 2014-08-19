package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4instruction;

public abstract class CEASTdelegableInstruction 
extends CEASTdelegableExpression
implements IAST4instruction {

    //@Override
    //public abstract IAST2instruction getDelegate ();
    // NOTE: pas possible du point de vue typage du fait de l'inversion de
    // positionnement entre expression et instruction.

    @Override
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common,
                         final IDestination destination)
    throws CgenerationException {
        final IAST2instruction<CEASTparseException> delegate =
            (IAST2instruction<CEASTparseException>) getDelegate();
        delegate.compileInstruction(buffer, lexenv, common, destination);
    }
}
