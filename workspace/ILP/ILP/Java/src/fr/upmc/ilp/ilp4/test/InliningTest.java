/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:InliningTest.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.test;

import java.math.BigInteger;

import junit.framework.TestCase;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp4.ast.CEASTFactory;
import fr.upmc.ilp.ilp4.ast.CEASTfunctionDefinition;
import fr.upmc.ilp.ilp4.ast.CEASTglobalFunctionVariable;
import fr.upmc.ilp.ilp4.ast.CEASTglobalInvocation;
import fr.upmc.ilp.ilp4.ast.CEASTglobalVariable;
import fr.upmc.ilp.ilp4.ast.CEASTinteger;
import fr.upmc.ilp.ilp4.ast.CEASTinvocation;
import fr.upmc.ilp.ilp4.ast.CEASTlocalBlock;
import fr.upmc.ilp.ilp4.ast.CEASTreference;
import fr.upmc.ilp.ilp4.ast.CEASTvariable;
import fr.upmc.ilp.ilp4.ast.FindingInvokedFunctionsException;
import fr.upmc.ilp.ilp4.ast.InliningException;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.ast.NormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.ast.NormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

public class InliningTest extends TestCase {

    @Override
    public void setUp () throws EvaluationException {
        // pour normaliser:
        normlexenv = new NormalizeLexicalEnvironment.EmptyNormalizeLexicalEnvironment();
        normcommon = new NormalizeGlobalEnvironment();
        normcommon.addPrimitive(new CEASTglobalVariable("print"));
        normcommon.addPrimitive(new CEASTglobalVariable("newline"));
        normcommon.addPrimitive(new CEASTglobalVariable("throw"));
    }
    // pour normaliser:
    protected INormalizeLexicalEnvironment normlexenv;
    protected INormalizeGlobalEnvironment normcommon;

    // Integration fonction zero-aire
    public void testInlining0 ()
      throws NormalizeException, InliningException,
             FindingInvokedFunctionsException {
        // fonction zero-aire
        final CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                    new CEASTglobalFunctionVariable("foobar"),
                    new IAST4variable[0],
                    new CEASTinteger("1") );
        // invocation zero-aire (checkArity est actif!)
        final CEASTinvocation invocation =
            new CEASTinvocation(
                    new CEASTreference(function_definition.getDefinedVariable()),
                    new IAST4expression[0] );
        IAST4Factory factory = new CEASTFactory();
        CEASTglobalInvocation ceast = (CEASTglobalInvocation)
            invocation.normalize(normlexenv, normcommon, factory);
        ceast.computeInvokedFunctions();
        ceast.inline(factory);
        assertTrue(ceast.getInlined() instanceof CEASTlocalBlock);
        final CEASTlocalBlock lb = (CEASTlocalBlock) ceast.getInlined();
        final CEASTinteger ci = (CEASTinteger) lb.getBody();
        assertEquals(ci.getValue(), new BigInteger("1"));
    }

    // Integration fonction unaire
    public void testInlining1 ()
      throws NormalizeException, InliningException,
             FindingInvokedFunctionsException {
        // fonction zero-aire
        final CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                    new CEASTglobalFunctionVariable("foobar"),
                    new IAST4variable[] {
                        new CEASTvariable("x"),
                    },
                    new CEASTinteger("1") );
        // invocation zero-aire (checkArity est actif!)
        final CEASTinvocation invocation =
            new CEASTinvocation(
                    new CEASTreference(function_definition.getDefinedVariable()),
                    new IAST4expression[]{
                        new CEASTinteger("2"),
                    });
        IAST4Factory factory = new CEASTFactory();
        CEASTglobalInvocation ceast = (CEASTglobalInvocation)
            invocation.normalize(normlexenv, normcommon, factory);
        ceast.computeInvokedFunctions();
        ceast.inline(factory);
        assertTrue(ceast.getInlined() instanceof CEASTlocalBlock);
        final CEASTlocalBlock lb = (CEASTlocalBlock) ceast.getInlined();
        final CEASTinteger ci = (CEASTinteger) lb.getBody();
        assertEquals(ci.getValue(), new BigInteger("1"));
        assertEquals(1, lb.getVariables().length);
    }

}
