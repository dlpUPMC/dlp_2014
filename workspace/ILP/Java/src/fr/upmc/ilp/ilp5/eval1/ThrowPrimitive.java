/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ThrowPrimitive.java 482 2006-10-01 13:56:39Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp5.eval1;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.runtime.InvokableImpl;

/** Cette classe implante la fonction throw() qui permet de signaler
 * une exception (n'importe quelle valeur d'ILP3). */

public class ThrowPrimitive
  extends InvokableImpl {

  private ThrowPrimitive () {}
  private static final ThrowPrimitive THE_INSTANCE;
  static {
    THE_INSTANCE = new ThrowPrimitive();
  }
  public static ThrowPrimitive create () {
    return THE_INSTANCE;
  }

  /** La valeur � signaler est envelopp�e dans une exception et
   * signal�e � Java. */

  @Override
  public Object invoke (final Object exception)
    throws EvaluationException {
    if ( exception instanceof VisitorEvaluationException ) {
      final VisitorEvaluationException exc = (VisitorEvaluationException) exception;
      throw exc;
    } else if ( exception instanceof RuntimeException ) {
      final RuntimeException exc = (RuntimeException) exception;
      throw exc;
    } else {
      throw new ThrownException(exception);
    }
  }

}

// end of ThrowPrimitive.java
