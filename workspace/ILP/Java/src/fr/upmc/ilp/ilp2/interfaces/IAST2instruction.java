/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id:IAST2.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;

public interface IAST2instruction<Exc extends Exception>
extends IAST2<Exc> {

    /** Compilation d'une instruction. Production de code C par ajout à
     * un tampon, dans un environnement lexical et un environnement
     * global. Le résultat est produit avec une certaine destination. */

    void compileInstruction (StringBuffer buffer,
                             ICgenLexicalEnvironment lexenv,
                             ICgenEnvironment common,
                             IDestination destination)
      throws CgenerationException;
}

// end of IAST2instruction.java
