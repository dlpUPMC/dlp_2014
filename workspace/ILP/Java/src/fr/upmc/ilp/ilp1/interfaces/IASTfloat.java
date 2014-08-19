/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IASTfloat.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

import java.math.BigDecimal;

/** Citation d'un flottant. */

public interface IASTfloat extends IASTconstant {
        /** Renvoie le flottant cité comme constante. */
        BigDecimal getValue ();
}
