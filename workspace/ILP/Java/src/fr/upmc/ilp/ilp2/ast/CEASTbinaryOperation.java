/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTbinaryOperation.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2binaryOperation;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** Opérateurs binaires. */

public class CEASTbinaryOperation extends CEASToperation
  implements IAST2binaryOperation<CEASTparseException> {

  public CEASTbinaryOperation (
          final String operatorName,
          final IAST2expression<CEASTparseException> left,
          final IAST2expression<CEASTparseException> right)
  {
    super(operatorName, 2);
    this.left = left;
    this.right = right;
  }
  private final IAST2expression<CEASTparseException> left;
  private final IAST2expression<CEASTparseException> right;

  public IAST2expression<CEASTparseException> getLeftOperand() {
      return this.left;
  }
  public IAST2expression<CEASTparseException> getRightOperand() {
      return this.right;
  }
  public IAST2expression<CEASTparseException>[] getOperands () {
      final CEASTexpression[] result = {
              (CEASTexpression) this.left,
              (CEASTexpression) this.right,
      };
      return result;
  }

  public static IAST2binaryOperation<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      String operatorName = e.getAttribute("operateur");
      final NodeList nl = e.getChildNodes();
      IAST2expression<CEASTparseException> leftOperand =
          (IAST2expression<CEASTparseException>)
          parser.findThenParseChildAsUnique(nl, "operandeGauche");
      IAST2expression<CEASTparseException> rightOperand =
          (IAST2expression<CEASTparseException>)
          parser.findThenParseChildAsUnique(nl, "operandeDroit");
      return parser.getFactory().newBinaryOperation(
              operatorName, leftOperand, rightOperand);
  }

  //NOTE: Acces direct aux champs interdit a partir d'ici!

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common )
    throws EvaluationException {
    Object r1 = getLeftOperand().eval(lexenv, common);
    Object r2 = getRightOperand().eval(lexenv, common);
    return common.applyOperator(getOperatorName(), r1, r2);
  }

  public void compileExpression (final StringBuffer buffer,
                                 final ICgenLexicalEnvironment lexenv,
                                 final ICgenEnvironment common,
                                 final IDestination destination)
    throws CgenerationException {
    destination.compile(buffer, lexenv, common);
    buffer.append(" ");
    buffer.append(common.compileOperator2(getOperatorName()));
    buffer.append("(");
    getLeftOperand().compileExpression(buffer, lexenv, common);
    buffer.append(", ");
    getRightOperand().compileExpression(buffer, lexenv, common);
    buffer.append(") ");
  }

  @Override
  public void
    findGlobalVariables (final Set<IAST2variable> globalvars,
                       final ICgenLexicalEnvironment lexenv ) {
    getLeftOperand().findGlobalVariables(globalvars, lexenv);
    getRightOperand().findGlobalVariables(globalvars, lexenv);
  }

}

// end of CEASTbinaryOperation.java
