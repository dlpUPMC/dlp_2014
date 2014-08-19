/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EASTvariable.java 1213 2012-08-27 15:09:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import fr.upmc.ilp.ilp1.interfaces.*;
import fr.upmc.ilp.ilp1.runtime.*;

/** Une variable. */

public class EASTvariable extends EAST implements IASTvariable {

  public EASTvariable (String name) {
    this.name = name;
  }
  private final String name;

  public String getName () {
    return name;
  }

  @Override
  public Object eval (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException {
    return lexenv.lookup(this);
  }
}

// end of EASTvariable.java
