/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTconstant.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.Set;

import fr.upmc.ilp.ilp2.interfaces.IAST2constant;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;

/** Les constantes et leur interprétation. */

public abstract class CEASTconstant
  extends CEASTexpression
  implements IAST2constant<CEASTparseException> {

  public CEASTconstant (final String description, final Object value) {
      this.description = description;
      this.valueAsObject = value;
  }
  private final String description;
  private final Object valueAsObject;

  public Object getValue() {
      return this.valueAsObject;
  }
  public String getDescription () {
      return this.description;
  }

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  /** Toutes les constantes valent leur propre valeur. */

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common) {
    return getValue();
  }
  
  @Override
  public void
  findGlobalVariables (final Set<IAST2variable> globalvars,
                     final ICgenLexicalEnvironment lexenv ) {
      return;
  }
}

// end of CEASTConstant.java
