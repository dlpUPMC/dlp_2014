/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASTbooleen.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import fr.upmc.ilp.ilp1.interfaces.*;

/** Une constante booleenne. */

public class ASTboolean extends AST
implements IASTboolean {

  public ASTboolean (String valeur) {
    this.valeur = "true".equals(valeur);
  }
  private final boolean valeur;

  public boolean getValue () {
    return valeur;
  }

  @Override
  public String toXML () {
    return "<booleen valeur='" + valeur + "'/>";
  }

}

// end of ASTbooleen.java
