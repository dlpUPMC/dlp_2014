/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EASTchaine.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;
import fr.upmc.ilp.ilp1.interfaces.IASTstring;

/** Les constantes de type chaine de caracteres. */

public class EASTstring extends EASTConstant implements IASTstring {

  public EASTstring (String valeur) {
    super(valeur);
    this.valeur = valeur;
  }
  private final String valeur;

  public String getValue () {
    return valeur;
  }
}

// end of EASTchaine.java
