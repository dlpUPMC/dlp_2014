/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ICommon.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;

/** Cette interface definit les caracteristiques globales d'un interprete du
 * langage ILP2. On y trouve, notamment, la definition des operateurs du
 * langage. */

public interface ICommon
extends fr.upmc.ilp.ilp1.runtime.ICommon {

  /** Determiner la valeur d'une primitive. */
  Object primitiveLookup (String primitiveName)
    throws EvaluationException;

  /** Associer une valeur a une primitive. */
  void bindPrimitive (String primitiveName, Object value)
    throws EvaluationException;

  /** Determiner la valeur d'une variable globale. */
  Object globalLookup (IAST2variable variable)
    throws EvaluationException;

  /** Associer une valeur a une variable globale. */
  void updateGlobal (String variableName, Object value)
    throws EvaluationException;

  /** Determiner si une variable globale est presente. */
  boolean isPresent (String variableName);
}

// end of ICommon.java
