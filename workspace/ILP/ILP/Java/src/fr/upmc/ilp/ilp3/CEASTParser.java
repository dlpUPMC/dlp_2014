/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTParser.java 997 2010-10-13 16:26:26Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp3;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp2.interfaces.IAST2program;

/** Transformer un document XML en un CEAST. */

public class CEASTParser extends fr.upmc.ilp.ilp2.ast.CEASTParser
implements IParser<CEASTparseException> {

    public CEASTParser (IAST3Factory<CEASTparseException> factory) {
        super(factory);
    }

    @Override
    public IAST3Factory<CEASTparseException> getFactory () {
        return (IAST3Factory<CEASTparseException>) super.getFactory();
    }

    @Override
    public IAST2program<CEASTparseException> parse (final Document d)
    throws CEASTparseException {
        final Element e = d.getDocumentElement();
        return CEASTprogram.parse(e, this);
    }

    @Override
    public IAST2<CEASTparseException> parse (final Node n)
    throws CEASTparseException {
    switch ( n.getNodeType() ) {

    case Node.ELEMENT_NODE: {
      final Element e = (Element) n;
      final String name = e.getTagName();

      if ( "try".equals(name) ) {
          return CEASTtry.parse(e, this);
      } else {
          return super.parse(n);
      }
    }

    default: {
        return super.parse(n);
    }
    }
  }
}

// end of CEASTParser.java
