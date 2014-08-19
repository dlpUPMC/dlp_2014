/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTParser.java 1299 2013-08-27 07:09:39Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp2.interfaces.IAST2program;
import fr.upmc.ilp.ilp2.interfaces.IAST2Factory;

/** Transformer un document XML en un CEAST. */

public class CEASTParser extends AbstractParser {

    public CEASTParser (IAST2Factory<CEASTparseException> factory) {
        super(factory);
    }

    public IAST2program<CEASTparseException> parse (final Document d)
    throws CEASTparseException {
        final Element e = d.getDocumentElement();
        return CEASTprogram.parse(e, this);
    }

  public IAST2<CEASTparseException> parse (final Node n)
    throws CEASTparseException {
    switch ( n.getNodeType() ) {

    case Node.ELEMENT_NODE: {
      final Element e = (Element) n;
      final String name = e.getTagName();
      //System.err.println("Parsing " + name + " ..."); // DEBUG
      switch (name) {
      case "programme1": 
      case "programme2": 
          return CEASTprogram.parse(e, this);
      case "alternative": 
        return CEASTalternative.parse(e, this);
      case "sequence":
        return CEASTsequence.parse(e, this);
      case "boucle":
        return CEASTwhile.parse(e, this);
      case "affectation":
        return CEASTassignment.parse(e, this);
      case "definitionFonction":
        return CEASTfunctionDefinition.parse(e, this);
      case "blocUnaire":
        return CEASTunaryBlock.parse(e, this);
      case "blocLocal":
        return CEASTlocalBlock.parse(e, this);
      case "variable":
        return CEASTreference.parse(e, this);
      case "invocationPrimitive":
        return CEASTprimitiveInvocation.parse(e, this);
      case "invocation":
        return CEASTinvocation.parse(e, this);
      case "operationUnaire":
        return CEASTunaryOperation.parse(e, this);
      case "operationBinaire":
        return CEASTbinaryOperation.parse(e, this);
      case "entier":
        return CEASTinteger.parse(e, this);
      case "flottant":
        return CEASTfloat.parse(e, this);
      case "chaine":
        return CEASTstring.parse(e, this);
      case "booleen":
        return CEASTboolean.parse(e, this);
      default: {
        final String msg = "Unknown element name: " + name;
        throw new CEASTparseException(msg);
      }
      }
    }

    default: {
      final String msg = "Unknown node type: " + n.getNodeName();
      throw new CEASTparseException(msg);
    }
    }
  }
}

// end of CEASTParser.java
