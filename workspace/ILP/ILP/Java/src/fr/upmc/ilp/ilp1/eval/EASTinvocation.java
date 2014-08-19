/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:EASTinvocation.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import java.util.List;

import fr.upmc.ilp.ilp1.interfaces.IASTinvocation;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;
import fr.upmc.ilp.ilp1.runtime.Invokable;

/** La classe abstraite des invocations en general. */

public abstract class EASTinvocation extends EAST
implements IASTinvocation {

  protected EASTinvocation (final EAST fonction, final List<EAST> arguments) {
    this.fonction = fonction;
    this.argument = arguments.toArray(new EAST[0]);
  }
  protected EAST     fonction;
  protected EAST[]   argument;

  public EAST getFunction () {
    return this.fonction;
  }

  public EAST[] getArguments () {
    return this.argument;
  }

  public int getArgumentsLength () {
    return this.argument.length;
  }

  public EAST getArgument (final int i) {
    return this.argument[i];
  }

  @Override
  public Object eval (final ILexicalEnvironment lexenv, final ICommon common)
    throws EvaluationException {
    final Object fn = this.fonction.eval(lexenv, common);
    if ( fn instanceof Invokable ) {
      Invokable invokable = (Invokable) fn;
      final Object[] args = new Object[argument.length];
      for ( int i = 0 ; i<argument.length ; i++ ) {
        args[i] = argument[i].eval(lexenv, common);
      }
      return invokable.invoke(args);
    } else {
      final String msg = "Not a function: " + fn;
      throw new EvaluationException(msg);
    }
  }

}

// end of EASTinvocation.java
