/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: InliningException.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

public class InliningException extends Exception {

    static final long serialVersionUID = +1234567890010000L;

    public InliningException(String message) {
        super(message);
    }

    public InliningException(Throwable cause) {
        super(cause);
    }
}

// end of InliningException.java
