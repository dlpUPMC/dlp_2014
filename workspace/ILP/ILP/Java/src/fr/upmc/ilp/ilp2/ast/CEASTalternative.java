/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTalternative.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2alternative;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** L'alternative: son interprétation et sa compilation. */

public class CEASTalternative
  extends CEASTinstruction
  implements IAST2alternative<CEASTparseException> {

  public CEASTalternative (
          final IAST2expression<CEASTparseException> condition,
          final IAST2instruction<CEASTparseException> consequence,
          final IAST2instruction<CEASTparseException> alternant ) {
    this.condition   = condition;
    this.consequence = consequence;
    this.alternant   = alternant;
    this.ternary     = true;
  }
  public CEASTalternative (
          final IAST2expression<CEASTparseException> condition,
          final IAST2instruction<CEASTparseException> consequence ) {
      this.condition   = condition;
      this.consequence = consequence;
      this.alternant   = CEASTinstruction.voidInstruction();
      this.ternary     = false;
  }
  private final IAST2expression<CEASTparseException> condition;
  private final IAST2instruction<CEASTparseException> consequence;
  private final IAST2instruction<CEASTparseException> alternant;
  private boolean ternary;

  public IAST2expression<CEASTparseException> getCondition () {
      return this.condition;
  }
  public IAST2instruction<CEASTparseException> getConsequent (){
      return this.consequence;
  }
  // Plus de @OrNull: impossible si emploi du bon constructeur:
  public IAST2instruction<CEASTparseException> getAlternant () {
      return this.alternant;
  }
  public boolean isTernary () {
      return this.ternary;
  }

  public static IAST2alternative<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      final NodeList nl = e.getChildNodes();
      IAST2expression<CEASTparseException> condition =
          (IAST2expression<CEASTparseException>)
          parser.findThenParseChildAsUnique(nl, "condition");
      IAST2instruction<CEASTparseException> consequence =
          (IAST2instruction<CEASTparseException>)
          parser.findThenParseChildAsSequence(nl, "consequence");
      try {
          IAST2instruction<CEASTparseException> alternant =
              (IAST2instruction<CEASTparseException>)
              parser.findThenParseChildAsSequence(nl, "alternant");
          return parser.getFactory().newAlternative(
                  condition, consequence, alternant);
      } catch (CEASTparseException exc) {
          return parser.getFactory().newAlternative(condition, consequence);
      }
  }

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
    Object bool = getCondition().eval(lexenv, common);
    if ( bool instanceof Boolean ) {
      Boolean b = (Boolean) bool;
      if ( b.booleanValue() ) {
        return getConsequent().eval(lexenv, common);
      } else {
          return getAlternant().eval(lexenv, common);
      }
    } else {
      return getConsequent().eval(lexenv, common);
    }
  }

  public void compileInstruction (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common,
                                  final IDestination destination)
    throws CgenerationException {
    buffer.append(" if ( ILP_isEquivalentToTrue( ");
    getCondition().compileExpression(buffer, lexenv, common);
    buffer.append(" ) ) {\n");
    getConsequent().compileInstruction(buffer, lexenv, common, destination);
    buffer.append(";\n} else {\n");
    getAlternant().compileInstruction(buffer, lexenv, common, destination);
    buffer.append(";\n}");
  }

  @Override
  public void
    findGlobalVariables (final Set<IAST2variable> globalvars,
                       final ICgenLexicalEnvironment lexenv  ) {
    getCondition().findGlobalVariables(globalvars, lexenv);
    getConsequent().findGlobalVariables(globalvars, lexenv);
    getAlternant().findGlobalVariables(globalvars, lexenv);
  }

}

// end of CEASTalternative.java
