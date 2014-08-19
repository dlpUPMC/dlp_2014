/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: CommonPlus.java 909 2009-12-03 15:50:28Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.runtime;

import fr.upmc.ilp.ilp2.interfaces.ICommon;

/** Environnement global d'interpretation d'ILP4. */

public class CommonPlus
extends fr.upmc.ilp.ilp2.runtime.CommonPlus
implements ICommon {

     public CommonPlus () {
         super();
     }

     @Override
     public boolean isPresent (final String variableName) {
          return (   globalMap.containsKey(variableName)
                  || primitiveMap.containsKey(variableName) );
    }

}
  
// end of CommonPlus.java
  
  
