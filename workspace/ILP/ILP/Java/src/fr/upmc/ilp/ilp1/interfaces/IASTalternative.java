/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:IASTalternative.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

import fr.upmc.ilp.annotation.OrNull;

/** Décrit une alternative (si-alors-sinon).
 *
 * Une alternative comporte une condition (booléenne), une conséquence
 * (une expression quelconque) ainsi, éventuellement, qu'un alternant
 * (une expression aussi). La méthode isTernary() permet de distinguer
 * entre ces deux cas.
 */

public interface IASTalternative extends IAST {

  /** Renvoie la condition. */
  IAST getCondition ();

  /** Renvoie la conséquence. */
  IAST getConsequent ();

  /** Renvoie l'alternant si présent ou null.  */
  @OrNull IAST getAlternant ();

  /** Indique si l'alternative est ternaire (qu'elle a un alternant). */
  boolean isTernary ();
}

// fin de IASTalternative.java
