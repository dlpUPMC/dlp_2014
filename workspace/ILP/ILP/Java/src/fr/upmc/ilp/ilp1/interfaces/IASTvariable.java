/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IASTvariable.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

/**
 * Cette interface décrit une variable mais seulement une variable. D'autres
 * interfaces existent pour lire ou écrire des variables. Usuellement, les
 * variables n'apparaissent que dans des lieurs (fonctions, methodes ou blocs
 * locaux).
 */

public interface IASTvariable extends IAST {
    /** Renvoie le nom de la variable. */
    String getName();
}
