/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: NormalizeGlobalEnvironment.java 1243 2012-09-16 08:01:36Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.ast;

import java.util.HashMap;
import java.util.Map;

import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;

/** Une implantation d'environnement global pour la normalisation des
 * expressions. */

public class NormalizeGlobalEnvironment
extends fr.upmc.ilp.ilp4.ast.NormalizeGlobalEnvironment 
implements INormalizeGlobalEnvironment {

  public NormalizeGlobalEnvironment () {
      super();
      this.classMap = new HashMap<>();
  }
  private final Map<String,IAST6classDefinition> classMap;

  // On enregistre les classes afin de verifier l'arite des instantiations.

  public IAST6classDefinition findClassDefinition (final String className) {
    IAST6classDefinition result = this.classMap.get(className);
    if ( result != null ) {
      return result;
    } else {
      final String msg = "No such class " + className;
      throw new RuntimeException(msg);
    }
  }

  public void addClassDefinition (final IAST6classDefinition clazz) {
    this.classMap.put(clazz.getName(), clazz);
  }

}

// end of NormalizeGlobalEnvironment.java
