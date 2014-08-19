/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: VoidDestination.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp2.cgen;

import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;

/** Destination indiquant qu'un résultat est inutile. */

public class VoidDestination 
implements IDestination {
    
    private static final VoidDestination VOID_DESTINATION = 
        new VoidDestination();

    private VoidDestination() {}

    // Singleton
    public static VoidDestination create() {
        return VOID_DESTINATION;
    }

    /** Préfixe le résultat avec (void) pour indiquer son inintérêt. */
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common) {
        buffer.append("(void) ");
    }
}

// end of VoidDestination.java
