/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASToperation.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import fr.upmc.ilp.ilp2.interfaces.IAST2operation;

/** Les operations (unaires ou binaires). */

public abstract class CEASToperation
  extends CEASTexpression
  implements IAST2operation<CEASTparseException> {

  public CEASToperation (final String operatorName, final int arity) {
    this.operatorName = operatorName;
    this.arity = arity;
  }
  private final String operatorName;
  private final int arity;

  public String getOperatorName () {
      return this.operatorName;
  }
  public int getArity () {
      return this.arity;
  }
}

// end of CEASToperation.java
