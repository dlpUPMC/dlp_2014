/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EASTentier.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;
import java.math.BigInteger;

import fr.upmc.ilp.ilp1.interfaces.IASTinteger;

/** Les constantes entières littérales. Elles sont représentées par
 * des BigInteger ce qui permet de ne pas rendre syntaxiquement faux
 * un programme qui mentionnerait des entiers plus grand que ce que
 * sait faire la machine. En outre, cela permet de donner une
 * arithmétique complète à cette implantation.
 */

public class EASTinteger extends EASTConstant implements IASTinteger {

  /** Constructeur.
   *
   * NOTA: Pour initialiser le champ valueAsObject, on fait le bon
   * appel à super() mais comme un BigInteger est un gros objet que
   * l'on ne tient pas à dupliquer, on l'utilise tel qu'il apparaît
   * dans EASTConstant. Conclusion: valueAsObject ne peut plus être
   * privée à EASTConstant et voilà comment les « protected »
   * croissent dans les codes!
   */

  public EASTinteger (String valeur) {
    super(new BigInteger(valeur));
  }

  public BigInteger getValue () {
    return (BigInteger) this.valueAsObject;
  }
}

// end of EASTentier.java
