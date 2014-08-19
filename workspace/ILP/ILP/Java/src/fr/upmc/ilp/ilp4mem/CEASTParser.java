/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTParser.java 997 2010-10-13 16:26:26Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4mem;

import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;

/** Transformer un document XML en un CEAST. */

public class CEASTParser extends fr.upmc.ilp.ilp4.ast.CEASTParser {

    public CEASTParser (IAST4Factory factory) {
        super(factory);
        this.addParser("programme1", CEASTprogram.class);
        this.addParser("programme2", CEASTprogram.class);
        this.addParser("programme3", CEASTprogram.class);
        this.addParser("programme4", CEASTprogram.class);
        this.addParser("entier", CEASTinteger.class);
    }

    @Override
    public IAST4Factory getFactory () {
        return (IAST4Factory) super.getFactory();
    }
}

// end of CEASTParser.java
