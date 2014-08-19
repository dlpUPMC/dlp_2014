/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:IASToperation.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;


/** Interface décrivant les opérations.
 *
 * Une opération implique un opérateur et un ou plusieurs opérandes.
 * Les opérations peuvent être unaires ou binaires cf. les
 * sous-interfaces appropriées.
 */

public interface IASToperation extends IAST {

  /** Renvoie le nom de l'opérateur concerné par l'opération. */
  String getOperatorName ();

  /** Renvoie l'arité de l'opérateur concerné par l'opération. L'arité
   * est toujours de 1 pour une IASTunaryOperation et de 2 pour une
   * IASTbinaryOperation. */
  int getArity ();

  /** Renvoie les opérandes d'une opération.
   *
   * NOTA: cette méthode est générale, les méthodes d'accès aux
   * opérandes des opérations unaires ou binaires sont, probablement,
   * plus efficaces.
   *
   * NOTA2: normalement le nombre d'operandes doit être égal à l'arité
   * de l'opérateur (mais il se peut que certains opérateurs soit n-aires)
   * auquel cas, que l'arité soit un simple entier n'est pas une bonne idée!
   */
  IAST[] getOperands ();
}

// fin de IASToperation.java
