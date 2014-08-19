/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: Invokable.java 832 2009-10-09 08:24:31Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;

/** L'interface des fonctions. La plupart des fonctions ont moins de 4
 * arguments aussi des méthodes d'arité diverses sont-elles procurées
 * pour invoquer une fonction avec 0, 1, 2 ou 3 arguments.
*/

public interface Invokable {

  /** Une fonction invoquée avec un nombre quelconque d'arguments. */

  Object invoke (Object[] arguments)
    throws EvaluationException;

  /** Invocation d'une fonction zéro-aire (ou niladique) */

  Object invoke ()
    throws EvaluationException;

  /** Invocation d'une fonction unaire (ou monadique) */

  Object invoke (Object argument1)
    throws EvaluationException;

  /** Invocation d'une fonction binaire (ou dyadique) */

  Object invoke (Object argument1, Object argument2)
    throws EvaluationException;

  /** Invocation d'une fonction ternaire */

  Object invoke (Object argument1, Object argument2, Object argument3)
    throws EvaluationException;

}

// end of Invokable.java
