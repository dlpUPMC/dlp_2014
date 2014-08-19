/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:EmptyLexicalEnvironment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;
import fr.upmc.ilp.ilp1.interfaces.*;

/** Une définition de l'environnement vide. */

public class EmptyLexicalEnvironment
  implements ILexicalEnvironment {

  // La technique du singleton:
  protected EmptyLexicalEnvironment () {}
  private static final EmptyLexicalEnvironment THE_EMPTY_LEXICAL_ENVIRONMENT;
  static {
    THE_EMPTY_LEXICAL_ENVIRONMENT = new EmptyLexicalEnvironment();
  }
  
  /** Creer un environnement lexical vide. 
   * L'environnement vide ne contient rien et signale
   * systématiquement une erreur si l'on cherche la valeur d'une
   * variable.*/

  public static EmptyLexicalEnvironment create () {
    return EmptyLexicalEnvironment.THE_EMPTY_LEXICAL_ENVIRONMENT;
  }

  /** Chercher la valeur d'une variable dans un environnement lexical.
   * 
   * @param variable la variable dont la valeur est cherchee
   * @throws EvaluationException si la variable n'a pas de valeur
   */

  public Object lookup (IASTvariable variable)
      throws EvaluationException {
      String msg = "Variable sans valeur: "
        + variable.getName();
      throw new EvaluationException(msg);
    }

  /** On peut étendre l'environnement vide.
   *
   * Malheureusement, cela crée une dépendance avec la classe des
   * environnements non vides et çà c'est moche!                     MOCHE.
   */
  public ILexicalEnvironment extend (IASTvariable variable, Object value) {
    return new LexicalEnvironment(variable, value, this);
  }

}

// end of EmptyLexicalEnvironment.java
