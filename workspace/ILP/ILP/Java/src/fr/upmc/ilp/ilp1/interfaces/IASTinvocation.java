/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:IASTinvocation.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

import fr.upmc.ilp.annotation.OrNull;


/** Décrit une invocation de fonction. La fonction peut être calculée
 * ou pas, c'est néanmoins un AST. Si le nom n'est pas calculée, c'est
 * une référence à un nom de variable (car, en JavaScript, les
 * fonctions sont dans le même espace de nom).
 */

public interface IASTinvocation extends IAST {

  /** Renvoie la fonction invoquée. */
  IAST getFunction ();

  /** Renvoie les arguments de l'invocation sous forme d'une liste. */
  IAST[] getArguments ();

  /** Renvoie le nombre d'arguments de l'invocation. */
  int getArgumentsLength ();

  /** Renvoie le i-ème argument de l'invocation ou null. */
  @OrNull IAST getArgument (int i);
}

// fin de IASTinvocation.java
