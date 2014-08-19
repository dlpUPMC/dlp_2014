/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTunaryOperation.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.cgen.NoDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryOperation;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** Les operations unaires. */

public class CEASTunaryOperation
  extends CEASToperation
  implements IAST2unaryOperation<CEASTparseException> {

  public CEASTunaryOperation (
          final String operatorName,
          final IAST2expression<CEASTparseException> operand) {
    super(operatorName, 1);
    this.operand = operand;
  }
  private final IAST2expression<CEASTparseException> operand;

  public IAST2expression<CEASTparseException> getOperand() {
    return operand;
  }
  public IAST2expression<CEASTparseException>[] getOperands () {
      CEASTexpression[] result = {
              (CEASTexpression) this.operand,
      };
      return result;
  }

  public static IAST2unaryOperation<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
    throws CEASTparseException {
      String operatorName = e.getAttribute("operateur");
      final NodeList nl = e.getChildNodes();
      IAST2expression<CEASTparseException> operand =
          (IAST2expression<CEASTparseException>)
          parser.findThenParseChildAsUnique(nl, "operande");
      return parser.getFactory().newUnaryOperation(operatorName, operand);
  }

  //NOTE: Acces direct aux champs interdit a partir d'ici!

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
    return common.applyOperator(getOperatorName(),
                                getOperand().eval(lexenv, common));
  }

  public void compileExpression (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common,
                                  final IDestination destination)
    throws CgenerationException {
    destination.compile(buffer, lexenv, common);
    buffer.append(" ");
    buffer.append(common.compileOperator1(getOperatorName()));
    buffer.append("(");
    getOperand().compileExpression(
            buffer, lexenv, common, NoDestination.create() );
    buffer.append(") ");
  }

  @Override
  public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                 final ICgenLexicalEnvironment lexenv ) {
      getOperand().findGlobalVariables(globalvars, lexenv);
  }

}

// end of CEASTunaryOperation.java
