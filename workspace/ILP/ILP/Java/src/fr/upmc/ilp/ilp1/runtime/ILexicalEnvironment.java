/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ILexicalEnvironment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;
import fr.upmc.ilp.ilp1.interfaces.*;

/** Cette interface définit un environnement lexical pour une
 * évaluation. Un environnement est une structure de données présente
 * à l'exécution et contenant une suite de couples (on dit « liaison
 * ») variable - valeur de cette variable.
 */

public interface ILexicalEnvironment {

  /** Renvoie la valeur d'une variable si présente dans
   * l'environnement.
   *
   * @throws EvaluationException si la variable est absente.
   */
  Object lookup (IASTvariable variable)
    throws EvaluationException;

  /** Étend l'environnement avec un nouveau couple variable-valeur. */
  ILexicalEnvironment extend (IASTvariable variable, Object value);

}

// end of ILexicalEnvironment.java
