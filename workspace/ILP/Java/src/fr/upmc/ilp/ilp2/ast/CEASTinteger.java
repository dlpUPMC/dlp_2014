/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTinteger.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.math.BigInteger;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2integer;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** Les constantes entieres. */

public class CEASTinteger
extends CEASTconstant
implements IAST2integer<CEASTparseException> {

  public CEASTinteger (final String value) {
    super(value, new BigInteger(value));
  }

  /** Le constructeur analysant syntaxiquement un DOM. */

  public static IAST2integer<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      String value = e.getAttribute("valeur");
      return parser.getFactory().newIntegerConstant(value);
  }

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  public void compileExpression (final StringBuffer buffer,
                                 final ICgenLexicalEnvironment lexenv,
                                 final ICgenEnvironment common,
                                 final IDestination destination)
    throws CgenerationException {
    final BigInteger value = (BigInteger) getValue();
    // && et non || comme l'a remarqué Nicolas.Bros@gmail.com
    if (    value.compareTo(BIMIN) >= 0
         && value.compareTo(BIMAX) <= 0 ) {
      destination.compile(buffer, lexenv, common);
      buffer.append(" ILP_Integer2ILP(");
      buffer.append(value.intValue());
      buffer.append(") ");
    } else {
      final String msg = "Too large integer " + value;
      throw new CgenerationException(msg);
    }
  }

  // Définir les bornes encadrant les seuls entiers représentables:
  public static final BigInteger BIMIN;
  public static final BigInteger BIMAX;
  static {
    final Integer i = new Integer(Integer.MIN_VALUE);
    BIMIN = new BigInteger(i.toString());
    final Integer j = new Integer(Integer.MAX_VALUE);
    BIMAX = new BigInteger(j.toString());
  }
}

// end of CEASTinteger.java
