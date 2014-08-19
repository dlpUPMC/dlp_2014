/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTboolean.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2boolean;
import fr.upmc.ilp.ilp4.interfaces.IAST4boolean;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Les constantes booleennes.*/

public class CEASTboolean
extends CEASTconstant implements IAST4boolean {

    public CEASTboolean (String value) {
        super(new fr.upmc.ilp.ilp2.ast.CEASTboolean(value));
    }

    public static IAST2boolean<CEASTparseException> parse (
            final Element e, final IParser parser)
    throws CEASTparseException {
        return fr.upmc.ilp.ilp2.ast.CEASTboolean.parse(e, parser);
    }
}

// end of CEASTboolean.java
