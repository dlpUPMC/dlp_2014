/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: CgenLexicalEnvironment6.java 1190 2011-12-19 15:58:38Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.cgen;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;

/** Les environnements lexicaux pour la compilation. Ils sont semblables
 * a ceux d'ilp2 sauf qu'ils permettent en plus de savoir dans quelle
 * methode l'on est lorsque l'on compile une methode. C'est utile pour
 * compiler l'appel a la super-methode. */

public class CgenLexicalEnvironment6
extends fr.upmc.ilp.ilp2.cgen.CgenLexicalEnvironment {

  public CgenLexicalEnvironment6 (final IAST2variable variable,
                                  final ICgenLexicalEnvironment next) {
    super(variable, next);
  }
  public CgenLexicalEnvironment6 (final IAST2variable variable,
                                  final String cname,
                                  final ICgenLexicalEnvironment next) {
      super(variable, cname, next);
  }

  // On utilise maintenant l'egalité physique.

  @Override
  public String compile (final IAST2variable variable)
    throws CgenerationException {
    if ( this.variable == variable ) {
      return this.variable.getMangledName();
    } else {
      return getNext().compile(variable);
    }
  }

  @Override
  public boolean isPresent (IAST2variable variable) {
    if ( this.variable == variable ) {
      return true;
    } else {
      return next.isPresent(variable);
    }
  }

  @Override
  public ICgenLexicalEnvironment extend (final IAST2variable variable) {
    return new CgenLexicalEnvironment6(variable, this);
  }
  @Override
  public ICgenLexicalEnvironment extend (final IAST2variable variable,
                                         final String cname) {
      return new CgenLexicalEnvironment6(variable, cname, this);
    }

  /** ==============================================
   */
  public static class CgenEmptyLexicalEnvironment6
  extends fr.upmc.ilp.ilp2.cgen.CgenLexicalEnvironment.CgenEmptyLexicalEnvironment {

    public CgenEmptyLexicalEnvironment6 () {}
    private static final CgenEmptyLexicalEnvironment6
        THE_EMPTY_LEXICAL_ENVIRONMENT;
    static {
        THE_EMPTY_LEXICAL_ENVIRONMENT = new CgenEmptyLexicalEnvironment6();
    }

    public static ICgenLexicalEnvironment create () {
        return CgenEmptyLexicalEnvironment6.THE_EMPTY_LEXICAL_ENVIRONMENT;
    }

    @Override
    public ICgenLexicalEnvironment extend (final IAST2variable variable) {
        return new CgenLexicalEnvironment6(variable, this);
    }
    @Override
    public ICgenLexicalEnvironment extend (final IAST2variable variable,
                                           final String cname) {
        return new CgenLexicalEnvironment6(variable, cname, this);
    }
  }

}

// end of CgenLexicalEnvironment6.java
