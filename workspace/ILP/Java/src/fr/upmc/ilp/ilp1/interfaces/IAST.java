/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IAST.java 1213 2012-08-27 15:09:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

/** Interface des arbres de syntaxe abstraite.
 *
 * Les arbres de syntaxe abstraite (AST) ne sont utilisés que via
 * cette interface ou, plus exactement, via une de ses
 * sous-interfaces. Cette interface ressemble un peu à celle du DOM
 * (Document Object Model) sauf qu'elle est beaucoup plus légère: elle
 * est plus typée mais elle ne permet que de descendre dans les AST.
 *
 * La raison d'être de cette interface est qu'elle sert de point de
 * rencontre entre les analyseurs syntaxiques qui doivent produire des
 * IAST qui seront ainsi manipulables par les premières passes de
 * l'interprète ou du compilateur.
 */

public interface IAST {

  /** Décrit l'AST sous forme d'une chaîne imprimable.
   *
   * En fait, c'était pour mettre quelque chose (ce qui n'est pas
   * obligatoire) ! Cette méthode vient avec toute implantation
   * puisque l'objet implantant hérite d'Object qui définit
   * toString()! Par contre, la mentionner ici rend sa définition
   * explicite obligatoire dans les classes qui implantent IAST.
   */
  String toString ();

  // FUTURE ajoute-t-on toXML() pour sérialiser les IAST ?
}

// fin de IAST.java
