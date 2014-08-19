/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: ASTException.java 768 2008-11-02 15:56:00Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;

/** Une exception comme les autres mais qu'utilisent préférentiellement
 * les classes du paquetage fromxml.
 */

public class ASTException extends Exception {

        static final long serialVersionUID = +1234567890001000L;

        public ASTException (Throwable cause) {
                super(cause);
        }

        public ASTException (String message) {
                super(message);
        }

}

// fin de ASTException.java
