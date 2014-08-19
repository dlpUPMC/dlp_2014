/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASTflottant.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import fr.upmc.ilp.ilp1.interfaces.*;

import java.math.BigDecimal;

/** Une constante flottante. */

public class ASTfloat extends AST
implements IASTfloat {

  public ASTfloat (String valeur) {
    this.valeur = Double.parseDouble(valeur);
    this.bigfloat = new BigDecimal(valeur);
  }
  private final double valeur;
  private final BigDecimal bigfloat;

  public BigDecimal getValue () {
    return bigfloat;
  }

  @Override
  public String toXML () {
    return "<flottant valeur='" + valeur + "'/>";
  }

}

// end of ASTflottant.java
