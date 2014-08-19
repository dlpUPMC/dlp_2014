/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: LexicalEnvironment.java 932 2010-08-21 13:12:32Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.runtime;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.runtime.BasicEmptyEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;

/** Cette implantation d'environnement est très naïve par contre elle
 * utilise des comparaisons d'adresses rapides plutot que des comparaisons
 * de chaine de caracteres comme dans ILP2.
 */

public class LexicalEnvironment 
extends fr.upmc.ilp.ilp2.runtime.LexicalEnvironment
implements ILexicalEnvironment {
    
    public LexicalEnvironment (final IAST2variable variable, 
                               final Object value,
                               final ILexicalEnvironment next) {
        super(variable, value, next);
    }
    
    /** Renvoie la valeur d'une variable si présente dans
     * l'environnement. */
    
    @Override
    public Object lookup (final IAST2variable otherVariable)
        throws EvaluationException {
        if ( this.variable == otherVariable ) {
            return this.value;
        } else {
            return getNext().lookup(otherVariable);
        }
    }

    /** verifie si une variable est presente dans l'environnement. */
    @Override
    public boolean isPresent (final IAST2variable otherVariable) {
        if (variable == otherVariable) {
            return true;
        } else {
            return getNext().isPresent(otherVariable);
        }
    }

    @Override
    public void update (final IAST2variable otherVariable, 
                        final Object value)
        throws EvaluationException {
        if (variable == otherVariable) {
            this.value = value;
        } else {
            getNext().update(otherVariable, value);
        }
    }

    /** On peut étendre tout environnement. */
    public ILexicalEnvironment extend (final IAST4variable variable,
                                       final Object value) {
        return new LexicalEnvironment(variable, value, this);
    }
    
    /** Comme il y a une dependance entre  EmptyLexicalEnvironment
     * et LexicalEnvironment, on lie leurs definitions en un unique fichier.
     * */

    public static class EmptyLexicalEnvironment
    extends BasicEmptyEnvironment<IAST2variable>
    implements ILexicalEnvironment {

      // La technique du singleton:
      private EmptyLexicalEnvironment () {}
      private static final EmptyLexicalEnvironment THE_EMPTY_LEXICAL_ENVIRONMENT;
      static {
        THE_EMPTY_LEXICAL_ENVIRONMENT = new EmptyLexicalEnvironment();
      }

      public static EmptyLexicalEnvironment create () {
        return EmptyLexicalEnvironment.THE_EMPTY_LEXICAL_ENVIRONMENT;
      }

      public Object lookup (IAST2variable variable) {
          String msg = "Variable sans valeur: " + getVariable().getName();
          throw new RuntimeException(msg);
      }
      @Override
      public EmptyLexicalEnvironment getNext() {
          final String msg = "Empty environment!";
          throw new RuntimeException(msg);
      }
      @Override
      public @OrNull ILexicalEnvironment shrink(IAST2variable v) {
          return null;
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
      @Override
      public ILexicalEnvironment extend (final IAST2variable variable,
                                         final Object value) {
        return new LexicalEnvironment(variable, value, this);
      }
    }
}

// end of LexicalEnvironment.java
