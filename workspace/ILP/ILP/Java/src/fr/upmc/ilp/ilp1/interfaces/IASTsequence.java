/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:IASTsequence.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

import fr.upmc.ilp.annotation.OrNull;

/** Décrit une séquence d'instructions. On reprend le même
 * style d'interface que IASTinvocation. */

public interface IASTsequence extends IAST {

    /** Renvoie la séquence des instructions contenues. */
    IAST[] getInstructions ();

    /** Renvoie le nombre d'instructions de la sequence. */
    int getInstructionsLength ();

    /** Renvoie la i-ème instruction ou null. */
    @OrNull IAST getInstruction (int i);

    // FUTURE ? ajouter getAllButLastInstructions() ???
}

// fin de IASTsequence.java
