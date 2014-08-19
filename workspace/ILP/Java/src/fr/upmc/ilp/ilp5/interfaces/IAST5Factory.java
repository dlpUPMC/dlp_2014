package fr.upmc.ilp.ilp5.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;

public interface IAST5Factory
extends IAST4Factory
{
    IAST5codefinedLocalFunctions newCodefinedLocalFunctions (
            IAST5localFunctionDefinition[] fns,
            IAST4expression body );
    IAST5localFunctionDefinition newLocalFunctionDefinition(
            IAST5localFunctionVariable fnv,
            IAST4variable[] variables,
            IAST4expression body );
    IAST5localFunctionVariable newLocalFunctionVariable(String name);
}
