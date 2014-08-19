/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTprogram.java 1319 2013-11-25 18:03:34Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

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
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4sequence;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** La classe d'un programme composé de fonctions globales et
 * d'instructions. Ce n'est pas une expression ni une instruction mais
 * un programme. */

public class CEASTprogram
extends CEAST implements IAST4program {

    public CEASTprogram (final IAST4functionDefinition[] definitions,
                          final IAST4expression body) {
        this.delegate = new fr.upmc.ilp.ilp3.CEASTprogram(definitions, body);
    }
    protected fr.upmc.ilp.ilp3.CEASTprogram delegate;

    public fr.upmc.ilp.ilp3.CEASTprogram getDelegate () {
        return this.delegate;
    }
    
    public static IAST4program parse (final Element e, final IParser parser)
            throws CEASTparseException {
        List<IAST2<CEASTparseException>> itemsAsList =
            parser.parseList(e.getChildNodes());
        IAST4[] items = itemsAsList.toArray(new IAST4[0]);
        final List<IAST4functionDefinition> definitions = new Vector<>();
        final List<IAST4expression> instructions = new Vector<>();
        for ( IAST4 item : items ) {
            if ( item instanceof IAST4functionDefinition ) {
                definitions.add((IAST4functionDefinition) item);
            } else if ( item instanceof IAST4expression ) {
                instructions.add((IAST4expression) item);
            } else {
                final String msg = "Should never occur!";
                assert false : msg;
                throw new CEASTparseException(msg);
            }
        }
        IAST4functionDefinition[] defs =
            definitions.toArray(new IAST4functionDefinition[0]);
        IAST4Factory factory = parser.getFactory();
        IAST4sequence body = factory.newSequence(
                instructions.toArray(new IAST4expression[0]));
        return factory.newProgram(defs, body);
    }

    @ILPexpression
    public IAST4expression getBody () {
      return CEAST.narrowToIAST4expression(this.getDelegate().getBody());
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

    @Override
    public Object eval (final ILexicalEnvironment lexenv,
                        final ICommon common)
    throws EvaluationException {
        return getDelegate().eval(lexenv, common);
    }

  /** Compiler un programme tout entier. */
  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common )
    throws CgenerationException {
      buffer.append("#include <stdio.h>\n");
      buffer.append("#include <stdlib.h>\n");
      buffer.append("\n");
      buffer.append("#include \"ilp.h\"\n");
      buffer.append("#include \"ilpException.h\"\n");
      buffer.append("\n");
      // Declarer les variables globales:
      buffer.append("/* Variables ou prototypes globaux: */\n");
      for ( IAST4globalVariable var : getGlobalVariables() ) {
          var.compileGlobalDeclaration(buffer, lexenv, common);
          if ( ! common.isPresent(var.getName()) ) {
            common.bindGlobal(var);
          }
      }
      IAST4functionDefinition[] definitions = getFunctionDefinitions();
      for ( IAST4functionDefinition fun : definitions ) {
          // On pourrait ne pas compiler les fonctions non recursives car 
          // si elles sont integrees, elles ne sont plus invoquees!
          fun.compileHeader(buffer, lexenv, common);
      }
      // Puis le code des fonctions globales:
      buffer.append("\n/* Fonctions globales: */\n");
      for ( IAST4functionDefinition fun : definitions ) {
          fun.compile(buffer, lexenv, common);
      }
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
      buffer.append("  ILP_print(ilp_caught_program());\n");
      buffer.append("  ILP_newline();\n");
      buffer.append("  return EXIT_SUCCESS;\n");
      buffer.append("}\n\n");
      buffer.append("\n/* fin */\n");
  }

  /** Le nom de la fonction C correspondant au programme. */
  public static String PROGRAM = "ilp_program";

  /** Compiler une instruction en une chaîne de caractères. C'est une
   * méthode permettant de compiler confortablement tout un programme
   * et d'obtenir le résultat sous forme d'une chaîne qu'il suffira
   * d'écrire dans un fichier pour le compiler avec un compilateur
   * C. */

  public String compile (final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common )
    throws CgenerationException {
    final StringBuffer buffer = new StringBuffer(4095);
    this.compile(buffer, lexenv, common);
    return buffer.toString();
  }

  /** Normaliser un programme dans un environnement lexical et global
   * particuliers. */

  @Override
  public IAST4program normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
      // Introduire d'abord toutes les variables globales nommant les
      // fonctions globales et les associer ensemble:
      IAST4functionDefinition[] definitions = getFunctionDefinitions();
      for ( int i = 0 ; i<definitions.length ; i++ ) {
          IAST4globalFunctionVariable gfv =
              factory.newGlobalFunctionVariable(
                      definitions[i].getDefinedVariable().getName());
          gfv.setFunctionDefinition(definitions[i]);
          common.add(gfv);
      }
      // On normalise toutes les definitions
      final IAST4functionDefinition[] definitions_ =
          new IAST4functionDefinition[definitions.length + 1];
      for ( int i = 0 ; i<definitions.length ; i++ ) {
          definitions_[i] = definitions[i].normalize(lexenv, common, factory);
      }
      // Empaqueter le code hors fonction en une fonction globale:
      final IAST4expression body_= 
          getBody().normalize(lexenv, common, factory);
      final IAST4globalFunctionVariable program =
          new CEASTglobalFunctionVariable(PROGRAM);
      common.add(program);
      final IAST4functionDefinition bodyAsFunction =
          factory.newFunctionDefinition(program, new IAST4variable[0], body_);
      program.setFunctionDefinition(bodyAsFunction);
      definitions_[definitions.length] = 
          bodyAsFunction.normalize(lexenv, common, factory);
      final IAST4expression body__ =
          factory.newGlobalInvocation(program, new CEASTexpression[0]);
      // Finalisation
      IAST4program program_ = factory.newProgram(definitions_, body__);
      return program_;
  }

  /** On calcule pour chaque fonction globale, les fonctions qu'elle
   * invoque puis on calcule la fermeture transitive. L'ensemble des
   * fonctions invoquées du programme n'est constitué que des seules
   * fonctions invoquées par son corps. */

  @Override
  public void computeInvokedFunctions ()
  throws FindingInvokedFunctionsException {
      IAST4functionDefinition[] definitions = getFunctionDefinitions();
      for ( int i = 0 ; i<definitions.length ; i++ ) {
          definitions[i].computeInvokedFunctions();
      }
      boolean shouldContinue = true;
      while ( shouldContinue ) {
          shouldContinue = false;
          for ( int i = 0 ; i<definitions.length ; i++ ) {
              final IAST4functionDefinition currentFunction = definitions[i];
              for ( IAST4globalFunctionVariable gv :
                      currentFunction.getInvokedFunctions()
                          .toArray(IAST4GFV_EMPTY_ARRAY) ) {
                  // currentFunction invoque gv donc elle invoque
                  // (indirectement) les fonctions qu'invoque gv.
                  final IAST4functionDefinition other =
                    gv.getFunctionDefinition();
                  // | et non || comme remarqué par <Jeremie.Lumbroso@etu.upmc.fr>
                  shouldContinue |= currentFunction
                      .addInvokedFunctions(other.getInvokedFunctions());
                  // NOTA: la precedente methode change la collection que l'on
                  // est en train d'inspecter ce qui pose des problemes
                  // d'acces simultanes a cette collection d'ou l'emploi d'un
                  // toArray() plus haut.
              }
          }
      }
      // Savoir ce qu'invoque le programme est de peu d'utilite!
      findAndAdjoinToInvokedFunctions(getBody());
  }
  public static final IAST4globalFunctionVariable[] IAST4GFV_EMPTY_ARRAY =
      new IAST4globalFunctionVariable[0];

  /** Integration de toutes les fonctions non recursives. */

  @Override
  public void inline (IAST4Factory factory) throws InliningException {
      IAST4functionDefinition[] definitions = getFunctionDefinitions();
      for ( IAST4functionDefinition fd : definitions ) {
          fd.inline(factory);
      }
      getBody().inline(factory);
  }

  /** Recensement des variables globales. */
  // Nouvelle version avec visiteur:
  public void computeGlobalVariables () {
      globals = GlobalCollector.getGlobalVariables(this);
  }
  // Ancienne version dépréciée:
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
  
  @Override
  public void findGlobalVariables (final Set<IAST2variable> globalvars,
          final ICgenLexicalEnvironment lexenv ) {
      throw new RuntimeException("Should not occurr!");
  }
  
  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTprogram.java
