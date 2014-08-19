/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: EASTException.java 1213 2012-08-27 15:09:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

/**
 * Une exception comme les autres mais qu'utilisent préférentiellement les
 * classes du paquetage eval.
 */

public class EASTException extends Exception {

    static final long serialVersionUID = +1234567890002000L;

    public EASTException(Throwable cause) {
        super(cause);
    }

    public EASTException(String message) {
        super(message);
    }

}

// end of EASTException.java
