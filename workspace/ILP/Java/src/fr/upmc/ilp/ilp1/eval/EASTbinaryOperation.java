/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:EASToperationBinaire.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import java.util.List;
import java.util.Vector;

import fr.upmc.ilp.ilp1.interfaces.IASTbinaryOperation;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

/** Les operations binaires. */

public class EASTbinaryOperation extends EASToperation
implements IASTbinaryOperation {

  public EASTbinaryOperation (String operateur,
                              EAST operandeGauche,
                              EAST operandeDroit)
  {
    super(operateur, 2);
    this.operandeGauche = operandeGauche;
    this.operandeDroit = operandeDroit;
  }

  protected EAST operandeGauche;
  protected EAST operandeDroit;

  public EAST getLeftOperand () {
    return this.operandeGauche;
  }
  public EAST getRightOperand () {
    return this.operandeDroit;
  }

  public EAST[] getOperands () {
    // On calcule paresseusement car ce n'est pas une methode usuelle:
    if ( operands == null ) {
        List<EAST> loperands = new Vector<>();
        loperands.add(operandeGauche);
        loperands.add(operandeDroit);
        operands = loperands.toArray(new EAST[0]);
    }
    return operands;
  }
  // NOTA: remarquer que ce mode paresseux interdit de qualifier ce
  // champ de « final » ce qui n'est pas sûr!
  private EAST[] operands;

  @Override
  public Object eval (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException {
    Object r1 = operandeGauche.eval(lexenv, common);
    Object r2 = operandeDroit.eval(lexenv, common);
    return common.applyOperator(this.getOperatorName(), r1, r2);
  }

}

// end of EASToperationBinaire.java
