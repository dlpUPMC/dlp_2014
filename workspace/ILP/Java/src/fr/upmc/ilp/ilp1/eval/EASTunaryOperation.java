/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:EASToperationUnaire.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import java.util.List;
import java.util.Vector;

import fr.upmc.ilp.ilp1.interfaces.IASTunaryOperation;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

/** Les operations unaires. */

public class EASTunaryOperation extends EASToperation
implements IASTunaryOperation {

  public EASTunaryOperation (String operateur, EAST operand) {
    super(operateur, 1);
    this.operand = operand;
  }
  protected EAST operand;

  public EAST getOperand () {
    return this.operand;
  }

  public EAST[] getOperands () {
    // On calcule paresseusement car ce n'est pas une methode usuelle:
    if ( operands == null ) {
        List<EAST> loperands = new Vector<>();
        loperands.add(operand);
        operands = loperands.toArray(new EAST[0]);
    }
    return operands;
  }
  private EAST[] operands;

  @Override
  public Object eval (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException {
    return common.applyOperator(this.getOperatorName(),
                                operand.eval(lexenv, common));
  }

}

// end of EASToperationUnaire.java
