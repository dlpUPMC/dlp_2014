/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:IAST4globalFunctionVariable.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.interfaces;

public interface IAST4globalFunctionVariable 
extends IAST4globalVariable {

    /** Recuperer la definition de la fonction ainsi nommee. */
    IAST4functionDefinition getFunctionDefinition ();
    
    /** Associer une definition de fonction. */
    void setFunctionDefinition (IAST4functionDefinition functionDefinition);
        
}

//end of IAST4globalFunctionVariable.java
