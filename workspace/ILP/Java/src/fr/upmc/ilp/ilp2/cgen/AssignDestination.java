/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: AssignDestination.java 1190 2011-12-19 15:58:38Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp2.cgen;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.IDestination;

/** Destination indiquant dans quelle variable affecter un r�sultat. */

public class AssignDestination 
implements IDestination {

    private final IAST2variable variable;

    public AssignDestination (final IAST2variable variable) {
        this.variable = variable;
    }

    /** Préfixe le résultat avec "variable = " pour indiquer une
     * affectation. 
     * @throws CgenerationException */
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common ) 
        throws CgenerationException {
        if ( lexenv.isPresent(variable) ) {
            buffer.append(lexenv.compile(variable));
        } else {
            buffer.append(common.compileGlobal(variable));
        }
        buffer.append(" = ");
    }
}

// end of AssignDestination.java
