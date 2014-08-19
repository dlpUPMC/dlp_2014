package fr.upmc.ilp.ilp3.jsgen;

/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IAST4localVariable.java 869 2009-10-23 16:48:43Z queinnec $
 * GPL version>=2
 * ******************************************************************/

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;

public interface IAST3localVariable 
extends IAST2variable {

    /** Engendrer une déclaration locale en C pour cette variable. */
    void compileDeclaration(
            StringBuffer buffer,
            ICgenLexicalEnvironment lexenv, 
            ICgenEnvironment common )
        throws CgenerationException;
}

//end of IAST2localeVariable
