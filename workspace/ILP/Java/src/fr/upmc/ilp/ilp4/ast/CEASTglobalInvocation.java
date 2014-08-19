/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTglobalInvocation.java 1247 2012-09-19 14:24:59Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import java.util.Set;

import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

/** Les invocations aux fonctions globales. C'est une classe technique
 * introduite par la normalisation. */

public class CEASTglobalInvocation
extends CEASTinvocation
implements IAST4globalInvocation {

  public CEASTglobalInvocation (final IAST4globalFunctionVariable function,
                                final IAST4expression[] argument) {
      super(new CEASTreference(function), argument);
      this.function = function;
  }
  private final IAST4globalFunctionVariable function;

  @ILPvariable
  public IAST4globalFunctionVariable getFunctionGlobalVariable () {
    return this.function;
  }

  /** Interprétation prenant en compte l'éventuelle intégration. */

  @Override
  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
    if ( this.inlined == null ) {
        return super.eval(lexenv, common);
    } else {
      return this.inlined.eval(lexenv, common);
    }
  }

  @Override
  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common,
                       final IDestination destination)
    throws CgenerationException {
    if ( this.inlined == null ) {
        // L'invocation n'a pas été intégrée.
        compileInvocation(buffer, lexenv, common, destination);
    } else {
      // L'invocation a été intégrée.
      buffer.append("/* Appel intégré à ");
      buffer.append(getFunctionGlobalVariable().getMangledName());
      buffer.append(" */");
      this.inlined.compile(buffer, lexenv, common, destination);
    }
  }

  @Override
  public IAST4expression normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
    // On vérifie au passage l'arité:
    checkArity();
    final IAST4globalFunctionVariable function_ =
        CEAST.narrowToIAST4globalFunctionVariable(
                getFunctionGlobalVariable().normalize(lexenv, common, factory));
    final IAST4expression[] arguments = getArguments();
    IAST4expression[] argument_ = new IAST4expression[arguments.length];
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      argument_[i] = arguments[i].normalize(lexenv, common, factory);
    }
    return factory.newGlobalInvocation(function_, argument_);
  }

  /** Verifier que l'invocation a la bonne arite vis-a-vis de la definition
   * de la fonction globale.
   */
  @Override
  public void checkArity ()
  throws NormalizeException {
      final IAST4functionDefinition fd =
          getFunctionGlobalVariable().getFunctionDefinition();
      final int arity = fd.getVariables().length;
      if ( arity != getArguments().length ) {
          final String msg = "arity error";
          throw new NormalizeException(msg);
      }
  }

  public void compileInvocation (final StringBuffer buffer,
                                 final ICgenLexicalEnvironment lexenv,
                                 final ICgenEnvironment common,
                                 final IDestination destination )
  throws CgenerationException {
      IAST4expression[] args = getArguments();
      IAST4localVariable[] tmps = new IAST4localVariable[args.length];
      ICgenLexicalEnvironment bodyLexenv = lexenv;
      buffer.append("{ ");
      for ( int i=0; i<args.length ; i++ ) {
          tmps[i] = CEASTlocalVariable.generateVariable();
          tmps[i].compileDeclaration(buffer, lexenv, common);
          bodyLexenv = bodyLexenv.extend(tmps[i]);
      }
      for ( int i=0 ; i<args.length ; i++ ) {
          args[i].compile(buffer, bodyLexenv, common,
                  new AssignDestination(tmps[i]) );
          buffer.append(";\n");
      }
      destination.compile(buffer, bodyLexenv, common);
      buffer.append(getFunctionGlobalVariable().getMangledName());
      buffer.append("(");
      for ( int i=0 ; i<(args.length-1) ; i++ ) {
          buffer.append(tmps[i].getMangledName());
          buffer.append(", ");
      }
      if ( args.length > 0 ) {
          buffer.append(tmps[args.length-1].getMangledName());
      }
      buffer.append(");\n}\n");
  }

  @Override
  public void computeInvokedFunctions ()
  throws FindingInvokedFunctionsException {
      addInvokedFunction(getFunctionGlobalVariable());
      super.computeInvokedFunctions();
  }

  /** Intégration de la fonction globale invoquée (si non
   * récursive) et si non déjà intégrée. */

  @Override
  public void inline (IAST4Factory factory) throws InliningException {
    if ( this.getInlined() != null ) {
        return;
    } else {
        // On analyse les arguments!
        for ( IAST4expression arg : getArguments() ) {
            arg.inline(factory);
        }
        final IAST4functionDefinition function =
            getFunctionGlobalVariable().getFunctionDefinition();
        if ( function.isRecursive() ) {
            // On n'intègre pas les fonctions récursives!
            return;
        } else {
            // La fonction a toutes les qualités requises, on l'intègre!
            this.setInlined(factory.newLocalBlock(
                    function.getVariables(),
                    getArguments(),
                    function.getBody()));
            // inlined.inline(); // deja fait quand function fut analysée.
            return;
        }
    }
  }

  /** Rendre la version integree de l'expression. */
  @Override
  public @OrNull IAST4expression getInlined () {
      return this.inlined;
  }
  public void setInlined(IAST4expression inlined) {
      this.inlined = inlined;
  }
  // Seules les invocations a des fonctions globales sont integrees.
  private IAST4expression inlined = null;

  /** Suivre l'expression integree pour determiner les variables globales. */

  @Override
  public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                 final ICgenLexicalEnvironment lexenv ) {
    if ( this.inlined != null ){
        this.inlined.findGlobalVariables(globalvars, lexenv);
    } else {
        super.findGlobalVariables(globalvars, lexenv);
    }
  }

  @Override 
  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTglobalInvocation.java
