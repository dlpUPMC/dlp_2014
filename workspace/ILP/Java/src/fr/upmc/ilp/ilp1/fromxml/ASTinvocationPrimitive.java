/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ASTinvocationPrimitive.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;

/** Le cas particulier, parmi les invocations de fonctions,
 * des primitives.
 */

public class ASTinvocationPrimitive
  extends ASTinvocation {

  public ASTinvocationPrimitive (String fonction, AST[] arguments) {
    super(new ASTvariable(fonction), arguments);
  }

}

// fin de ASTinvocationPrimitive.java
