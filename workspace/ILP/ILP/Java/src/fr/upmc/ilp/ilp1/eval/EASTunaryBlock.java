/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EASTblocUnaire.java 838 2009-10-12 13:18:34Z malenfant $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import fr.upmc.ilp.ilp1.interfaces.IASTunaryBlock;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

/** Les blocs locaux à une seule variable. */

public class EASTunaryBlock extends EAST implements IASTunaryBlock {

  public EASTunaryBlock (EASTvariable variable,
                         EAST initialization,
                         EASTsequence body)
  {
    this.variable       = variable;
    this.initialization = initialization;
    this.body           = body;
  }

  protected EASTvariable variable;
  protected EAST         initialization;
  protected EASTsequence body;

  public EASTvariable getVariable () {
    return this.variable;
  }
  public EAST getInitialization () {
    return this.initialization;
  }
  public EASTsequence getBody () {
    return this.body;
  }

  @Override
  public Object eval (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException {
    ILexicalEnvironment newlexenv =
      lexenv.extend(variable, initialization.eval(lexenv, common));
    return body.eval(newlexenv, common);
  }

}

// end of EASTblocUnaire.java
