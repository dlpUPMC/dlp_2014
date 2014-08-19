/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EASTflottant.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;
import java.math.BigDecimal;

import fr.upmc.ilp.ilp1.interfaces.IASTfloat;

/** Les constantes flottantes. */

public class EASTfloat extends EASTConstant implements IASTfloat {

  /** Constructeur.
   *
   * Ici, le double est pris comme une chaîne puis manipulé comme un
   * Double (en guise d'objet Java) pour l'évaluation. En revanche
   * pour les IAST, il est manipulé comme un BigDecimal pour ne pas
   * subir de dégradation de précision. L'implantation perd donc de la
   * précision par rapport à la syntaxe!
   */

  public EASTfloat (String valeur) {
    super(new Double(valeur));
    this.bigfloat = new BigDecimal(valeur);
  }
  private final BigDecimal bigfloat;

  public BigDecimal getValue () {
    return bigfloat;
  }
}

// end of EASTflottant.java
