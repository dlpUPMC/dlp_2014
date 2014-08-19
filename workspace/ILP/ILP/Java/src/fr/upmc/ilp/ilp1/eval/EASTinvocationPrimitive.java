/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EASTinvocationPrimitive.java 1213 2012-08-27 15:09:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import java.util.List;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

/** L'invocation des primitives. */

public class EASTinvocationPrimitive
extends EASTinvocation {

  public EASTinvocationPrimitive (final String fonction,
                                  final List<EAST> arguments) {
    super(new EASTvariable(fonction), arguments);
  }

  @Override
  public Object eval (final ILexicalEnvironment lexenv, final ICommon common)
    throws EvaluationException {
    return super.eval(lexenv, common);
  }

}

// end of EASTinvocationPrimitive.java
