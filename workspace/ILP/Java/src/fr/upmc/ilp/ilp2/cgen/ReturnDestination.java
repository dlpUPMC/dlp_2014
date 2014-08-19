/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ReturnDestination.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp2.cgen;

import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;

/** Destination indiquant le résultat d'une fonction. */

public class ReturnDestination 
implements IDestination {
    
    private static final ReturnDestination RETURN_DESTINATION = 
        new ReturnDestination();

    private ReturnDestination() {}

    // Singleton
    public static ReturnDestination create () {
        return RETURN_DESTINATION;
    }

    /** Préfixe le résultat avec "return" pour indiquer son intérêt. */
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common ) {
        buffer.append("return ");
    }
}

// end of ReturnDestination.java
