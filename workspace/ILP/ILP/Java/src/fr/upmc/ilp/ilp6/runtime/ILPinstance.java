/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ILPinstance.java 872 2009-10-26 10:31:31Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.runtime;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICommon;

/** Une instance pour ILP6. */

public class ILPinstance {

  public ILPinstance (final ILPClass clazz, final Object[] argument) {
    this.clazz = clazz;
    this.field = argument;
  }
  private final ILPClass clazz;
  private final Object[] field;

  /** Rendre la classe pour ILP6 de cette instance. */

  public ILPClass classOf () {
    return clazz;
  }

  /** Lire un champ de cette instance. */

  public Object read (final String fieldName)
    throws EvaluationException {
    return field[clazz.fieldRank(fieldName)];
  }

  /** Écrire un champ de cette instance. */

  public Object write (final String fieldName, final Object value)
    throws EvaluationException {
    field[clazz.fieldRank(fieldName)] = value;
    return Boolean.FALSE;
  }

  /** Invoquer une méthode sur cette instance. */

  public Object send (final String message,
                      final Object[] argument,
                      final ICommon common)
    throws EvaluationException {
    return clazz.send(this, message, argument, common);
  }

  /** Imprimer une instance (et tous ses champs). */
  @Override
  public String toString () {
    final StringBuffer sb = new StringBuffer();
    sb.append("<");
    sb.append(clazz.getName());
    for ( int i = 0 ; i<field.length ; i++ ) {
      sb.append(":");
      sb.append(clazz.fieldName(i));
      sb.append("=");
      sb.append(field[i]);
    }
    sb.append(">");
    return sb.toString();
  }

}

// end of ILPInstance.java
