/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2008 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTsend.java 542 2006-10-26 18:53:30Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.ast;

import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.CEAST;
import fr.upmc.ilp.ilp4.ast.CEASTlocalVariable;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6send;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IParser6;
import fr.upmc.ilp.ilp6.runtime.ICommon;
import fr.upmc.ilp.ilp6.runtime.ILPinstance;

/** Envoi de message c'est-à-dire invocation d'une méthode. */

public class CEASTsend
extends CEAST6expression
implements IAST6send {

  public CEASTsend (final String methodName,
                    final IAST4expression receiver,
                    final IAST4expression[] arguments) {
    this.methodName = methodName;
    this.receiver = receiver;
    this.arguments = arguments;
  }
  private final String methodName;
  private final IAST4expression receiver;
  private final IAST4expression[] arguments;

  public String getMethodName () {
      return this.methodName;
  }
  @ILPexpression
  public IAST4expression getReceiver () {
      return this.receiver;
  }
  @ILPexpression(isArray=true)
  public IAST4expression[] getArguments () {
      return this.arguments;
  }

  public static IAST6send parse (final Element e, final IParser6 parser)
  throws CEASTparseException  {
      final String message = e.getAttribute("message");
      try {
          final XPathExpression receiverPath = xPath.compile("./receveur/*");
          final Node nReceiver = (Node)
              receiverPath.evaluate(e, XPathConstants.NODE);
          final IAST4expression receiver = CEAST.narrowToIAST4expression(
                  parser.parse(nReceiver) );

          final IAST4expression[] arguments =
              parser.findThenParseChildAsList(e, "arguments")
              .toArray(new IAST4expression[0]);

              return parser.getFactory().newSend(
                      message, receiver, arguments);
      } catch (XPathExpressionException e1) {
          final String msg = "Should never occur!";
          assert false : msg;
          throw new CEASTparseException(msg);
      }
  }
  private static final XPath xPath = XPathFactory.newInstance().newXPath();
  
  @Override
  public Object eval6 (final ILexicalEnvironment lexenv,
                       final ICommon common)
    throws EvaluationException {
    final Object target = getReceiver().eval(lexenv, common);
    IAST4expression[] arguments = getArguments();
    final Object[] value = new Object[arguments.length];
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      value[i] = arguments[i].eval(lexenv, common);
    }
    if ( target instanceof ILPinstance ) {
        ILPinstance o = (ILPinstance) target;
        return o.send(getMethodName(), value, common);
    } else {
        final String msg = "No such method " + getMethodName();
        throw new EvaluationException(msg);
    }
  }

  @Override
  public void compile6 (final StringBuffer buffer,
                        final ICgenLexicalEnvironment lexenv,
                        final ICgenEnvironment common,
                        final IDestination destination)
    throws CgenerationException {
    buffer.append("{ ");
    final IAST4variable tempInstance = CEASTlocalVariable.generateVariable();
    tempInstance.compileDeclaration(buffer, lexenv, common);
    final IAST4variable tempMethod = CEASTlocalVariable.generateVariable();
    buffer.append("  ILP_general_function ");
    buffer.append(tempMethod.getName());
    buffer.append(";\n");
    IAST4expression[] arguments = getArguments();
    final IAST4variable[] tempvar = new IAST4variable[arguments.length];
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      tempvar[i] = CEASTlocalVariable.generateVariable();
      tempvar[i].compileDeclaration(buffer, lexenv, common);
    }
    // getReceiver() et non this.receiver. Vu par Hiep Luu <htr999@gmail.com>
    getReceiver().compile(buffer, lexenv, common,
                     new AssignDestination(tempInstance));
    buffer.append(";\n");
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      arguments[i].compile(buffer, lexenv, common,
                          new AssignDestination(tempvar[i]));
      buffer.append(";\n");
    }
    // Déterminer la méthode séparément (mieux pour gdb):
    buffer.append(tempMethod.getName());
    buffer.append(" = ILP_find_method(");
    buffer.append(tempInstance.getMangledName());
    buffer.append(", &ILP_object_");
    buffer.append(getMethodName());
    buffer.append("_method, ");
    buffer.append(1 + arguments.length);
    buffer.append(");\n");
    // Invoquer la méthode:
    destination.compile(buffer, lexenv, common);
    buffer.append(tempMethod.getName());
    buffer.append("(");
    buffer.append(tempInstance.getMangledName());
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      buffer.append(", ");
      buffer.append(tempvar[i].getMangledName());
    }
    buffer.append(");\n}\n");
  }

  @Override
  public void findGlobalVariables (
          final Set<IAST2variable> globalvars,
          final fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment lexenv ) {
      IAST4expression[] arguments = getArguments();
      getReceiver().findGlobalVariables(globalvars, lexenv);
      for ( int i = 0 ; i<arguments.length ; i++ ) {
          arguments[i].findGlobalVariables(globalvars, lexenv);
      }
  }

  @Override
  public IAST6send normalize6 (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          IAST6Factory factory )
    throws NormalizeException {
      IAST4expression receiver_ = CEAST.narrowToIAST4expression(
              getReceiver().normalize(lexenv, common, factory) );
      IAST4expression[] arguments = getArguments();
      IAST4expression[] arguments_ = new IAST4expression[arguments.length];
      for ( int i = 0 ; i<arguments.length ; i++ ) {
          arguments_[i] = CEAST.narrowToIAST4expression(
                  arguments[i].normalize(lexenv, common, factory) );
      }
      return factory.newSend(getMethodName(), receiver_, arguments_);
  }

  public <Data, Result, Exc extends Throwable> Result 
  accept (IAST6visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
  public <Data, Result, Exc extends Throwable> Result 
  accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return CEAST6.narrowToIAST6visitor(visitor).visit(this, data);
  }
  // NOTE: double methode surchargee.

}

// end of CEASTsend.java
