/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ICgenLexicalEnvironment.java 768 2008-11-02 15:56:00Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.cgen;

import fr.upmc.ilp.ilp1.interfaces.*;

/** L'interface décrivant l'environnement lexical de compilation vers
 * C. Il est l'analogue de runtime/ILexicalEnvironment pour le
 * paquetage cgen. */

public interface ICgenLexicalEnvironment  {

  /** Renvoie le code compilé d'accès à cette variable.
   *
   * @throws CgenerationException si la variable est absente.
   */

  String compile (IASTvariable variable)
    throws CgenerationException;

  /** Étend l'environnement avec une nouvelle variable et vers quel
   * nom la compiler. */

  ICgenLexicalEnvironment extend (IASTvariable variable,
                                  String compiledName );

  /** Étend l'environnement avec une nouvelle variable qui sera
   * compilée par son propre nom. */

  ICgenLexicalEnvironment extend (IASTvariable variable);

}

// end of ICgenLexicalEnvironment.java
