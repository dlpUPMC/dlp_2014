/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id:EASTTest.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval.test;

import java.math.BigInteger;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import fr.upmc.ilp.ilp1.eval.EAST;
import fr.upmc.ilp.ilp1.eval.EASTException;
import fr.upmc.ilp.ilp1.eval.EASTalternative;
import fr.upmc.ilp.ilp1.eval.EASTbinaryOperation;
import fr.upmc.ilp.ilp1.eval.EASTboolean;
import fr.upmc.ilp.ilp1.eval.EASTfloat;
import fr.upmc.ilp.ilp1.eval.EASTinteger;
import fr.upmc.ilp.ilp1.eval.EASTsequence;
import fr.upmc.ilp.ilp1.eval.EASTstring;
import fr.upmc.ilp.ilp1.eval.EASTunaryBlock;
import fr.upmc.ilp.ilp1.eval.EASTunaryOperation;
import fr.upmc.ilp.ilp1.eval.EASTvariable;
import fr.upmc.ilp.ilp1.runtime.Common;
import fr.upmc.ilp.ilp1.runtime.CommonPlus;
import fr.upmc.ilp.ilp1.runtime.EmptyLexicalEnvironment;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

/** Tests (JUnit3) d'évaluation sans utiliser de fabrique. */

public class EASTTest extends TestCase {

    @Override
    public void setUp () {
        lexenv = EmptyLexicalEnvironment.create();
        common = new Common();
    }

    private ILexicalEnvironment lexenv;

    private ICommon common;

