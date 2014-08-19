/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:UserGlobalFunction.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.runtime;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICommon;

/* La classe des fonctions globales definies par l'utilisateur. */

public class UserGlobalFunction
extends UserFunction {

  public UserGlobalFunction (final String name,
                             final IAST2variable[] variable,
                             final IAST2instruction<CEASTparseException> body) {
      super(variable, body, LexicalEnvironment.EmptyLexicalEnvironment.create());
      this.name = name;
  }
  protected final String name;

  @Override
  public Object invoke (final Object[] arguments,
                        final ICommon common)
    throws EvaluationException {
      IAST2variable[] variables = getVariables();
      if ( variables.length != arguments.length ) {
          final String msg = "Wrong arity for function:" + name;
          throw new EvaluationException(msg);
      }
      return unsafeInvoke(arguments, common);
  }
}

// end of UserGlobalFunction.java
