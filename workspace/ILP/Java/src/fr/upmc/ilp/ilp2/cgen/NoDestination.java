/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: NoDestination.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp2.cgen;

import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;

/** Destination indiquant qu'un résultat est inutile. */

public class NoDestination 
implements IDestination {

    private static final NoDestination NO_DESTINATION = 
        new NoDestination();

    private NoDestination() {}

    // Singleton
    public static NoDestination create () {
        return NO_DESTINATION;
    }

    /** Ne préfixe rien devant le résultat. Cette destination est
     * utilisée pour compiler la définition des fonctions globales. */
    
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common ) {
        // rien du tout!
    }
}

// end of NoDestination.java
