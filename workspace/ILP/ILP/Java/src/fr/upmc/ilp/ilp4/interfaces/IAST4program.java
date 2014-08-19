package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2program;
import fr.upmc.ilp.ilp4.ast.NormalizeException;

public interface IAST4program
extends IAST4, IAST2program<CEASTparseException> {
    IAST4functionDefinition[] getFunctionDefinitions ();
    IAST4expression           getBody ();
    IAST4globalVariable[]     getGlobalVariables ();
    
    // analyses statiques
    void computeGlobalVariables();
    void setGlobalVariables (IAST4globalVariable[] globals);
    IAST4program normalize (
            INormalizeLexicalEnvironment lexenv,
            INormalizeGlobalEnvironment common,
            IAST4Factory factory )
      throws NormalizeException;
}
