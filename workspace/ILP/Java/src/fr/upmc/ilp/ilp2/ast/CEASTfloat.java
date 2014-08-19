/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTfloat.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2float;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** Les constantes flottantes. */

public class CEASTfloat
extends CEASTconstant
implements IAST2float<CEASTparseException> {

  public CEASTfloat (final String value) {
    super(value, new Double(value));
  }

  public static IAST2float<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      String value = e.getAttribute("valeur");
      return parser.getFactory().newFloatConstant(value);
  }

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  public void compileExpression (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common,
                                  final IDestination destination)
    throws CgenerationException {
    final Double value = (Double) getValue();
    final double d = value.doubleValue();
    destination.compile(buffer, lexenv, common);
    buffer.append(" ILP_Float2ILP(");
    buffer.append(d);
    buffer.append(") ");
  }
}

// end of CEASTfloat.java
