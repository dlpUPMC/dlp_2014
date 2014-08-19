/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2008 <Christian.Queinnec@lip6.fr>
 * $Id: EASTalternative.java 1213 2012-08-27 15:09:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.interfaces.IASTalternative;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

public class EASTalternative extends EAST
implements IASTalternative {

  public EASTalternative(EAST condition, EAST consequence, EAST alternant) {
    this.condition = condition;
    this.consequence = consequence;
    this.alternant = alternant;
  }

  // NOTA: Masquer l'implantation de l'alternative binaire afin
  // d'éviter la propagation de null.

  public EASTalternative(EAST condition, EAST consequence) {
     this(condition, consequence, null);
  }

  protected EAST condition;
  protected EAST consequence;
  protected EAST alternant;

  public EAST getCondition() {
    return this.condition;
  }

  public EAST getConsequent() {
    return this.consequence;
  }

  /** Quand il n'y a pas d'alternant, on rend n'importe quoi. Ce
   * n'importe-quoi est engendré par un utilitaire partagé au niveau d'AST. */

  @OrNull public EAST getAlternant() {
    return this.alternant;
  }

  public boolean isTernary() {
    return alternant != null;
  }

  @Override
    public Object eval(ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException {
    Object bool = condition.eval(lexenv, common);
    if (Boolean.FALSE == bool) {
      if (isTernary()) {
        return alternant.eval(lexenv, common);
      } else {
        // Et pas EAST.voidConstant() comme signalé par
        // <bourgerie.quentin@gmail.com>
        return EAST.voidConstantValue();
      }
    } else {
      return consequence.eval(lexenv, common);
    }
  }

}

// end of EASTalternative.java
