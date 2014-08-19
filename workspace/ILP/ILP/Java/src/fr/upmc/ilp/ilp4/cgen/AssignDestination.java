/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: AssignDestination.java 869 2009-10-23 16:48:43Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.cgen;

import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;

/** Destination indiquant dans quelle variable affecter un résultat. 
 * Ici, on utilise le nom adapté au langage visé. */

public class AssignDestination implements IDestination {

    public AssignDestination (final IAST4variable variable) {
        this.variable = variable;
    }
    private final IAST4variable variable;

    /** Préfixe le résultat avec "variable = " pour indiquer une
     * affectation. */
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common ) {
        buffer.append(variable.getMangledName());
        buffer.append(" = ");
    }

}
