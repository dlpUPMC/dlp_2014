/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IASTinteger.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

import java.math.BigInteger;

/** Citation d'un entier. */

public interface IASTinteger extends IASTconstant {
        /** Renvoie l'entier cite comme constante. */
        BigInteger getValue ();
}
