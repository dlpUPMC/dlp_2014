package fr.upmc.ilp.ilp5.ast;

import fr.upmc.ilp.ilp4.ast.CEASTFactory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp5.interfaces.IAST5Factory;
import fr.upmc.ilp.ilp5.interfaces.IAST5codefinedLocalFunctions;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionDefinition;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionVariable;

public class CEAST5Factory extends CEASTFactory
implements IAST5Factory {

    public IAST5codefinedLocalFunctions newCodefinedLocalFunctions(
            IAST5localFunctionDefinition[] fns,
            IAST4expression body ) {
        return new EASTcodefinedLocalFunctions(fns, body);
    }

    public IAST5localFunctionDefinition newLocalFunctionDefinition(
            IAST5localFunctionVariable fnv,
            IAST4variable[] variables,
            IAST4expression body ) {
        return new EASTlocalFunctionDefinition(fnv, variables, body);
    }

    public IAST5localFunctionVariable newLocalFunctionVariable(String name) {
        return new EASTlocalFunctionVariable(name);
    }
}
