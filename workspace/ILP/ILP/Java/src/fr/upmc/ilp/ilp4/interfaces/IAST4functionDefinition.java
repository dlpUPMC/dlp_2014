/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:IAST4functionDefinition.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.interfaces;

import java.util.Set;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp4.ast.NormalizeException;

public interface IAST4functionDefinition
extends IAST4internal, IAST2functionDefinition<CEASTparseException> {
    IAST4globalFunctionVariable getDefinedVariable ();
    IAST4variable[]             getVariables ();
    // Version mieux typee de la precedente:
    IAST4localVariable[]        getLocalVariables ();
    IAST4expression             getBody ();
    boolean                     isRecursive ();
    Set<IAST4globalFunctionVariable> getInvokedFunctions ();
    void setInvokedFunctions(Set<IAST4globalFunctionVariable> funvars);
    
    IAST4functionDefinition normalize(
            INormalizeLexicalEnvironment lexenv,
            INormalizeGlobalEnvironment common,
            IAST4Factory factory )
    throws NormalizeException;    
}
