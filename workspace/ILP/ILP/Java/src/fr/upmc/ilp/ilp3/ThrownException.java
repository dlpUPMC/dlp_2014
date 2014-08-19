/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ThrownException.java 942 2010-08-24 17:09:58Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp3;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;

/** La classe des valeurs signalées comme exception. */

public class ThrownException
extends EvaluationException {

  static final long serialVersionUID = +200711241641L;

  public ThrownException (final Object value) {
    super("Thrown value");
    this.value = value;
  }
  private final Object value;

  public Object getThrownValue () {
    return value;
  }

  @Override
  public String toString () {
    return "Thrown value: " + value;
  }
}

// end of ThrownException.java
