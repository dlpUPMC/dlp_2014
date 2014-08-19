/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTfunctionDefinition.java 1247 2012-09-19 14:24:59Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import java.util.Set;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.cgen.ReturnDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4delegable;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Définition d'une fonction globale. */

public class CEASTfunctionDefinition
extends CEAST
implements IAST4functionDefinition, IAST4delegable {

  public CEASTfunctionDefinition (final IAST4globalFunctionVariable global,
                                  final IAST4variable[] variable,
                                  final IAST4expression body ) {
      this.delegate =
          new fr.upmc.ilp.ilp2.ast.CEASTfunctionDefinition(
                  global.getName(), variable, body );
      this.global = global;
      this.global.setFunctionDefinition(this);
  }
  private IAST4globalFunctionVariable global;
  private fr.upmc.ilp.ilp2.ast.CEASTfunctionDefinition delegate;

  public fr.upmc.ilp.ilp2.ast.CEASTfunctionDefinition getDelegate () {
      return this.delegate;
  }

  public IAST4globalFunctionVariable getDefinedVariable () {
    return this.global;
  }
  public String getFunctionName (){
    return this.delegate.getFunctionName();
  }
  public String getMangledFunctionName () {
      return this.delegate.getMangledFunctionName();
  }
  @ILPvariable(isArray=true)
  public IAST4localVariable[] getLocalVariables () {
      return CEAST.narrowToIAST4localVariableArray(this.delegate.getVariables());
  }
  public IAST4variable[] getVariables () {
      return CEAST.narrowToIAST4variableArray(this.delegate.getVariables());
  }
  @ILPexpression
  public IAST4expression getBody () {
      return CEAST.narrowToIAST4expression(this.delegate.getBody());
  }

  public static IAST4functionDefinition parse(
          final Element e, final IParser parser)
  throws CEASTparseException {
      IAST2functionDefinition<CEASTparseException> delegate =
          fr.upmc.ilp.ilp2.ast.CEASTfunctionDefinition.parse(e, parser);
      IAST4Factory factory = parser.getFactory();
      IAST4globalFunctionVariable gfv =
          factory.newGlobalFunctionVariable(delegate.getFunctionName());
      return factory.newFunctionDefinition(
              gfv,
              CEAST.narrowToIAST4variableArray(delegate.getVariables()),
              CEAST.narrowToIAST4expression(delegate.getBody()) );
  }

  @Override
  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
  throws EvaluationException {
      return getDelegate().eval(lexenv, common);
  }

  /** Émettre le prototype de la fonction globale. Cela permettra
   * d'assurer la récursion mutuelles des fonctions globales. */

  public void compileHeader (final StringBuffer buffer,
                             final ICgenLexicalEnvironment lexenv,
                             final ICgenEnvironment common)
    throws CgenerationException {
    buffer.append("static ILP_Object ");
    buffer.append(getDefinedVariable().getMangledName());
    compileVariableList(buffer);
    buffer.append(";\n");
  }

  /** Étendre un environnement lexical de compilation avec les
   * variables de la fonction. */

  public ICgenLexicalEnvironment extendWithFunctionVariables (
          final ICgenLexicalEnvironment lexenv )
  {
    ICgenLexicalEnvironment newlexenv = lexenv;
    final IAST4variable[] variables = getVariables();
    for ( int i = 0 ; i<variables.length ; i++ ) {
      newlexenv = newlexenv.extend(variables[i]);
    }
    return newlexenv;
  }

  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common )
    throws CgenerationException {
    // Émettre en commentaire les fonctions appelées:
      if ( getInvokedFunctions().size() > 0 ) {
          buffer.append("/* Fonctions globales invoquées: ");
          for ( IAST4globalFunctionVariable gv : getInvokedFunctions() ) {
              buffer.append(gv.getMangledName());
              buffer.append(" ");
          }
          buffer.append(" */\n");
      }
    if ( this.isRecursive() ) {
      buffer.append("/* Cette fonction est récursive. */\n");
    }
    // Émettre la définition de la fonction:
    buffer.append("\nILP_Object\n");
    buffer.append(getDefinedVariable().getMangledName());
    compileVariableList(buffer);
    buffer.append("\n{\n");
    final ICgenLexicalEnvironment bodyLexenv =
      this.extendWithFunctionVariables(lexenv);
    getBody().compile(buffer, bodyLexenv, common, ReturnDestination.create());
    buffer.append(";\n}");
  }

  public void compileVariableList (final StringBuffer buffer)
    throws CgenerationException {
    buffer.append(" (");
    final IAST4variable[] variables = getVariables();
    for ( int i = 0 ; i<variables.length-1 ; i++ ) {
      buffer.append("    ILP_Object ");
      buffer.append(variables[i].getMangledName());
      buffer.append(",\n");
    }
    if ( variables.length > 0 ) {
      buffer.append("    ILP_Object ");
      buffer.append(variables[variables.length-1].getMangledName());
    }
    buffer.append(" ) ");
  }
  // heriter aupres du delegue ???

  /** Normaliser une fonction globale revient principalement à
   * normaliser son corps. */

  @Override
  public IAST4functionDefinition normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
      final IAST4globalFunctionVariable gfv =
          CEAST.narrowToIAST4globalFunctionVariable(
                  getDefinedVariable().normalize(lexenv, common, factory));
      INormalizeLexicalEnvironment bodyLexenv = lexenv;
      final IAST4variable[] variables = getVariables();
      final IAST4variable[] variables_ = new IAST4variable[variables.length];
      for ( int i = 0 ; i<variables.length ; i++ ) {
          variables_[i] = factory.newLocalVariable(variables[i].getName());
          bodyLexenv = bodyLexenv.extend(variables_[i]);
      }
      final IAST4expression body_ = 
          getBody().normalize(bodyLexenv, common, factory);
      final IAST4functionDefinition fd = 
          factory.newFunctionDefinition(gfv, variables_, body_);
      return fd;
  }

  /** Déterminer si la fonction est récursive. Cette méthode ne
   * fonctionne qu'après avoir calculé le graphe des appels. */

  public boolean isRecursive () {
    for ( IAST4variable gv : getInvokedFunctions() ) {
        if ( gv == getDefinedVariable() ) {
            return true;
          }
    }
    return false;
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
  @Override
  public void findInvokedFunctions () {
      findAndAdjoinToInvokedFunctions(getBody());
  }

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
    getBody().inline();
  }
  */

  @Override
  public void findGlobalVariables (final Set<IAST2variable> globalvars,
          final ICgenLexicalEnvironment lexenv ) {
      final ICgenLexicalEnvironment newlexenv = 
              this.extendWithFunctionVariables(lexenv);
      getBody().findGlobalVariables(globalvars, newlexenv);
  }
  
  public <Data, Result, Exc extends Throwable> Result 
  accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
    return visitor.visit(this, data);
}
}

// end of CEASTfunctionDefinition.java
