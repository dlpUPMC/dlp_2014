/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: NormalizeException.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

public class NormalizeException extends Exception {
    static final long serialVersionUID = +1234567890010000L;

    public NormalizeException(String message) {
        super(message);
    }

    public NormalizeException(Throwable cause) {
        super(cause);
    }
}

// end of NormalizeException.java
