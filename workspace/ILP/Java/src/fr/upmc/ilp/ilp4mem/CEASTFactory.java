package fr.upmc.ilp.ilp4mem;

import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4integer;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;

public class CEASTFactory
extends fr.upmc.ilp.ilp4.ast.CEASTFactory {

    public CEASTFactory () {
        super();
    }

    @Override
    public IAST4program newProgram(
            IAST4functionDefinition[] defs, 
            IAST4expression body) {
        return new CEASTprogram(defs, body);
    }

    @Override
    public IAST4integer newIntegerConstant(String value) {
        return new CEASTinteger(value);
    }
}
