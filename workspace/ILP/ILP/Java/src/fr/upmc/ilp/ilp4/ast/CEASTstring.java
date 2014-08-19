/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTstring.java 918 2010-01-15 16:51:15Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2string;
import fr.upmc.ilp.ilp4.interfaces.IAST4string;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Les constantes chaines de caracteres. */
public class CEASTstring
extends CEASTconstant implements IAST4string {

    public CEASTstring (final String value) {
        super(new fr.upmc.ilp.ilp2.ast.CEASTstring(value));
    }

    public static IAST2string<CEASTparseException> parse (
            final Element e, final IParser parser)
    throws CEASTparseException {
        return fr.upmc.ilp.ilp2.ast.CEASTstring.parse(e, parser);
    }
}

// end of CEASTstring.java
