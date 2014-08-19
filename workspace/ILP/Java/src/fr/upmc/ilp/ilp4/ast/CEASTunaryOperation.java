/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTunaryOperation.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryOperation;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Les opérations unaires. */

public class CEASTunaryOperation
  extends CEASToperation
  implements IAST4unaryOperation {

  public CEASTunaryOperation (final String operatorName,
                              final IAST4expression operand)
  {
    this.delegate =
        new fr.upmc.ilp.ilp2.ast.CEASTunaryOperation(operatorName, operand);
  }
  private fr.upmc.ilp.ilp2.ast.CEASTunaryOperation delegate;

  @Override
  public fr.upmc.ilp.ilp2.ast.CEASTunaryOperation getDelegate () {
      return this.delegate;
  }

  public static IAST2unaryOperation<CEASTparseException> parse
    (final Element e, final IParser parser)
  throws CEASTparseException {
    return fr.upmc.ilp.ilp2.ast.CEASTunaryOperation.parse(e, parser);
  }

  @ILPexpression
  public IAST4expression getOperand () {
    return CEAST.narrowToIAST4expression(getDelegate().getOperand());
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
      return factory.newUnaryOperation(
              getOperatorName(),
              getOperand().normalize(lexenv, common, factory) );
  }

  @Override
  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common,
                       final IDestination destination )
  throws CgenerationException {
      IAST4localVariable tmp = CEASTlocalVariable.generateVariable();
      buffer.append("{\n");
      tmp.compileDeclaration(buffer, lexenv, common);
      ICgenLexicalEnvironment bodyLexenv = lexenv;
      bodyLexenv = bodyLexenv.extend(tmp);
      getOperand().compile(buffer, bodyLexenv, common,
              new AssignDestination(tmp) );
      buffer.append(";\n");
      destination.compile(buffer, bodyLexenv, common);
      buffer.append(common.compileOperator1(getOperatorName()));
      buffer.append("(");
      buffer.append(tmp.getMangledName());
      buffer.append(");}\n");
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
  @Override
  public void findInvokedFunctions () {
      findAndAdjoinToInvokedFunctions(getOperand());
  }

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
    getOperand().inline();
  }
  */

  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTunaryOperation.java
