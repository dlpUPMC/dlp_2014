/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTinteger.java 918 2010-01-15 16:51:15Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2integer;
import fr.upmc.ilp.ilp4.interfaces.IAST4integer;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Les constantes entieres. */

public class CEASTinteger
extends CEASTconstant implements IAST4integer  {

    public CEASTinteger (final String value) {
        super(new fr.upmc.ilp.ilp2.ast.CEASTinteger(value));
    }

    public static IAST2integer<CEASTparseException> parse (
            final Element e, final IParser parser)
    throws CEASTparseException {
        return fr.upmc.ilp.ilp2.ast.CEASTinteger.parse(e, parser);
    }
}

// end of CEASTinteger.java
