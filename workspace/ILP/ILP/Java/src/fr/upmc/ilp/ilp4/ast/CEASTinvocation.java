/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTinvocation.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2invocation;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4invocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4reference;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** C'est une classe technique dont la normalisation mene a
 * l'un des cas particuliers reconnus.
 */

public class CEASTinvocation
extends CEASTdelegableExpression
implements IAST4invocation {

    public CEASTinvocation (final IAST4expression function,
                            final IAST4expression[] argument) {
        this.delegate =
            new fr.upmc.ilp.ilp2.ast.CEASTinvocation(function, argument);
    }
    protected fr.upmc.ilp.ilp2.ast.CEASTinvocation delegate;

    @Override
    public fr.upmc.ilp.ilp2.ast.CEASTinvocation getDelegate () {
        return this.delegate;
    }

    public static IAST2invocation<CEASTparseException> parse (
            final Element e, final IParser parser)
    throws CEASTparseException {
        return fr.upmc.ilp.ilp2.ast.CEASTinvocation.parse(e, parser);
    }

    @ILPexpression(isArray=true)
    public IAST4expression[] getArguments() {
      return CEAST.narrowToIAST4expressionArray(this.delegate.getArguments());
    }
    public IAST4expression getArgument (int i) {
      return CEAST.narrowToIAST4expression(this.delegate.getArgument(i));
    }
    public int getArgumentsLength () {
        return this.delegate.getArgumentsLength();
    }
    @ILPexpression
    public IAST4expression getFunction() {
        return CEAST.narrowToIAST4expression(this.delegate.getFunction());
    }

    /** Normaliser une invocation. Si la fonction invoquée est globale,
     * le résultat de la normalisation sera une instance de
     * CEASTglobalInvocation et la variable nommant la fonction sera une
     * CEASTglobalFunctionVariable. */

    @Override
    public IAST4expression normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
    throws NormalizeException {
        final IAST4expression function_ =
            getFunction().normalize(lexenv, common, factory);
        /* Les arguments seront normalises dans les sous-classes. */
        // Discrimination
        if ( function_ instanceof IAST4reference ) {
            IAST4variable var = ((IAST4reference) function_).getVariable();
            if ( var instanceof IAST4globalFunctionVariable ) {
                final IAST4globalFunctionVariable gv =
                    CEAST.narrowToIAST4globalFunctionVariable(var);
                final IAST4invocation result =
                    factory.newGlobalInvocation(gv, getArguments());
                return result.normalize(lexenv, common, factory);

            } else {
                final IAST4invocation result =
                    factory.newComputedInvocation(function_, getArguments());
                return result.normalize(lexenv, common, factory);
            }
        } else {
            final IAST4invocation result =
                factory.newComputedInvocation(function_, getArguments());
            return result.normalize(lexenv, common, factory);
        }
    }
    // NOTE: pas de super.normalize() dans les sous-classes, ca bouclerait.
    // Utiliser normalizeInvocation() sur fonction ?

    public void checkArity () throws NormalizeException {
        final String msg = "Should not be called!";
        throw new NormalizeException(msg);
    }

    @Override
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common,
                         final IDestination destination )
    throws CgenerationException {
        final String msg = "Should not compile a vanilla invocation!";
        throw new CgenerationException(msg);
    }

    /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
    @Override
    public void findInvokedFunctions () {
        findAndAdjoinToInvokedFunctions(getFunction());
        for ( IAST4expression arg : getArguments() ) {
            findAndAdjoinToInvokedFunctions(arg);
        }
    }

    /* Obsolète de par CEAST.inline() qui use de réflexivité.
    @Override
    public void inline () {
        getFunction().inline();
        for ( IAST4expression arg : getArguments() ) {
            arg.inline();
        }
    }
    */
    
    public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

//end of CEASTinvocation.java
