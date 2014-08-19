/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CgenLexicalEnvironment.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.cgen;

import fr.upmc.ilp.ilp1.interfaces.*;

/** La représentation des environnements lexicaux de compilation vers
 * C. C'est l'analogue de runtime/LexicalEnvironement pour le
 * paquetage cgen. */

public class CgenLexicalEnvironment
  implements ICgenLexicalEnvironment {

  public CgenLexicalEnvironment (final IASTvariable variable,
                                 final String compiledName,
                                 final ICgenLexicalEnvironment next) {
    this.variableName = variable.getName();
    this.compiledName = compiledName;
    this.next = next;
  }
  private final String variableName;
  private final String compiledName;
  private final ICgenLexicalEnvironment next;

  @Override
public String compile (final IASTvariable variable)
    throws CgenerationException {
    if ( variableName.equals(variable.getName()) ) {
      return compiledName;
    } else {
      return next.compile(variable);
    }
  }

  @Override
public ICgenLexicalEnvironment extend (final IASTvariable variable,
                                       final String compiledName) {
    return new CgenLexicalEnvironment(variable, compiledName, this);
  }

  @Override
public ICgenLexicalEnvironment extend (final IASTvariable variable) {
    return new CgenLexicalEnvironment(variable, variable.getName(), this);
  }

  // Classe interne:
  public static class Empty
  extends CgenLexicalEnvironment {

      // La technique du singleton:
      private Empty () {
          super(new IASTvariable() {
              @Override
            public String getName() { return null; }
          }, null, null);
      }
      private static final Empty
        EMPTY_LEXICAL_ENVIRONMENT;
      static {
        EMPTY_LEXICAL_ENVIRONMENT = new Empty();
      }

      public static ICgenLexicalEnvironment create () {
        return Empty.EMPTY_LEXICAL_ENVIRONMENT;
      }

      @Override
      public String compile (final IASTvariable variable)
        throws CgenerationException {
        final String msg = "Variable inaccessible: " + variable.getName();
        throw new CgenerationException(msg);
      }
    }

}

// end of CgenLexicalEnvironment.java
