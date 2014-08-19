/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTparseException.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

/** Les exceptions signalées lors de l'analyse syntaxique. */

public class CEASTparseException
  extends Exception {
    
  static final long serialVersionUID = +1234567890006000L;

  public CEASTparseException (final Throwable cause) {
    super(cause);
  }

  public CEASTparseException (final String message) {
    super(message);
  }

}

// end of CEASTParseException.java
