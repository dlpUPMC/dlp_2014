/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IASTbinaryOperation.java 768 2008-11-02 15:56:00Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

/** Interface décrivant les opérations binaires.
 *
 * Cette interface hérite de celle des opérations qui permet l'accès
 * à l'opérateur et à la liste des opérandes.
 */

public interface IASTbinaryOperation extends IASToperation {
        
  /** renvoie l'opérande de gauche. */
  IAST getLeftOperand ();
        
  /** renvoie l'opérande de droite. */
  IAST getRightOperand ();
}

// fin de IASTbinaryOperation.java
