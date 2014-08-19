/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASTalternative.java 1247 2012-09-19 14:24:59Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.interfaces.IAST;
import fr.upmc.ilp.ilp1.interfaces.IASTalternative;

/** La représentation des alternatives (binaires ou ternaires). */

public class ASTalternative extends AST
implements IASTalternative {

  public ASTalternative (AST condition,
                         AST consequence,
                         @OrNull AST alternant ) {
    this.condition   = condition;
    this.consequence = consequence;
    this.alternant   = alternant;
  }

  // NOTA: Masquer l'implantation de l'alternative binaire afin
  // d'éviter la propagation de null.

  public ASTalternative (AST condition, AST consequence) {
    this(condition, consequence, null);
  }

  private final AST condition;
  private final AST consequence;
  private final AST alternant;

  public IAST getCondition () {
    return this.condition;
  }

  public IAST getConsequent () {
    return this.consequence;
  }

  public @OrNull IAST getAlternant () {
    return this.alternant;
  }

  /** Vérifie que l'alternative est ternaire c'est-à-dire qu'elle a un
      véritable alternant. */

  public boolean isTernary () {
    return this.alternant != null;
  }

  @Override
  public String toXML () {
    StringBuffer sb = new StringBuffer();
    sb.append("<alternative><condition>");
    sb.append(condition.toXML());
    sb.append("</condition><consequence>");
    sb.append(consequence.toXML());
    sb.append("</consequence>");
    if ( isTernary() ) {
      sb.append("<alternant>");
      sb.append(alternant.toXML());
      sb.append("</alternant>");
    }
    sb.append("</alternative>");
    return sb.toString();
  }
}

// fin de ASTalternative.java
