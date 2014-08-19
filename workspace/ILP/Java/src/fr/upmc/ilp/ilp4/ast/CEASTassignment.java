/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTassignment.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2assignment;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4assignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Affectation à des variables. Cette classe sert a representer une
 * affectation. La phase de normalisation la classifiera en affectation
 * locale ou globale.
 */

public class CEASTassignment
  extends CEASTdelegableExpression
  implements IAST4assignment {

  public CEASTassignment (final IAST4variable variable,
                          final IAST4expression value) {
      this.delegate =
          new fr.upmc.ilp.ilp2.ast.CEASTassignment(
                  variable, value );
  }
  private fr.upmc.ilp.ilp2.ast.CEASTassignment delegate;

  @Override
  public IAST2assignment<CEASTparseException> getDelegate () {
      return this.delegate;
  }

  @ILPvariable
  public IAST4variable getVariable () {
    return CEAST.narrowToIAST4variable(getDelegate().getVariable());
  }
  @ILPexpression
  public IAST4expression getValue() {
    return CEAST.narrowToIAST4expression(getDelegate().getValue());
  }

  public static IAST2assignment<CEASTparseException> parse (
          final Element e, final IParser parser)
  throws CEASTparseException {
      return fr.upmc.ilp.ilp2.ast.CEASTassignment.parse(e, parser);
  }

  /**
   * Normaliser l'affectation en l'une de ses sous-classes suivant
   * la nature de la variable affectee.  */
  @Override
  public IAST4expression normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
    IAST4variable variable_ = 
        getVariable().normalize(lexenv, common, factory);
    final IAST4expression value_ = 
        getValue().normalize(lexenv, common, factory);

    if ( variable_ instanceof IAST4globalVariable ) {
      final IAST4globalVariable gv = (IAST4globalVariable) variable_;
      return factory.newGlobalAssignment(gv, value_);

    } else if ( variable_ instanceof IAST4localVariable ) {
        final IAST4localVariable lv = (IAST4localVariable) variable_;
      return factory.newLocalAssignment(lv, value_);

    } else {
        final String msg = "Should never occur!";
        assert false : msg;
        throw new NormalizeException(msg);
    }
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
  @Override
  public void findInvokedFunctions () {
      findAndAdjoinToInvokedFunctions(getValue());
  }

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
      getValue().inline();
  }
  */

  @Override
  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common,
                       final IDestination destination)
  throws CgenerationException {
    getValue().compile(buffer, lexenv, common,
            new AssignDestination(getVariable()) );
    buffer.append(";\n");
    buffer.append(getVariable().getMangledName());
    buffer.append(";\n");
  }

  @Override
  public Object eval (final ILexicalEnvironment lexenv, final ICommon common)
  throws EvaluationException {
      final String msg = "Should never occur!";
      assert false : msg;
      throw new EvaluationException(msg);
  }

  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTassignment.java
