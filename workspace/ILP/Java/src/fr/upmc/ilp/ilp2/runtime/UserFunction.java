/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:UserFunction.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.runtime;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IUserFunction;

/* La classe des fonctions anonymes définies par l'utilisateur. */

public class UserFunction
implements IUserFunction {

  public UserFunction (final IAST2variable[] variable,
                       final IAST2instruction<CEASTparseException> body,
                       final ILexicalEnvironment lexenv) {
    this.variable = variable;
    this.body = body;
    this.lexenv = lexenv;
  }
  protected final IAST2variable[] variable;
  protected final IAST2instruction<CEASTparseException> body;
  protected final ILexicalEnvironment lexenv;

  public IAST2variable[] getVariables () {
      return this.variable;
  }
  public IAST2instruction<CEASTparseException> getBody () {
      return this.body;
  }
  public ILexicalEnvironment getEnvironment () {
      return this.lexenv;
  }

  // Interdit d'acceder directement les champs a partir d'ici!

  public Object invoke (final Object[] arguments,
                        final ICommon common)
    throws EvaluationException {
      IAST2variable[] variables = getVariables();
      if ( variables.length != arguments.length ) {
          final String msg = "Wrong arity";
          throw new EvaluationException(msg);
      }
      return unsafeInvoke(arguments, common);
  }

  protected Object unsafeInvoke (final Object[] arguments,
                                 final ICommon common)
    throws EvaluationException {
      IAST2variable[] variables = getVariables();
      ILexicalEnvironment lexenv = getEnvironment();
      for ( int i = 0 ; i<variables.length ; i++ ) {
          lexenv = lexenv.extend(variables[i], arguments[i]);
      }
      return getBody().eval(lexenv, common);
  }
}

// end of UserFunction.java
