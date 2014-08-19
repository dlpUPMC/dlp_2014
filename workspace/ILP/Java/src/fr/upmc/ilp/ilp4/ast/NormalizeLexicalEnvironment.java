/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: NormalizeLexicalEnvironment.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

public class NormalizeLexicalEnvironment implements INormalizeLexicalEnvironment {
    private final IAST4variable variable;
    private final INormalizeLexicalEnvironment next;

    public NormalizeLexicalEnvironment(final IAST4variable variable,
        final INormalizeLexicalEnvironment next) {
        this.variable = variable;
        this.next = next;
    }

    public INormalizeLexicalEnvironment extend(final IAST4variable variable) {
        return new NormalizeLexicalEnvironment(variable, this);
    }

    public IAST4variable isPresent(final IAST4variable otherVariable) {
        if (variable.getName().equals(otherVariable.getName())) {
            return variable;
        } else {
            return next.isPresent(otherVariable);
        }
    }

    public static class EmptyNormalizeLexicalEnvironment
    implements INormalizeLexicalEnvironment {

        public EmptyNormalizeLexicalEnvironment() {}

        public INormalizeLexicalEnvironment extend(final IAST4variable variable) {
            return new NormalizeLexicalEnvironment(variable, this);
        }

        public IAST4variable isPresent(final IAST4variable otherVariable) {
            return null;
        }
    }

}

// end of NormalizeLexicalEnvironment.java
