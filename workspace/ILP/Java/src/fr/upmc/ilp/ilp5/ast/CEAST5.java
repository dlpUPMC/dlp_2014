package fr.upmc.ilp.ilp5.ast;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.CEAST;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp5.interfaces.IAST5Factory;

public abstract class CEAST5 extends CEAST {

    public IAST2<CEASTparseException> getDelegate() {
       throw new RuntimeException("No delegate!");
    }

    @Override
    public Object eval(ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException {
        throw new RuntimeException("Evaluation not implemented!");
    }

    public static IAST5Factory narrowToIAST5Factory(IAST4Factory factory) {
        if ( factory instanceof IAST5Factory ) {
            return (IAST5Factory) factory;
        } else {
            final String msg = "Not an IAST5Factory " + factory;
            throw new ClassCastException(msg);
        }
    }
}
