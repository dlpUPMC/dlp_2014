package fr.upmc.ilp.ilp5.interfaces;

import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

/** Définir une fonction locale non anonyme. */

public interface IAST5localFunctionDefinition
extends IAST4, IAST5visitable {
    String                     getFunctionName();
    IAST5localFunctionVariable getDefinedVariable();
    IAST4variable[]            getVariables ();
    IAST4expression            getBody ();
    
    IAST5localFunctionDefinition normalize(
            INormalizeLexicalEnvironment lexenv,
            INormalizeGlobalEnvironment common,
            IAST5Factory factory )
    throws NormalizeException; 
}
