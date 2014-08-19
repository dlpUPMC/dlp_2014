/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTbinaryOperation.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2binaryOperation;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4binaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Opérateurs binaires. */

public class CEASTbinaryOperation
  extends CEASToperation
  implements IAST4binaryOperation {

  public CEASTbinaryOperation (final String operatorName,
                               final IAST4expression left,
                               final IAST4expression right) {
      this.delegate =
          new fr.upmc.ilp.ilp2.ast.CEASTbinaryOperation(
                  operatorName, left, right);
  }
  private fr.upmc.ilp.ilp2.ast.CEASTbinaryOperation delegate;

  @Override
  public IAST2binaryOperation<CEASTparseException> getDelegate () {
      return this.delegate;
  }

  public static IAST2binaryOperation<CEASTparseException> parse (
           final Element e, final IParser parser)
  throws CEASTparseException {
      return fr.upmc.ilp.ilp2.ast.CEASTbinaryOperation.parse(e, parser);
  }

  @ILPexpression
  public IAST4expression getLeftOperand () {
    return CEAST.narrowToIAST4expression(this.delegate.getLeftOperand());
  }
  @ILPexpression
  public IAST4expression getRightOperand () {
    return CEAST.narrowToIAST4expression(getDelegate().getRightOperand());
  }
  public IAST4expression[] getOperands () {
      IAST2expression<CEASTparseException>[] operands =
          getDelegate().getOperands();
      return CEAST.narrowToIAST4expressionArray(operands);
  }

  @Override
  public IAST4expression normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
      return factory.newBinaryOperation(
              getOperatorName(),
              getLeftOperand().normalize(lexenv, common, factory),
              getRightOperand().normalize(lexenv, common, factory) );
  }

  @Override
  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common,
                       final IDestination destination )
  throws CgenerationException {
      final IAST4variable right = CEASTlocalVariable.generateVariable();
      final IAST4variable left  = CEASTlocalVariable.generateVariable();
      buffer.append("{\n");
      ICgenLexicalEnvironment bodyLexenv = lexenv.extend(left).extend(right);
      right.compileDeclaration(buffer, lexenv, common);
      left.compileDeclaration(buffer, lexenv, common);
      getLeftOperand().compile(buffer, bodyLexenv, common,
              new AssignDestination(left) );
      buffer.append(";\n");
      getRightOperand().compile(buffer, bodyLexenv, common,
              new AssignDestination(right) );
      buffer.append(";\n");
      destination.compile(buffer, bodyLexenv, common);
      buffer.append(common.compileOperator2(getOperatorName()));
      buffer.append("(");
      buffer.append(left.getMangledName());
      buffer.append(", ");
      buffer.append(right.getMangledName());
      buffer.append(");}\n");
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
  @Override
  public void findInvokedFunctions () {
      findAndAdjoinToInvokedFunctions(getLeftOperand());
      findAndAdjoinToInvokedFunctions(getRightOperand());
  }

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
    getLeftOperand().inline();
    getRightOperand().inline();
  }
  */

  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTbinaryOperation.java
