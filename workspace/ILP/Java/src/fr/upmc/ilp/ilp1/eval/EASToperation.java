/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EASToperation.java 1213 2012-08-27 15:09:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import fr.upmc.ilp.ilp1.interfaces.IASToperation;

/** La classe abstraite des operations.
 *
 * NOTA: Notez que la méthode getOperands() manque alors qu'elle est
 * présente dans l'interface (IASToperation) que cette classe
 * implante! Savez-vous pourquoi ?
 */

public abstract class EASToperation extends EAST
implements IASToperation
{

  protected EASToperation (final String operateur, final int arity) {
    this.operateur = operateur;
    this.arity = arity;
  }
  private final String operateur;
  private final int arity;

  // et pourquoi les final ici ?
  public final String getOperatorName () {
    return this.operateur;
  }

  public final int getArity () {
    return this.arity;
  }

}

// end of EASToperation.java
