/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTtry.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp3.IAST3try;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4try;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

public class CEASTtry
  extends CEASTdelegableInstruction
  implements IAST4try {

  protected CEASTtry (final IAST4expression body,
                      final IAST4variable caughtExceptionVariable,
                      final IAST4expression catcher,
                      final IAST4expression finallyer) {
      this.delegate = new fr.upmc.ilp.ilp3.CEASTtry(
              body, caughtExceptionVariable, catcher, finallyer );
  }
  private fr.upmc.ilp.ilp3.CEASTtry delegate;

  @Override
  public fr.upmc.ilp.ilp3.CEASTtry getDelegate () {
      return this.delegate;
  }

  @ILPexpression
  public IAST4expression getBody () {
    return CEAST.narrowToIAST4expression(this.delegate.getBody());
  }
  @ILPvariable(neverNull=false)
  public @OrNull IAST4variable getCaughtExceptionVariable () {
      IAST2variable cev = this.delegate.getCaughtExceptionVariable();
      if ( null != cev ) {
          return CEAST.narrowToIAST4variable(cev);
      } else {
          return null;
      }
  }
  @ILPexpression(neverNull=false)
  public @OrNull IAST4expression getCatcher () {
      IAST2instruction<CEASTparseException> result = this.delegate.getCatcher();
      if ( null != result ) {
          return CEAST.narrowToIAST4expression(result);
      } else {
          return null;
      }
  }
  @ILPexpression(neverNull=false)
  public @OrNull IAST4expression getFinallyer () {
      IAST2instruction<CEASTparseException> result = this.delegate.getFinallyer();
      if ( null != result ) {
          return CEAST.narrowToIAST4expression(result);
      } else {
          return null;
      }
  }

  public static IAST3try<CEASTparseException> parse (
          final Element e, final IParser parser)
  throws CEASTparseException {
    IAST3try<CEASTparseException> delegate =
      fr.upmc.ilp.ilp3.CEASTtry.parse(e, parser);
    IAST4Factory factory = parser.getFactory();
    IAST4expression body_ =
        CEAST.narrowToIAST4expression(delegate.getBody());
    IAST4variable cev = null;
    IAST4expression catcher_ = null;
    if ( null != delegate.getCaughtExceptionVariable() ) {
        IAST2variable cev_ = delegate.getCaughtExceptionVariable();
        cev = (IAST4variable) factory.newVariable(cev_.getName());
        catcher_ = CEAST.narrowToIAST4expression(delegate.getCatcher());
    }
    IAST4expression finallyer_ = null;
    if ( null != delegate.getFinallyer() ) {
        finallyer_ = CEAST.narrowToIAST4expression(delegate.getFinallyer());
    }
    return factory.newTry(body_, cev, catcher_, finallyer_);
  }

  @Override
  public IAST4expression normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
    IAST4expression body_ =
        getBody().normalize(lexenv, common, factory);
    IAST4expression catcher_ = getCatcher();
    IAST4variable caughtVar_ = null;
    if ( catcher_ != null ) {
        caughtVar_ = factory.newLocalVariable(
            getCaughtExceptionVariable().getName());
        final INormalizeLexicalEnvironment catcherLexenv =
            lexenv.extend(caughtVar_);
        catcher_ = catcher_.normalize(catcherLexenv, common, factory);
    }
    IAST4expression finallyer_ = null;
    if ( null != getFinallyer() ) {
        finallyer_ = getFinallyer().normalize(lexenv, common, factory);
    }
    return factory.newTry(body_, caughtVar_, catcher_, finallyer_);
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
  @Override
  public void findInvokedFunctions () {
      findAndAdjoinToInvokedFunctions(getBody());
      if ( getCatcher() != null ) {
          findAndAdjoinToInvokedFunctions(getCatcher());
      };
      if ( getFinallyer() != null ) {
          findAndAdjoinToInvokedFunctions(getFinallyer());
      };
  }

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
      getBody().inline();
      if ( getCatcher() != null ) {
          getCatcher().inline();
      };
      if ( getFinallyer() != null ) {
          getFinallyer().inline();
      };
  }
  */

  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTtry.java
