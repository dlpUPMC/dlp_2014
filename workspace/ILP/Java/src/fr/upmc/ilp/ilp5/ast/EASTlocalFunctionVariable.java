package fr.upmc.ilp.ilp5.ast;

import fr.upmc.ilp.ilp4.ast.CEASTlocalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionVariable;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitor;

/** Une variable locale nommant une fonction locale. */

public class EASTlocalFunctionVariable
extends CEASTlocalVariable
implements IAST5localFunctionVariable {

    public EASTlocalFunctionVariable (final String name) {
        super(name);
    }

    /* normalize() est heritee. */

    public <Data, Result, Exc extends Throwable> Result 
     accept (IAST5visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
    @Override
    public <Data, Result, Exc extends Throwable> Result 
     accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return CEAST5expression.narrowToIAST5visitor(visitor).visit(this, data);
    }
    // NOTE: double methode surchargee.
}
