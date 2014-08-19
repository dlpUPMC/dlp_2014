/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IASTboolean.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

/** Decrit la citation d'un booleen. */

public interface IASTboolean extends IASTconstant {
        /** Renvoie le booleen cite. */
        boolean getValue ();
}
