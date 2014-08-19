/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ASTinvocation.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import fr.upmc.ilp.ilp1.interfaces.IASTinvocation;

/** Une invocation mentionne une fonction et des arguments. */

public class ASTinvocation extends AST
implements IASTinvocation {

  public ASTinvocation (AST fonction, AST[] arguments) {
    this.fonction = fonction;
    this.argument = arguments;
  }
  private final AST   fonction;
  private final AST[] argument;

  public AST getFunction () {
    return this.fonction;
  }

  public AST[] getArguments () {
    return this.argument;
  }

  public int getArgumentsLength () {
    return this.argument.length;
  }

  public AST getArgument (int i) {
    return this.argument[i];
  }

  @Override
  public String toXML () {
    StringBuffer sb = new StringBuffer();
    sb.append("<invocation><function>");
    sb.append(fonction.toXML());
    sb.append("</function>");
    //sb.append("<arguments>");
    for ( AST arg : this.argument ) {
        sb.append(arg.toXML());
    }
    // sb.append("</arguments>");
    sb.append("</invocation>");
    return sb.toString();
  }

}

// end of ASTinvocation.java
