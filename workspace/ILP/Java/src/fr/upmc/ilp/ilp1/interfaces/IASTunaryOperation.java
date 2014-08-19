/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IASTunaryOperation.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

/** Interface décrivant les opérations unaires. */

public interface IASTunaryOperation extends IASToperation {
        /** renvoie l'unique operande. */
        IAST getOperand ();
}
