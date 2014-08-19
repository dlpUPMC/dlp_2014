/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ICgenLexicalEnvironment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;

/** L'interface décrivant l'environnement lexical de compilation vers
 * C. Il est l'analogue de runtime/ILexicalEnvironment pour le
 * paquetage cgen. */

public interface ICgenLexicalEnvironment
extends IEnvironment<IAST2variable> {

  // Soyons covariant:
  ICgenLexicalEnvironment getNext ();
  ICgenLexicalEnvironment shrink (IAST2variable variable);

  /** Renvoie le code compilé d'accès à cette variable.
   *
   * @throws CgenerationException si la variable est absente.
   */

  String compile (IAST2variable variable)
    throws CgenerationException;

  /** Étend l'environnement avec une nouvelle variable et vers quel
   * nom la compiler. */
  ICgenLexicalEnvironment extend (IAST2variable variable);
  ICgenLexicalEnvironment extend (IAST2variable variable, String cname);
}

// end of ICgenLexicalEnvironment.java
