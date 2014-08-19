/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASTblocUnaire.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import fr.upmc.ilp.ilp1.interfaces.*;

/** Description d'un bloc local pourvu d'une seule variable locale, de
 * l'expression initialisant cette variable locale et d'un corps qui
 * est une séquence d'instructions.
 */

public class ASTunaryBlock extends AST
implements IASTunaryBlock {

  public ASTunaryBlock (ASTvariable variable,
                        AST         initialization,
                        ASTsequence body)
  {
    this.variable       = variable;
    this.initialization = initialization;
    this.body           = body;
  }

  private final ASTvariable variable;
  private final AST         initialization;
  private final ASTsequence body;

  public IASTvariable getVariable () {
    return this.variable;
  }
  public IAST getInitialization () {
    return this.initialization;
  }
  public IASTsequence getBody () {
    return this.body;
  }

  @Override
  public String toXML () {
    StringBuffer sb = new StringBuffer();
    sb.append("<blocUnaire>");
    sb.append(variable.toXML());
    sb.append("<valeur>");
    sb.append(initialization.toXML());
    sb.append("</valeur><corps>");
    sb.append(body.toXML());
    sb.append("</corps></blocUnaire>");
    return sb.toString();
  }

}

// end of ASTblocUnaire.java
