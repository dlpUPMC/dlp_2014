/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:LexicalEnvironment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.runtime;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;

/** Cette implantation d'environnement est tres naive: c'est une
 * simple liste chainee (mais comme nous n'avons pour l'instant que
 * des blocs unaires cela suffit!). En revanche, comme la classe 
 * correspondant a l'environnement vide depend de celui-ci, on la
 * definit comme classe emboitee.
 */

public class LexicalEnvironment
extends BasicEnvironment<IAST2variable>
implements ILexicalEnvironment {

  public LexicalEnvironment (final IAST2variable variable,
                             final Object value,
                             final ILexicalEnvironment next )
  {
      super(variable, next);
      this.value = value;
  }
  protected Object value;

  @Override
  public ILexicalEnvironment getNext () {
      return (ILexicalEnvironment) super.getNext();
  }
  @Override
  public ILexicalEnvironment shrink (final IAST2variable v) {
      return (ILexicalEnvironment) super.shrink(v);
  }

  @Override
  public boolean isPresent (final IAST2variable variable) {
      if ( getVariable().equals(variable) ) {
          return true;
      } else {
          return getNext().isPresent(variable);
      }
  }

  public Object lookup (IAST2variable variable)
  throws EvaluationException {
      if ( getVariable().equals(variable) ) {
          return this.value;
      } else {
          return getNext().lookup(variable);
      }
  }

  public void update (final IAST2variable variable,
                      final Object value)
    throws EvaluationException {
    if ( getVariable().equals(variable) ) {
        this.value = value;
    } else {
        getNext().update(variable, value);
    }
  }

  /** On peut etendre tout environnement. */
  public ILexicalEnvironment extend (final IAST2variable variable,
                                     final Object value) {
    return new LexicalEnvironment(variable, value, this);
  }
  
  /** ===========================================================
   * Comme il y a une dependance entre  EmptyLexicalEnvironment
   * et LexicalEnvironment, on lie leurs definitions en un unique fichier.
   * */

  public static class EmptyLexicalEnvironment
  extends BasicEmptyEnvironment<IAST2variable>
  implements ILexicalEnvironment {

    // La technique du singleton:
    protected EmptyLexicalEnvironment () {}
    private static final EmptyLexicalEnvironment THE_EMPTY_LEXICAL_ENVIRONMENT;
    static {
      THE_EMPTY_LEXICAL_ENVIRONMENT = new EmptyLexicalEnvironment();
    }

    public static EmptyLexicalEnvironment create () {
      return EmptyLexicalEnvironment.THE_EMPTY_LEXICAL_ENVIRONMENT;
    }
    
    /** Encore une fois covariant! */
    @Override
    public EmptyLexicalEnvironment getNext() {
        final String msg = "Empty environment!";
        throw new RuntimeException(msg);
    }
    @Override
    public @OrNull ILexicalEnvironment shrink(IAST2variable v) {
        return null;
    }
    
    public Object lookup (IAST2variable variable) {
        String msg = "Variable sans valeur: " + getVariable().getName();
        throw new RuntimeException(msg);
    }

    /** L'environnement vide ne contient rien et signale
     * systematiquement une erreur si l'on cherche la valeur d'une
     * variable. */

    public void update (final IAST2variable variable,
                        final Object value )
      throws EvaluationException {
      final String msg = "Variable inexistante: " + variable.getName();
      throw new EvaluationException(msg);
    }

    /** On peut etendre l'environnement vide.
     *
     * Malheureusement, cela cree une dependance avec la classe des
     * environnements non vides d'ou l'inclusion de cette classe dans
     * celle des environnements non vides.
     */
    public ILexicalEnvironment extend (final IAST2variable variable,
                                       final Object value) {
      return new LexicalEnvironment(variable, value, this);
    }
  }

}

// end of LexicalEnvironment.java
