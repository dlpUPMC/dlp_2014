/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CgenLexicalEnvironment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.cgen;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.runtime.BasicEmptyEnvironment;
import fr.upmc.ilp.ilp2.runtime.BasicEnvironment;

/** La représentation des environnements lexicaux de compilation vers
 * C. C'est l'analogue de runtime/LexicalEnvironment pour le
 * paquetage cgen. */

public class CgenLexicalEnvironment
extends BasicEnvironment<IAST2variable>
implements ICgenLexicalEnvironment {

  public CgenLexicalEnvironment (final IAST2variable variable,
                                 final ICgenLexicalEnvironment next) {
      super(variable, next);
      this.cname = variable.getMangledName();
  }
  protected String cname;
  
  public CgenLexicalEnvironment (final IAST2variable variable,
                                 final String cname,
                                 final ICgenLexicalEnvironment next) {
      super(variable, next);
      this.cname = cname;
  }

  @Override
  public ICgenLexicalEnvironment getNext () {
      return (ICgenLexicalEnvironment) super.getNext();
  }

  @Override
  public @OrNull ICgenLexicalEnvironment shrink (IAST2variable variable) {
      if ( getVariable().equals(variable) ) {
          return this;
      } else {
          return getNext().shrink(variable);
      }
  }
  public String compile (final IAST2variable variable)
    throws CgenerationException {
    if ( getVariable().equals(variable) ) {
      return this.cname;
    } else {
      return getNext().compile(variable);
    }
  }

  public ICgenLexicalEnvironment extend (final IAST2variable variable) {
    return new CgenLexicalEnvironment(variable, this);
  }
  public ICgenLexicalEnvironment extend(final IAST2variable variable, 
                                        final String cname) {
      return new CgenLexicalEnvironment(variable, cname, this);
  }
  
  /** ===================================================== */
  public static class CgenEmptyLexicalEnvironment
  extends BasicEmptyEnvironment<IAST2variable>
  implements ICgenLexicalEnvironment {

    // La technique du singleton:
    protected CgenEmptyLexicalEnvironment () {}
    private static final CgenEmptyLexicalEnvironment
      THE_EMPTY_LEXICAL_ENVIRONMENT;
    static {
      THE_EMPTY_LEXICAL_ENVIRONMENT = new CgenEmptyLexicalEnvironment();
    }

    public static ICgenLexicalEnvironment create () {
      return CgenEmptyLexicalEnvironment.THE_EMPTY_LEXICAL_ENVIRONMENT;
    }

    public String compile (final IAST2variable variable)
      throws CgenerationException {
      final String msg = "Variable inaccessible: "
        + variable.getName();
      throw new CgenerationException(msg);
    }

    public ICgenLexicalEnvironment extend (final IAST2variable variable) {
      return new CgenLexicalEnvironment(variable, this);
    }
    public ICgenLexicalEnvironment extend(final IAST2variable variable, 
                                          final String cname) {
        return new CgenLexicalEnvironment(variable, cname, this);
    }

    /** Aucune variable n'est présente dans l'environnement vide. */

    @Override
    public boolean isPresent (IAST2variable variable) {
      return false;
    }

    @Override
  public @OrNull CgenEmptyLexicalEnvironment shrink(IAST2variable variable) {
        return null;
    }
    @Override
    public CgenEmptyLexicalEnvironment getNext () {
        final String msg = "Really empty environment!";
        throw new RuntimeException(msg);
    }
    
    @Override
    public IAST2variable getVariable () {
        final String msg = "Empty environment!";
        throw new RuntimeException(msg);
    }
    @Override
    public boolean isEmpty () {
        return true;
    }
  }

}

// end of CgenLexicalEnvironment.java
