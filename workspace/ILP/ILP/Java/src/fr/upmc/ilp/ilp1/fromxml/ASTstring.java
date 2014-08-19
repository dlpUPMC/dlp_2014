/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASTchaine.java 800 2009-09-07 12:44:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import fr.upmc.ilp.ilp1.interfaces.*;

/** Une constante chaine de caracteres. */

public class ASTstring extends AST
implements IASTstring {

  public ASTstring (String valeur) {
    this.valeur = valeur;
  }
  private final String valeur;

  public String getValue () {
    return valeur;
  }

  @Override
  public String toXML () {
    return "<chaine>" + valeur + "</chaine>";
  }

}

// end of ASTchaine.java
