/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASTentier.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import fr.upmc.ilp.ilp1.interfaces.*;

import java.math.BigInteger;

/** Une constante entière. */

public class ASTinteger extends AST
implements IASTinteger {

  public ASTinteger (String valeur) {
    this.valeur = Integer.parseInt(valeur);
    this.bigint = new BigInteger(valeur);
  }
  private final int valeur;
  private final BigInteger bigint;

  public BigInteger getValue () {
    return bigint;
  }

  @Override
  public String toXML () {
    return "<entier valeur='" + valeur + "'/>";
  }

}

// end of ASTentier.java
