/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASToperation.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;

/** La classe abstraite des opérations.

 * Il y en a deux sortes: les unaires et les binaires.
 */

public abstract class ASToperation extends AST
{
  protected ASToperation (String operateur, int arity) {
    this.operateur = operateur;
    this.arity = arity;
  }
  private final String operateur;
  private final int arity;

  public String getOperatorName () {
    return this.operateur;
  }

  public int getArity () {
    return this.arity;
  }

}

// end of ASToperation.java
