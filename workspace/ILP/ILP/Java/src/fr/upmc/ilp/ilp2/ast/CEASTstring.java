/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTstring.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2string;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.IParser;
import fr.upmc.ilp.tool.CStuff;

/** Les constantes chaines de caracteres. */

public class CEASTstring
extends CEASTconstant
implements IAST2string<CEASTparseException> {

  public CEASTstring (String value) {
      super(value, value);
  }

  public static IAST2string<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      String value = e.getTextContent();
      return parser.getFactory().newStringConstant(value);
  }

  //NOTE: Acc�s direct aux champs interdit � partir d'ici!

  public void compileExpression (final StringBuffer buffer,
                                 final ICgenLexicalEnvironment lexenv,
                                 final ICgenEnvironment common,
                                 final IDestination destination)
    throws CgenerationException {
    String value = (String) getValue();
    destination.compile(buffer, lexenv, common);
    buffer.append(" ILP_String2ILP(\"");
    buffer.append(CStuff.protect(value));
    buffer.append("\") ");
  }
}

// end of CEASTstring.java
