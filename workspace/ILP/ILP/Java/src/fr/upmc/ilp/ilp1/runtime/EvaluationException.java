/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: EvaluationException.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;

public class EvaluationException extends Exception {
        
        static final long serialVersionUID = +1234567890003000L;
        
        public EvaluationException (String message) {
                super(message);
        }
        
        public EvaluationException (Throwable cause) {
                super(cause);
        }
        
}

//end of EvaluationException.java
