/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EASTConstant.java 1213 2012-08-27 15:09:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;
import fr.upmc.ilp.ilp1.interfaces.IASTconstant;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

/** La classe abstraite des constantes. Elles partagent un même
 * comportement à savoir rendre leur propre valeur (un objet Java).
 *
 * Attention, même si le code de la méthode eval serait le même dans
 * les sous-classes de EASTConstant, le type de valeur est varié et ne
 * peut être , sans précaution, partagé! Noter le abstract de la
 * classe, le protected du constructeur. Noter le protected final sur
 * valueAsObject (explication en EASTentier).
 */

public abstract class EASTConstant extends EAST implements IASTconstant {

  protected EASTConstant (Object value) {
    this.valueAsObject = value;
  }
  protected final Object valueAsObject;

  /** Toutes les constantes valent leur propre valeur. */

  @Override
  public Object eval (ILexicalEnvironment lexenv, ICommon common) {
    return valueAsObject;
  }
}

// end of EASTConstant.java
