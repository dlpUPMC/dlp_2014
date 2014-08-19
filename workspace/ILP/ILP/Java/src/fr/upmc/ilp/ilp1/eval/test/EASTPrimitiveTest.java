/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:EASTPrimitiveTest.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval.test;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import fr.upmc.ilp.ilp1.eval.EAST;
import fr.upmc.ilp.ilp1.eval.EASTException;
import fr.upmc.ilp.ilp1.eval.EASTboolean;
import fr.upmc.ilp.ilp1.eval.EASTfloat;
import fr.upmc.ilp.ilp1.eval.EASTinteger;
import fr.upmc.ilp.ilp1.eval.EASTinvocationPrimitive;
import fr.upmc.ilp.ilp1.eval.EASTstring;
import fr.upmc.ilp.ilp1.eval.EASTvariable;
import fr.upmc.ilp.ilp1.runtime.Common;
import fr.upmc.ilp.ilp1.runtime.ConstantsStuff;
import fr.upmc.ilp.ilp1.runtime.EmptyLexicalEnvironment;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;
import fr.upmc.ilp.ilp1.runtime.PrintStuff;

/** Tests (Junit3) d'invocation des primitives (dans PrintStuff). */

public class EASTPrimitiveTest extends TestCase {

    @Override
    public void setUp() {
        common = new Common();
        lexenv = EmptyLexicalEnvironment.create();
        ps = new PrintStuff();
        lexenv = ps.extendWithPrintPrimitives(lexenv);
        cs = new ConstantsStuff();
        lexenv = cs.extendWithPredefinedConstants(lexenv);
    }

    private ILexicalEnvironment lexenv;
    private ICommon common;
    private PrintStuff ps;
    private ConstantsStuff cs;

    public void testPi() throws EASTException, EvaluationException {
        EASTvariable var = new EASTvariable("pi");
        Object result = var.eval(lexenv, common);
        assertTrue(result instanceof Double);
        assertEquals((Double) result, 3.14, 0.1);
    }

    public void testNewline() throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        EAST o = new EASTinvocationPrimitive("newline", args);
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof Boolean);
        assertEquals(result, Boolean.FALSE);
        assertEquals("\n", ps.getPrintedOutput());
    }

    public void testPrintEntier() throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        args.add(new EASTinteger("34"));
        EAST o = new EASTinvocationPrimitive("print", args);
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof Boolean);
        assertEquals(result, Boolean.FALSE);
        assertEquals("34", ps.getPrintedOutput());
    }

    public void testPrintFlottant() throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        args.add(new EASTfloat("3.14"));
        EAST o = new EASTinvocationPrimitive("print", args);
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof Boolean);
        assertEquals(result, Boolean.FALSE);
        assertEquals("3.14", ps.getPrintedOutput());
    }

    public void testPrintString() throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        args.add(new EASTstring("foobar"));
        EAST o = new EASTinvocationPrimitive("print", args);
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof Boolean);
        assertEquals(result, Boolean.FALSE);
        assertEquals("foobar", ps.getPrintedOutput());
    }

    public void testPrintBoolTrue() throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        args.add(new EASTboolean("true"));
        EAST o = new EASTinvocationPrimitive("print", args);
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof Boolean);
        assertEquals(result, Boolean.FALSE);
        assertEquals("true", ps.getPrintedOutput());
    }

    public void testPrintBoolFalse() throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        args.add(new EASTboolean("false"));
        EAST o = new EASTinvocationPrimitive("print", args);
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof Boolean);
        assertEquals(result, Boolean.FALSE);
        assertEquals("false", ps.getPrintedOutput());
    }
}

// end of EASTPrimitiveTest.java
