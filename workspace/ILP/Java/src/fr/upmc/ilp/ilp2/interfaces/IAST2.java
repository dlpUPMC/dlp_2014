/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id:IAST2.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.interfaces;

import java.util.Set;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;

/** L'interface des AST à la fois interprétables et compilables. */

public interface IAST2<Exc extends Exception> {

  /** Interprétation dans un certain environnement lexical et global. */

  Object eval (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException;

  /** Calculer l'ensemble des variables libres. */

  void findGlobalVariables (Set<IAST2variable> globalvars,
                            ICgenLexicalEnvironment lexenv );
}

// end of IAST2.java
