/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IAST4globalVariable.java 869 2009-10-23 16:48:43Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp3.jsgen;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;

public interface IAST3globalVariable 
extends IAST3variable {
    
    /** Engendrer une déclaration globale en C pour cette variable. */
    void compileGlobalDeclaration(
            StringBuffer buffer,
            ICgenLexicalEnvironment lexenv, 
            ICgenEnvironment common )
        throws CgenerationException;
    
}

// end of IAST3globalVariable
