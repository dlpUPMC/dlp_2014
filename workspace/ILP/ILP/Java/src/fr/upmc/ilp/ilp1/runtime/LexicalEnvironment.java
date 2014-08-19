/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:LexicalEnvironment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;
import fr.upmc.ilp.ilp1.interfaces.*;

/** Cette implantation d'environnement est très naïve: c'est une
 * simple liste chaînée (mais comme nous n'avons pour l'instant que
 * des blocs unaires cela suffit!).
 */

public class LexicalEnvironment
implements ILexicalEnvironment {

  public LexicalEnvironment (final IASTvariable variable,
                             final Object value,
                             final ILexicalEnvironment next )
  {
    this.variableName = variable.getName();
    this.value = value;
    this.next = next;
  }
  protected final String variableName;
  protected volatile Object value;
  protected final ILexicalEnvironment next;

  /** Renvoie la valeur d'une variable si présente dans
   * l'environnement.
   */
  public Object lookup (final IASTvariable variable)
    throws EvaluationException {
    if ( variableName.equals(variable.getName()) ) {
      return value;
    } else {
      return next.lookup(variable);
    }
  }

  /** On peut étendre tout environnement. */
  public ILexicalEnvironment extend (final IASTvariable variable, 
                                     final Object value) {
    return new LexicalEnvironment(variable, value, this);
  }
}

// end of LexicalEnvironment.java
