/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EASTbooleen.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;
import fr.upmc.ilp.ilp1.interfaces.IASTboolean;

/** Une constante booleenne. */

public class EASTboolean extends EASTConstant implements IASTboolean {

  /** Nota: Encore un cas ennuyeux où l'appel à super() n'est pas
   * trivial. Il y a quelques années, le vérifieur de code-octet avait
   * tendance à refuser ce genre de programmation! */

  public EASTboolean (String valeur) {
    super(  ("true".equals(valeur))
           ? Boolean.TRUE : Boolean.FALSE );
    this.valeur = "true".equals(valeur);
  }
  private final boolean valeur;

  public boolean getValue () {
    return valeur;
  }
}

// end of EASTbooleen.java
