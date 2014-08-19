/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CgenerationException.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.cgen;

/** Les exceptions préférentiellement signalées par la conversion en C. */

public class CgenerationException extends Exception {

    static final long serialVersionUID = +1234567890004000L;

  public CgenerationException (Throwable cause) {
    super(cause);
  }

  public CgenerationException (String message) {
    super(message);
  }

}

// end of CgenerationException.java
