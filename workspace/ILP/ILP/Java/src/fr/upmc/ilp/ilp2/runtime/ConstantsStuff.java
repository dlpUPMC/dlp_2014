/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ConstantsStuff.java 929 2010-08-20 18:00:59Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.runtime;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICommon;

/** Cette classe permet d'etendre un environnement lexical avec une definition
 * de la constante Pi.
 */

public class ConstantsStuff {
    
    public ConstantsStuff () {}

    /** Etendre un environnement lexical pour y installer la constante Pi. */

    public void extendWithPredefinedConstants (final ICommon common)
        throws EvaluationException {
      common.updateGlobal(
              "pi",
              new Double(3.141592653589793238462643) );
    }
}
