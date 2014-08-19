/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTinstantiate.java 542 2006-10-26 18:53:30Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.ast;

import java.util.Set;

import org.w3c.dom.Element;

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
import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6instantiation;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IParser6;
import fr.upmc.ilp.ilp6.runtime.ICommon;
import fr.upmc.ilp.ilp6.runtime.ILPClass;
import fr.upmc.ilp.ilp6.runtime.ILPinstance;

/** Créer un objet c'est-à-dire, en jargon, instantier une classe. */

public class CEASTinstantiate
extends CEAST6expression
implements IAST6instantiation {

  public CEASTinstantiate (final String className,
                           final IAST4expression[] arguments) {
    this.className = className;
    this.arguments = arguments;
  }
  private final String className;
  private final IAST4expression[] arguments;

  public String getClassName () {
      return this.className;
  }
  @ILPexpression(isArray=true)
  public IAST4expression[] getArguments () {
      return this.arguments;
  }

  public static IAST6instantiation parse (final Element e, final IParser6 parser)
  throws CEASTparseException {
      final String className = e.getAttribute("classe");
      final IAST4expression[] arguments =
          parser.parseList(e.getChildNodes())
          .toArray(new IAST4expression[0]);
      return parser.getFactory().newInstantiation(
              className, arguments);
  }

  // Plus d'acces direct aux champs a partir d'ici.

  @Override
  public Object eval6 (final ILexicalEnvironment lexenv,
                       final ICommon common)
    throws EvaluationException {
    final ILPClass clazz = common.findClass(getClassName());
    IAST4expression[] arguments = getArguments();
    final Object[] values = new Object[arguments.length];
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      values[i] = arguments[i].eval(lexenv, common);
    }
    return new ILPinstance(clazz, values);
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
    final IAST4expression[] arguments = getArguments();
    final IAST4variable[] tempvar = new IAST4variable[arguments.length];
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      tempvar[i] = CEASTlocalVariable.generateVariable();
      tempvar[i].compileDeclaration(buffer, lexenv, common);
    }
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      arguments[i].compile(buffer, lexenv, common,
                          new AssignDestination(tempvar[i]));
      buffer.append(";\n");
    }
    buffer.append(tempInstance.getMangledName());
    buffer.append(" = ILP_make_instance((ILP_Class) &ILP_object_");
    buffer.append(getClassName());
    buffer.append("_class);\n");
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      buffer.append(tempInstance.getMangledName());
      buffer.append("->_content.asInstance.field[");
      buffer.append(i);
      buffer.append("] = ");
      buffer.append(tempvar[i].getMangledName());
      buffer.append(";\n");
    }
    destination.compile(buffer, lexenv, common);
    buffer.append(tempInstance.getMangledName());
    buffer.append(";\n}\n");
  }

  @Override
  public void findGlobalVariables (
          final Set<IAST2variable> globalvars,
          final fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment lexenv ) {
      final IAST4expression[] arguments = getArguments();
      for ( int i = 0 ; i<arguments.length ; i++ ) {
          arguments[i].findGlobalVariables(globalvars, lexenv);
    }
  }

  @Override
  public IAST6instantiation normalize6 (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST6Factory factory )
    throws NormalizeException {
      final IAST4expression[] arguments = getArguments();
      IAST6classDefinition cd = common.findClassDefinition(getClassName());
      if ( arguments.length != cd.fieldSize(common) ) {
          final String msg = "Wrong constructor arity for " + getClassName()
          + " expected: " + cd.fieldSize(common)
          + " obtained: " + arguments.length;
          throw new NormalizeException(msg);
      }
    IAST4expression[] arguments_ = new IAST4expression[arguments.length];
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      arguments_[i] = CEAST.narrowToIAST4expression(
              arguments[i].normalize(lexenv, common, factory) );
    }
    return factory.newInstantiation(getClassName(), arguments_);
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
  
  @Override
  public void compileExpression(StringBuffer buffer,
        ICgenLexicalEnvironment lexenv,
        fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment common,
        IDestination destination) throws CgenerationException {
      compile6(buffer,
               lexenv,
               CEAST6.narrowToICgenEnvironment(common),
               destination );   
  }
  
  @Override
  public void compileInstruction(StringBuffer buffer,
        ICgenLexicalEnvironment lexenv,
        fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment common,
        IDestination destination) throws CgenerationException {
      compile6(buffer,
              lexenv,
              CEAST6.narrowToICgenEnvironment(common),
              destination );    
  }
}

// end of CEASTinstantiate.java
