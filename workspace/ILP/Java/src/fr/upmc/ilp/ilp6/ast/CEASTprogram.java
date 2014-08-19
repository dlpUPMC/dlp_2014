/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTprogram.java 1299 2013-08-27 07:09:39Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.ast;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.CEAST;
import fr.upmc.ilp.ilp4.ast.CEASTexpression;
import fr.upmc.ilp.ilp4.ast.CEASTfunctionDefinition;
import fr.upmc.ilp.ilp4.ast.CEASTglobalFunctionVariable;
import fr.upmc.ilp.ilp4.ast.FindingInvokedFunctionsException;
import fr.upmc.ilp.ilp4.ast.InliningException;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6methodDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6program;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IParser6;
import fr.upmc.ilp.ilp6.runtime.ICommon;

/** La classe d'un programme composé de fonctions globales et
 * d'instructions. Ce n'est pas une expression ni une instruction mais
 * un programme. */

public class CEASTprogram
extends CEAST6
implements IAST6program {
    
  protected CEASTprogram (final IAST4functionDefinition[] definitions,
                          final IAST6classDefinition[] clazzes,
                          final IAST4expression body ) {
      this.delegate = new fr.upmc.ilp.ilp4.ast.CEASTprogram(definitions, body);
      this.clazzes = clazzes;
  }
  private final fr.upmc.ilp.ilp4.ast.CEASTprogram delegate;
  private final IAST6classDefinition[] clazzes;
    
  public fr.upmc.ilp.ilp4.ast.CEASTprogram getDelegate () {
      return this.delegate;
  }

  @ILPexpression(isArray=true)
  public IAST6classDefinition[] getClassDefinitions () {
      return this.clazzes;
  }
  @ILPexpression
  public IAST4expression getBody () {
    return this.getDelegate().getBody();
  }
  @ILPexpression(isArray=true)
  public IAST4functionDefinition[] getFunctionDefinitions () {
      IAST2functionDefinition<CEASTparseException>[] fds =
          this.getDelegate().getFunctionDefinitions();
      IAST4functionDefinition[] result =
          new IAST4functionDefinition[fds.length];
      System.arraycopy(fds, 0, result, 0, fds.length);
      return result;
  }

  /** Le constructeur analysant syntaxiquement un DOM. */

  public static IAST6program parse (final Element e, final IParser6 parser)
          throws CEASTparseException {
      List<IAST2<CEASTparseException>> itemsAsList =
          parser.parseList(e.getChildNodes());
      IAST4[] items = itemsAsList.toArray(new IAST4[0]);
      final List<IAST4functionDefinition> functionDefinitions = new Vector<>();
      final List<IAST6classDefinition> classDefinitions = new Vector<>();
      final List<IAST4expression> instructions = new Vector<>();
      for ( IAST4 item : items ) {
          if ( item instanceof IAST4functionDefinition ) {
              functionDefinitions.add((IAST4functionDefinition) item);
          } else if ( item instanceof IAST6classDefinition ) {
              classDefinitions.add((IAST6classDefinition) item);
          } else if ( item instanceof IAST4expression ) {
              instructions.add((IAST4expression) item);
          } else {
              final String msg = "Should never occur!";
              assert false : msg;
              throw new CEASTparseException(msg);
          }
      }
      IAST4functionDefinition[] defs =
          functionDefinitions.toArray(new IAST4functionDefinition[0]);
      IAST6classDefinition[] clazzes =
          classDefinitions.toArray(new IAST6classDefinition[0]);
      IAST4expression instrs = parser.getFactory().newSequence(
              instructions.toArray(CEASTexpression.EMPTY_EXPRESSION_ARRAY));
      return parser.getFactory().newProgram(defs, clazzes, instrs);
  }
  // NOTE: utiliser Xpath pour trier classes/fonctions/instructions.

  /** Interpréter un programme tout entier. On évalue les fonctions
   * globales ce qui enrichit au passage l'environnement global
   * (common) puis on évalue le corps du programme c'est-à-dire les
   * expressions qui ne sont pas dans des fonctions globales. */

  @Override
  public Object eval6 (final ILexicalEnvironment lexenv,
                       final ICommon common)
    throws EvaluationException {
      IAST6classDefinition[] clazzes = getClassDefinitions();
      for ( int i = 0 ; i<clazzes.length ; i++ ) {
          clazzes[i].eval(lexenv, common);
      }
      return getDelegate().eval(lexenv, common);
  }
  
  // Compilation

  @Override
  public String compile(
        final ICgenLexicalEnvironment lexenv,
        final fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment common)
    throws CgenerationException {
      return compile6(
              lexenv,
              CEAST6.narrowToICgenEnvironment(common));
  }
  
  public String compile6 (final ICgenLexicalEnvironment lexenv,
                          final ICgenEnvironment common )
    throws CgenerationException {
    final StringBuffer buffer = new StringBuffer(4095);
    this.compile6(buffer, lexenv, common);
    return buffer.toString();
  }

  /** Compiler un programme tout entier. */

  @Override
  public void compile6 (final StringBuffer buffer,
                        final ICgenLexicalEnvironment lexenv,
                        final ICgenEnvironment common )
    throws CgenerationException {
      buffer.append("#include <stdio.h>\n");
      buffer.append("#include <stdlib.h>\n");
      buffer.append("#include <time.h>\n");
      buffer.append("\n");
      buffer.append("#include \"ilpObj.h\"\n");
      buffer.append("\n");
      // Déclarer les variables globales:
      buffer.append("/* Variables ou prototypes globaux: */\n");
      for ( IAST2variable var : getGlobalVariables() ) {
          IAST4globalVariable v = CEAST.narrowToIAST4globalVariable(var);
          v.compileGlobalDeclaration(buffer, lexenv, common);
      }
      IAST4functionDefinition[] definitions = getFunctionDefinitions();
      for ( IAST4functionDefinition fun : definitions ) {
          fun.compileHeader(buffer, lexenv, common);
      }
      // Déclarer les classes aussi:
      IAST6classDefinition[] clazzes = getClassDefinitions();
      for ( IAST6classDefinition clazz : clazzes ) {
          common.addClassDefinition(clazz);
      }
      for ( IAST6classDefinition clazz : clazzes ) {
          clazz.compileHeader(buffer, lexenv, common);
          // on en profite pour etablir le lien methode -> classe
          for ( IAST6methodDefinition method : clazz.getProperMethodDefinitions() ) {
              method.setDefiningClass(clazz);
          }
      }
      // Engendrer les classes et leurs methodes:
      buffer.append("\n/* Classes: */\n");
      for ( IAST6classDefinition clazz : clazzes ) {
          clazz.compile(buffer, lexenv, common);
      }
      // Engendrer le code des fonctions globales:
      buffer.append("\n/* Fonctions globales: */\n");
      for ( IAST4functionDefinition fun : definitions ) {
          fun.compile(buffer, lexenv, common);
      }
      // Émettre les instructions regroupées dans une fonction:
      IAST4globalFunctionVariable program =
          new CEASTglobalFunctionVariable(
                  fr.upmc.ilp.ilp4.ast.CEASTprogram.PROGRAM);
      buffer.append("\n/* Code hors fonction: */");
      IAST4functionDefinition bodyAsFunction =
          new CEASTfunctionDefinition(
                  program,
                  new IAST4variable[0],
                  getBody() );
      bodyAsFunction.compile(buffer, lexenv, common);
      buffer.append("\n");
      buffer.append("static ILP_Object ilp_caught_program () {\n");
      buffer.append("  struct ILP_catcher* current_catcher = ILP_current_catcher;\n");
      buffer.append("  struct ILP_catcher new_catcher;\n");
      buffer.append("\n");
      buffer.append("  if ( 0 == setjmp(new_catcher._jmp_buf) ) {\n");
      buffer.append("    ILP_establish_catcher(&new_catcher);\n");
      buffer.append("    return ilp_program();\n");
      buffer.append("  };\n");
      buffer.append("  /* Une exception est survenue. */\n");
      buffer.append("  return ILP_current_exception;\n");
      buffer.append("}\n");
      buffer.append("\n");
      buffer.append("int main (int argc, char *argv[]) {\n");
      buffer.append("  ILP_START_GC;\n");
      buffer.append("  ILP_print(ilp_caught_program());\n");
      buffer.append("  ILP_newline();\n");
      buffer.append("  return EXIT_SUCCESS;\n");
      buffer.append("}\n\n");
      buffer.append("\n/* fin */\n");
  }
  
  @Override
  public void compileHeader6 (
          final StringBuffer buffer,
          final ICgenLexicalEnvironment lexenv,
          final ICgenEnvironment common )
  throws CgenerationException {
      // rien!
  }

  /** Normaliser un programme dans un environnement lexical et global
   * particuliers. */

  @Override
  public IAST4program normalize (
          final INormalizeLexicalEnvironment lexenv,
          final fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
      return normalize6(lexenv, 
            CEAST6.narrowToINormalizeGlobalEnvironment(common), 
            CEAST6.narrowToIAST6Factory(factory) );
  }
  
  public IAST6program normalize6 (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST6Factory factory )
    throws NormalizeException {
      // Introduire d'abord toutes les variables globales nommant les
      // fonctions globales:
      IAST4functionDefinition[] definitions = getFunctionDefinitions();
      for ( int i = 0 ; i<definitions.length ; i++ ) {
          IAST4globalFunctionVariable gfv =
              factory.newGlobalFunctionVariable(
                      definitions[i].getDefinedVariable().getName());
          gfv.setFunctionDefinition(definitions[i]);
          common.add(gfv);
      }
      // ainsi que les definitions a normaliser des classes:
      // (les classes normalisent leurs methodes au passage et créent les
      // fonctions associées)
      IAST6classDefinition[] clazzes = getClassDefinitions();
      IAST6classDefinition[] clazzes_ =
          new IAST6classDefinition[clazzes.length];
      for ( int i = 0 ; i<clazzes.length ; i++ ) {
          clazzes_[i] = (IAST6classDefinition) clazzes[i]
              .normalize(lexenv, common, factory);
          common.addClassDefinition(clazzes_[i]);
      }
      // Normaliser les fonctions globales (et les méthodes):
      final IAST4functionDefinition[] definitions_ =
          new IAST4functionDefinition[definitions.length + 1];
      for ( int i = 0 ; i<definitions.length ; i++ ) {
          definitions_[i] = definitions[i].normalize(lexenv, common, factory);
      }
      // Empaqueter le code hors fonction en une fonction globale:
      final IAST4expression oldBody = 
              getBody().normalize(lexenv, common, factory);
      final IAST4globalFunctionVariable program =
          CEASTglobalFunctionVariable.generateGlobalFunctionVariable(factory);
      common.add(program);
      final IAST4functionDefinition bodyAsFunction =
          factory.newFunctionDefinition(program, new IAST4variable[0], oldBody);
      program.setFunctionDefinition(bodyAsFunction);
      definitions_[definitions.length] = CEAST.narrowToIAST4functionDefinition(
          bodyAsFunction.normalize(lexenv, common, factory));
      final IAST4expression body_ =
          factory.newGlobalInvocation(program, new CEAST6expression[0]);
      return factory.newProgram(definitions_, clazzes_, body_);
  }

  @Override
  public void computeInvokedFunctions ()
  throws FindingInvokedFunctionsException {
      this.getDelegate().computeInvokedFunctions();
  }

  @Override
  public void inline (IAST4Factory factory) throws InliningException {
      this.getDelegate().inline(factory);
  }

  /** Recensement des variables globales. */

  // NOTE: on utilise ilp6.cgen.CgenEnvironment
  @Override
  public void computeGlobalVariables () {
      globals = GlobalCollector6.getGlobalVariables(this);
  }
  @Deprecated
  public void computeGlobalVariables (final ICgenLexicalEnvironment lexenv) {
      // Cette methode est heritee mais son argument ne sert plus a rien car
      // on a change de mode de calcul des variables globales.
      computeGlobalVariables();
  }
  public IAST4globalVariable[] getGlobalVariables () {
      return this.globals;
  }
  private IAST4globalVariable[] globals = new IAST4globalVariable[0];
  public void setGlobalVariables (IAST4globalVariable[] globals) {
      this.globals = globals;
  }
  
  /** Visiteur */

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
  public void findGlobalVariables (final Set<IAST2variable> globalvars,
          final ICgenLexicalEnvironment lexenv ) {
      throw new RuntimeException("Should not occurr!");
  }
}

// end of CEASTprogram.java