    public void testEntier () throws EASTException, EvaluationException {
        EAST o = new EASTinteger("34");
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof BigInteger);
        assertEquals(result, new BigInteger("34"));
    }

    public void testFlottant () throws EASTException, EvaluationException {
        EAST o = new EASTfloat("3.14");
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof Double);
        assertEquals(result, new Double("3.14"));
    }

    public void testChaine () throws EASTException, EvaluationException {
        EAST o = new EASTstring("foobar");
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof String);
        assertEquals(result, "foobar");
    }

    public void testBooleenVrai () throws EASTException, EvaluationException {
        EAST o = new EASTboolean("true");
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof Boolean);
        assertEquals(result, Boolean.TRUE);
    }

    public void testBooleenFaux () throws EASTException, EvaluationException {
        EAST o = new EASTboolean("false");
        Object result = o.eval(lexenv, common);
        assertTrue(result instanceof Boolean);
        assertEquals(result, Boolean.FALSE);
    }

    public void testAlternativeVraie () throws EASTException,
            EvaluationException {
        EAST o = new EASTalternative(new EASTboolean("true"), new EASTinteger(
                "1"), new EASTinteger("0"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("1"));
    }

    public void testAlternativeFausse () throws EASTException,
            EvaluationException {
        EAST o = new EASTalternative(new EASTboolean("false"), new EASTinteger(
                "1"), new EASTinteger("0"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("0"));
    }

    public void testSequence1 () throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        args.add(new EASTinteger("0"));
        EAST o = new EASTsequence(args);
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("0"));
    }

    public void testSequence2 () throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        args.add(new EASTinteger("1"));
        args.add(new EASTinteger("0"));
        EAST o = new EASTsequence(args);
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("0"));
    }

    public void testSequence3 () throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        args.add(new EASTinteger("1"));
        args.add(new EASTinteger("0"));
        args.add(new EASTboolean("true"));
        EAST o = new EASTsequence(args);
        Object result = o.eval(lexenv, common);
        assertEquals(result, Boolean.TRUE);
    }

    public void testSequence0 () throws EASTException, EvaluationException {
        List<EAST> args = new Vector<>();
        EAST o = new EASTsequence(args);
        Object result = o.eval(lexenv, common);
        assertNotNull(result);
    }

    public void testVariable1 () throws EASTException, EvaluationException {
        lexenv = lexenv.extend(new EASTvariable("x"), new BigInteger("23"));
        EAST o = new EASTvariable("x");
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("23"));
    }

    public void testVariable2 () throws EASTException, EvaluationException {
        lexenv = lexenv.extend(new EASTvariable("x"), new BigInteger("23"));
        lexenv = lexenv.extend(new EASTvariable("y"), new BigInteger("25"));
        EAST o = new EASTvariable("x");
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("23"));
    }

    public void testVariable0 () throws EASTException, EvaluationException {
        lexenv = lexenv.extend(new EASTvariable("x"), new BigInteger("23"));
        lexenv = lexenv.extend(new EASTvariable("y"), new BigInteger("25"));
        EAST o = new EASTvariable("z");
        try {
            o.eval(lexenv, common);
        } catch (EvaluationException exc) {
            assertTrue(true);
        }
    }

    public void testBlocUnaireVarless () throws EASTException,
            EvaluationException {
        lexenv = lexenv.extend(new EASTvariable("x"), new BigInteger("23"));
        lexenv = lexenv.extend(new EASTvariable("y"), new BigInteger("25"));
        List<EAST> instructions = new Vector<>();
        instructions.add(new EASTinteger("1"));
        EAST o = new EASTunaryBlock(new EASTvariable("z"),
                new EASTinteger("345"), new EASTsequence(instructions));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("1"));
    }

    public void testBlocUnaireWithVar () throws EASTException,
            EvaluationException {
        lexenv = lexenv.extend(new EASTvariable("x"), new BigInteger("23"));
        lexenv = lexenv.extend(new EASTvariable("y"), new BigInteger("25"));
        List<EAST> instructions = new Vector<>();
        instructions.add(new EASTvariable("z"));
        EAST o = new EASTunaryBlock(new EASTvariable("z"),
                new EASTinteger("345"), new EASTsequence(instructions));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("345"));
    }

    public void testBlocUnaireOuterScope () throws EASTException,
            EvaluationException {
        lexenv = lexenv.extend(new EASTvariable("x"), new BigInteger("23"));
        lexenv = lexenv.extend(new EASTvariable("y"), new BigInteger("25"));
        List<EAST> instructions = new Vector<>();
        instructions.add(new EASTvariable("y"));
        EAST o = new EASTunaryBlock(new EASTvariable("z"),
                new EASTinteger("345"), new EASTsequence(instructions));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("25"));
    }

    public void testOperateurUnaireMoinsI () throws EASTException,
            EvaluationException {
        EAST o = new EASTunaryOperation("-", new EASTinteger("42"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("-42"));
    }

    public void testOperateurUnaireMoinsF () throws EASTException,
            EvaluationException {
        EAST o = new EASTunaryOperation("-", new EASTfloat("4.2"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new Double("-4.2"));
    }

    public void testOperateurUnaireMoins2I () throws EASTException,
            EvaluationException {
        EAST o = new EASTunaryOperation("-", new EASTunaryOperation("-",
                new EASTinteger("42")));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("42"));
    }

    public void testOperateurUnaireMoins2F () throws EASTException,
            EvaluationException {
        EAST o = new EASTunaryOperation("-", new EASTunaryOperation("-",
                new EASTfloat("4.2")));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new Double("4.2"));
    }

    public void testOperateurUnaireNot () throws EASTException,
            EvaluationException {
        EAST o = new EASTunaryOperation("!", new EASTboolean("true"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, Boolean.FALSE);
    }

    // Operateur binaire: addition.

    public void testOperateurBinairePlusII () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("+", new EASTinteger("1"),
                new EASTinteger("2"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("3"));
    }

    public void testOperateurBinairePlusIF () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("+", new EASTinteger("1"),
                new EASTfloat("3.1"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new Double("4.1"));
    }

    public void testOperateurBinairePlusFI () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("+", new EASTfloat("3.1"),
                new EASTinteger("1"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new Double("4.1"));
    }

    public void testOperateurBinairePlusFF () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("+", new EASTfloat("3.1"),
                new EASTfloat("1.1"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new Double("4.2"));
    }

    // Operateur binaire: soustraction

    public void testOperateurBinaireMinusII () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("-", new EASTinteger("11"),
                new EASTinteger("2"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new BigInteger("9"));
    }

    public void testOperateurBinaireMinusIF () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("-", new EASTinteger("10"),
                new EASTfloat("3.1"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new Double("6.9"));
    }

    public void testOperateurBinaireMinusFI () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("-", new EASTfloat("3.1"),
                new EASTinteger("1"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new Double("2.1"));
    }

    public void testOperateurBinaireMinusFF () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("-", new EASTfloat("3.1"),
                new EASTfloat("1.1"));
        Object result = o.eval(lexenv, common);
        assertEquals(result, new Double("2.0"));
    }

    // Les operateurs booleens. Ils ont besoin de CommonPlus.

    public void testOperateurBinaireEtFT () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("&", new EASTboolean("false"),
                new EASTboolean("true"));
        common = new CommonPlus();
        Object result = o.eval(lexenv, common);
        assertEquals(result, Boolean.FALSE);
    }

    public void testOperateurBinaireOuFT () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("|", new EASTboolean("false"),
                new EASTboolean("true"));
        common = new CommonPlus();
        Object result = o.eval(lexenv, common);
        assertEquals(result, Boolean.TRUE);
    }

    public void testOperateurBinaireXorFT () throws EASTException,
            EvaluationException {
        EAST o = new EASTbinaryOperation("^", new EASTboolean("false"),
                new EASTboolean("true"));
        common = new CommonPlus();
        Object result = o.eval(lexenv, common);
        assertEquals(result, Boolean.TRUE);
    }

}

// end of EASTTest.java
