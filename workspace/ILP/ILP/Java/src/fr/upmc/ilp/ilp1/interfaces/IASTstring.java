/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IASTstring.java 801 2009-09-07 15:30:20Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

/** Citation d'une chaine de caracteres.*/

public interface IASTstring extends IASTconstant {
        /** Renvoie la chaine de caracteres citee. */
        String getValue ();
}
