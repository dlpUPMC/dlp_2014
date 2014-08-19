/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:IUserFunction.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;

/** L'interface des fonctions définies par l'utilisateur. */

public interface IUserFunction {

  /** Une fonction invoquée avec un nombre quelconque d'arguments. */

  Object invoke (Object[] arguments, ICommon common)
    throws EvaluationException;

  /** Obtenir les composantes d'une fonction utilisateur. */
  IAST2variable[] getVariables ();
  IAST2instruction<CEASTparseException> getBody ();
  ILexicalEnvironment getEnvironment ();
}

// end of IUserFunction.java
