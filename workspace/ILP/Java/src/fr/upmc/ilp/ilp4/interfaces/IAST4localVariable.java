/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IAST4localVariable.java 869 2009-10-23 16:48:43Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;

public interface IAST4localVariable 
extends IAST4variable {

    /** Engendrer une déclaration locale en C pour cette variable. */
    void compileDeclaration(
            StringBuffer buffer,
            ICgenLexicalEnvironment lexenv, 
            ICgenEnvironment common )
        throws CgenerationException;
}

//end of IAST4localeVariable
