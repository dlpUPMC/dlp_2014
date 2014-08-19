/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTboolean.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2boolean;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** Les constantes booleennes.*/

public class CEASTboolean
extends CEASTconstant
implements IAST2boolean<CEASTparseException> {

  public CEASTboolean (String value) {
    super(value, "true".equals(value));
  }

  public static IAST2boolean<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      String value = e.getAttribute("valeur");
      return parser.getFactory().newBooleanConstant(value);
  }

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  public void compileExpression (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common,
                                  final IDestination destination)
    throws CgenerationException {
    destination.compile(buffer, lexenv, common);
    if ( Boolean.FALSE != getValue() ) {
          buffer.append(" ILP_TRUE ");
    } else {
      buffer.append(" ILP_FALSE ");
    }
  }
}

// end of CEASTboolean.java
