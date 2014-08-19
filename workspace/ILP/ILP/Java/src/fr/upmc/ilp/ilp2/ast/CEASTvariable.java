/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTvariable.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;

/** Les variables: interprétation et compilation. */

public class CEASTvariable
implements IAST2variable {

  public CEASTvariable (final String name) {
      this.name = name;
  }
  protected final String name;

  public String getName () {
      return this.name;
  }
  public String getMangledName () {
      return this.name;
  }

  @Override
  public boolean equals (Object other) {
      if ( other instanceof IAST2variable ) {
          IAST2variable otherV = (IAST2variable) other;
          return otherV.getName().equals(getName());
      } else {
          return false;
      }
  }

  @Override
  public int hashCode () {
      return getName().hashCode();
  }

  /** Utile pour les conversions de Liste de variable vers tableau
   * de variables */

  public final static IAST2variable[] EMPTY_VARIABLE_ARRAY =
      new IAST2variable[]{};

  /** Génération de variables temporaires. */

  public synchronized static IAST2variable generateVariable () {
    counter++;
    return new CEASTvariable("ilpTMP_" + counter);
  }
  private static int counter = 100;

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  public void compileDeclaration (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common ) {
      buffer.append(" ILP_Object ");
      buffer.append(getMangledName());
      buffer.append(";\n");
  }
}

// end of CEASTvariable.java
