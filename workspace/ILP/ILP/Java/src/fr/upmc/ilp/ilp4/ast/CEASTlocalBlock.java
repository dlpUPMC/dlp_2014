/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTlocalBlock.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2localBlock;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4localBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Bloc local. */

public class CEASTlocalBlock
extends CEASTdelegableInstruction
implements IAST4localBlock {

    /** Création d'un bloc local à partir de ses constituants
     * normalisés. On suppose ici que les tailles des deux vecteurs sont
     * bien égales. */

    public CEASTlocalBlock (
            final IAST4variable[] variable,
            final IAST4expression[] initialization,
            final IAST4expression body ) {
        assert(variable.length == initialization.length);
        this.delegate =
            new fr.upmc.ilp.ilp2.ast.CEASTlocalBlock(
                    variable, initialization, body );
    }
    private fr.upmc.ilp.ilp2.ast.CEASTlocalBlock delegate;

    @Override
    public fr.upmc.ilp.ilp2.ast.CEASTlocalBlock getDelegate () {
        return this.delegate;
    }

    // ou IAST4localBlock ???
    public static IAST2localBlock<CEASTparseException> parse (
            final Element e, final IParser parser)
    throws CEASTparseException {
      return fr.upmc.ilp.ilp2.ast.CEASTlocalBlock.parse(e, parser);
    }

    @ILPvariable(isArray=true)
    public IAST4variable[] getVariables () {
      return CEAST.narrowToIAST4variableArray(this.delegate.getVariables());
    }
    public IAST4localVariable[] getLocalVariables () {
        return CEAST.narrowToIAST4localVariableArray(this.delegate.getVariables());
      }
    @ILPexpression(isArray=true)
    public IAST4expression[] getInitializations () {
      return CEAST.narrowToIAST4expressionArray(this.delegate
                                                .getInitializations());
    }
    @ILPexpression
    public IAST4expression getBody() {
      return CEAST.narrowToIAST4expression(this.delegate.getBody());
    }

    @Override
    public IAST4expression normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
    throws NormalizeException {
        if ( getVariables().length == 0 ) {
            // On simplifie le bloc à son corps:
            return getBody().normalize(lexenv, common, factory);
        }
        
        IAST4expression[] initializations = getInitializations();
        IAST4expression[] initialization_ =
            new IAST4expression[initializations.length];
        for ( int i = 0 ; i<initializations.length ; i++ ) {
            initialization_[i] = 
                initializations[i].normalize(lexenv, common, factory);
        }
        // Normalisation du corps:
        INormalizeLexicalEnvironment bodyLexenv = lexenv;
        IAST4variable[] variables = getVariables();
        IAST4variable[] variables_ = new IAST4variable[variables.length];
        for ( int i = 0 ; i<variables.length ; i++ ) {
            final IAST4variable var = variables[i];
            variables_[i] = factory.newLocalVariable(var.getName());
            bodyLexenv = bodyLexenv.extend(variables_[i]);
        }
        final IAST4expression body_ = 
            getBody().normalize(bodyLexenv, common, factory);
        return factory.newLocalBlock(variables_, initialization_, body_);
    }

    @Override
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common,
                         final IDestination destination )
    throws CgenerationException {
        final IAST4localVariable[] vars = getLocalVariables();
        final IAST4expression[] inits = getInitializations();
        IAST4localVariable[] temp = new IAST4localVariable[vars.length];
        ICgenLexicalEnvironment templexenv = lexenv;

        buffer.append("{\n");
        for (int i = 0; i < vars.length; i++) {
            temp[i] = CEASTlocalVariable.generateVariable();
            templexenv = templexenv.extend(temp[i]);
            temp[i].compileDeclaration(buffer, templexenv, common);
        }

        for (int i = 0; i < vars.length; i++) {
            inits[i].compile(buffer, lexenv, common,
                    new AssignDestination(temp[i]) );
            buffer.append(";\n");
        }

        buffer.append("{\n");
        ICgenLexicalEnvironment bodylexenv = templexenv;
        for (int i = 0; i < vars.length; i++) {
            bodylexenv = bodylexenv.extend(vars[i]);
            vars[i].compileDeclaration(buffer, bodylexenv, common);
        }

        for (int i = 0; i < vars.length; i++) {
            buffer.append(vars[i].getMangledName());
            buffer.append(" = ");
            buffer.append(temp[i].getMangledName());
            buffer.append(";\n");
        }

        getBody().compile(buffer, bodylexenv, common, destination);
        buffer.append(";}\n}\n");
    }

    /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
    @Override
    public void findInvokedFunctions () {
        for ( IAST4expression init : getInitializations() ) {
            findAndAdjoinToInvokedFunctions(init);
        }
        findAndAdjoinToInvokedFunctions(getBody());
    }

    /* Obsolète de par CEAST.inline() qui use de réflexivité.
    @Override
    public void inline () {
        for ( IAST4expression init : this.getInitializations() ) {
            init.inline();
        }
        getBody().inline();
    }
    */

    public <Data, Result, Exc extends Throwable> Result 
      accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
}

//end of CEASTlocalBlock.java
