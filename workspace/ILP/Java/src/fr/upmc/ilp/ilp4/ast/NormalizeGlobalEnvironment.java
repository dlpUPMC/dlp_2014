/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: NormalizeGlobalEnvironment.java 1320 2013-11-25 22:45:00Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import java.util.HashMap;
import java.util.Map;

import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;

/** Une implantation d'environnement global pour la normalisation des
 * expressions. */

public class NormalizeGlobalEnvironment
  implements INormalizeGlobalEnvironment {

  public NormalizeGlobalEnvironment () {
    this.globals = new HashMap<>();
    this.primitives = new HashMap<>();
  }
  private final Map<String,IAST4globalVariable> globals;
  private final Map<String,IAST4globalVariable> primitives;

  public void add (final IAST4globalVariable variable) {
    globals.put(variable.getName(), variable);
  }

  public IAST4globalVariable isPresent (final IAST4variable otherVariable) {
    IAST4globalVariable gv = globals.get(otherVariable.getName());
    return gv;
  }

  public void addPrimitive (IAST4globalVariable variable) {
    primitives.put(variable.getName(), variable);
  }

  public IAST4globalVariable isPrimitive (final IAST4variable variable) {
    IAST4globalVariable pv = primitives.get(variable.getName());
    return pv;
  }

}

// end of NormalizeGlobalEnvironment.java
