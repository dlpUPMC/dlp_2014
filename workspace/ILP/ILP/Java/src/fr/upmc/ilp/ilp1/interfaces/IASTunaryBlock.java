/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IASTunaryBlock.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

/** Décrit un bloc local ne liant qu'une unique variable. */

public interface IASTunaryBlock extends IAST {

    /** Renvoie la variable liée localement. */
    IASTvariable getVariable();

    /** Renvoie l'expression initialisant la variable locale. */
    IAST getInitialization();

    /** Renvoie la sequence d'instructions présentes dans le corps du
     * bloc local. */
    IASTsequence getBody();
}

// fin de IASTunaryBlock.java
