/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: NormalizeTest.java 1241 2012-09-12 17:14:26Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.test;

import java.util.Set;

import junit.framework.TestCase;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp4.ast.CEASTFactory;
import fr.upmc.ilp.ilp4.ast.CEASTexpression;
import fr.upmc.ilp.ilp4.ast.CEASTfloat;
import fr.upmc.ilp.ilp4.ast.CEASTfunctionDefinition;
import fr.upmc.ilp.ilp4.ast.CEASTglobalFunctionVariable;
import fr.upmc.ilp.ilp4.ast.CEASTglobalInvocation;
import fr.upmc.ilp.ilp4.ast.CEASTglobalVariable;
import fr.upmc.ilp.ilp4.ast.CEASTinteger;
import fr.upmc.ilp.ilp4.ast.CEASTinvocation;
import fr.upmc.ilp.ilp4.ast.CEASTlocalVariable;
import fr.upmc.ilp.ilp4.ast.CEASTprimitiveInvocation;
import fr.upmc.ilp.ilp4.ast.CEASTprogram;
import fr.upmc.ilp.ilp4.ast.CEASTreference;
import fr.upmc.ilp.ilp4.ast.CEASTsequence;
import fr.upmc.ilp.ilp4.ast.CEASTvariable;
import fr.upmc.ilp.ilp4.ast.FindingInvokedFunctionsException;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.ast.NormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.ast.NormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

public class NormalizeTest extends TestCase {

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

    // {{{ Tests de normalisation

    // non simplification d'une sequence a 2 instructions (encore que!)
    public void testNormalizeSequence2 ()
      throws NormalizeException {
      CEASTsequence ceast =
        new CEASTsequence(
           new CEASTexpression[] {
             new CEASTinteger("1"),
             new CEASTinteger("2") });
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(normlexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTsequence);
    }

    // Simplification d'une sequence mono-instruction:
    public void testNormalizeSequence1 ()
      throws NormalizeException {
      CEASTsequence ceast =
        new CEASTsequence(
           new CEASTexpression[] {
             new CEASTinteger("2") });
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(normlexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTinteger);
    }

    // Identite de reference a une meme variable locale:
    public void testNormalizeLocalVariable ()
      throws NormalizeException {
      CEASTlocalVariable foo1 = new CEASTlocalVariable("foo");
      CEASTlocalVariable foo2 = new CEASTlocalVariable("foo");
      CEASTsequence ceast =
        new CEASTsequence(
             new CEASTexpression[] {
               new CEASTreference(foo1),
               new CEASTreference(foo2) });
      INormalizeLexicalEnvironment lexenv =  normlexenv.extend(foo1);
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(lexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTsequence);
      CEASTsequence s = (CEASTsequence) nceast;
      // les 2 references sont unifiees
      CEASTreference ref1 = (CEASTreference) s.getInstructions()[0];
      CEASTreference ref2 = (CEASTreference) s.getInstructions()[1];
      assertSame(ref1.getVariable(), ref2.getVariable());
      // a celle introduite dans l'environnement
      assertSame(foo1, ref1.getVariable());
    }

    // identite de reference a une meme variable globale utilisateur:
    public void testNormalizeGlobalVariable ()
      throws NormalizeException {
      CEASTglobalVariable foo1 = new CEASTglobalVariable("foo");
      CEASTglobalVariable foo2 = new CEASTglobalVariable("foo");
      CEASTsequence ceast =
        new CEASTsequence(
             new CEASTexpression[] {
                     new CEASTreference(foo1),
                     new CEASTreference(foo2) });
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(normlexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTsequence);
      CEASTsequence s = (CEASTsequence) nceast;
      CEASTreference ref1 = (CEASTreference) s.getInstructions()[0];
      CEASTreference ref2 = (CEASTreference) s.getInstructions()[1];
      // les references sont unifiees
      assertSame(ref1.getVariable(), ref2.getVariable());
      // et ce doivent etre des references a des variables globales
      assertTrue(ref1.getVariable() instanceof CEASTglobalVariable);
      // et foo a ete ajoute a l'environnement
      assertTrue(normcommon.isPresent(new CEASTglobalVariable("foo"))
                 instanceof CEASTglobalVariable );
      assertSame(ref1.getVariable(),
                 normcommon.isPresent(new CEASTglobalVariable("foo")) );
      assertSame(ref1.getVariable(),
                 normcommon.isPresent(new CEASTvariable("foo")) );
    }

    // Identite de reference a une meme variable globale. Ici les
    // references initiales ne mentionnent que la variable sans savoir
    // qu'elle est locale.
    public void testNormalizeVariable ()
      throws NormalizeException {
      CEASTglobalVariable foo = new CEASTglobalVariable("foo");
      CEASTsequence ceast =
        new CEASTsequence(
             new CEASTexpression[] {
                     new CEASTreference(new CEASTvariable("foo")),
                     new CEASTreference(new CEASTvariable("foo")) });
      INormalizeLexicalEnvironment lexenv =  normlexenv.extend(foo);
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(lexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTsequence);
      CEASTsequence s = (CEASTsequence) nceast;
      CEASTreference ref1 = (CEASTreference) s.getInstructions()[0];
      CEASTreference ref2 = (CEASTreference) s.getInstructions()[1];
      // les 2 references sont unifiees
      assertSame(ref1.getVariable(), ref2.getVariable());
      // a celle introduite dans l'environnement
      assertSame(foo, ref1.getVariable());
      // et ce doivent etre des references a des variables globales
      assertTrue(ref2.getVariable() instanceof CEASTglobalVariable);
    }

    // Identite de reference a une meme variable globale. Ici les
    // references initiales ne mentionnent que la variable sans savoir
    // qu'elle est locale. Et, en plus, on n'introduit pas la variable
    // globale dans l'environnement.
    public void testNormalizeVariable2 ()
      throws NormalizeException {
      CEASTsequence ceast =
        new CEASTsequence(
             new CEASTexpression[] {
                     new CEASTreference(new CEASTvariable("foo")),
                     new CEASTreference(new CEASTvariable("foo")) });
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(normlexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTsequence);
      CEASTsequence s = (CEASTsequence) nceast;
      CEASTreference ref1 = (CEASTreference) s.getInstructions()[0];
      CEASTreference ref2 = (CEASTreference) s.getInstructions()[1];
      // les 2 references sont unifiees
      assertSame(ref1.getVariable(), ref2.getVariable());
      // et ce doivent etre des references a des variables globales
      assertTrue(ref2.getVariable() instanceof CEASTglobalVariable);
      // et foo a ete ajoute a l'environnement
      assertTrue(normcommon.isPresent(new CEASTglobalVariable("foo"))
                 instanceof CEASTglobalVariable );
      assertSame(ref1.getVariable(),
                 normcommon.isPresent(new CEASTglobalVariable("foo")) );
      assertSame(ref2.getVariable(),
                 normcommon.isPresent(new CEASTvariable("foo")) );
    }

    // identite de reference a une meme primitive (nouvelle):
    public void testNormalizeglobalVariable ()
      throws NormalizeException {
      CEASTglobalVariable foo = new CEASTglobalVariable("foo");
      CEASTsequence ceast =
        new CEASTsequence(
             new CEASTexpression[] {
               new CEASTreference(foo)
               });
      normcommon.addPrimitive(foo);
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(normlexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTreference);
      IAST4variable var = ((CEASTreference)nceast).getVariable();
      assertTrue(var instanceof CEASTglobalVariable);
      assertSame(foo, var);
    }

    // identite de reference a une meme primitive (nouvelle):
    public void testNormalizeglobalVariable2 ()
      throws NormalizeException {
      CEASTglobalVariable foo = new CEASTglobalVariable("foo");
      CEASTsequence ceast =
        new CEASTsequence(
             new CEASTexpression[] {
               new CEASTreference(foo),
               new CEASTreference(foo),
               });
      normcommon.addPrimitive(foo);
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(normlexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTsequence);
      CEASTsequence s = (CEASTsequence) nceast;
      CEASTreference ref1 = (CEASTreference)s.getInstructions()[0];
      IAST4variable var1 = ref1.getVariable();
      CEASTreference ref2 = (CEASTreference)s.getInstructions()[1];
      IAST4variable var2 = ref2.getVariable();
      assertTrue(var1 instanceof CEASTglobalVariable);
      assertTrue(var2 instanceof CEASTglobalVariable);
      assertSame(var1, var2);
      assertSame(foo, var1);
    }
    public void testNormalizeglobalVariablePrint ()
      throws NormalizeException {
      CEASTglobalVariable foo = new CEASTglobalVariable("print");
      CEASTexpression ceast =
        new CEASTsequence(
             new CEASTexpression[] {
                  new CEASTreference(foo) });
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(normlexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTreference);
      IAST4variable var = ((CEASTreference)nceast).getVariable();
      assertTrue(var instanceof CEASTglobalVariable);
      assertEquals("print", var.getName());
      //assertNotSame(foo, var);
    }

    public void testNormalizeglobalVariable2Print ()
      throws NormalizeException {
      CEASTglobalVariable foo = new CEASTglobalVariable("print");
      CEASTexpression ceast =
        new CEASTsequence(
             new CEASTexpression[] {
                     new CEASTreference(foo),
                     new CEASTreference(foo) });
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(normlexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTsequence);
      CEASTsequence s = (CEASTsequence) nceast;
      CEASTreference ref1 = (CEASTreference)s.getInstructions()[0];
      IAST4variable var1 = ref1.getVariable();
      CEASTreference ref2 = (CEASTreference)s.getInstructions()[1];
      IAST4variable var2 = ref2.getVariable();
      assertEquals("print", var1.getName());
      assertEquals("print", var2.getName());
      assertSame(var1, var2);
      //assertNotSame(foo, var1);
    }

    // verification que l'unification se fait aussi en position d'invocation
    public void testNormalizeglobalVariablePrintInInvokation ()
      throws NormalizeException {
      CEASTglobalVariable foo = new CEASTglobalVariable("print");
      CEASTexpression ceast =
        new CEASTprimitiveInvocation(foo, new CEASTexpression[0]);
      IAST4Factory factory = new CEASTFactory();
      IAST4 nceast = ceast.normalize(normlexenv, normcommon, factory);
      assertTrue(nceast instanceof CEASTprimitiveInvocation);
      CEASTprimitiveInvocation pi = (CEASTprimitiveInvocation) nceast;
      assertEquals("print", pi.getFunctionGlobalVariable().getName());
      // getFunction() ne doit pas etre invoquee sur les primitiveInvocations!
      //assertNotSame(foo, pi.getFunction());
    }

    // Identite de reference a des fonctions globales:
    public void testNormalize1globalFunctionVariables1 ()
    throws NormalizeException {
      // fonction unaire
      final CEASTfunctionDefinition function_definition =
          new CEASTfunctionDefinition(
                  new CEASTglobalFunctionVariable("foobar"),
                  new IAST4variable[] {
                          new CEASTlocalVariable("x"),
                  },
                  new CEASTinteger("1") );
      // Le constructeur CEASTfunctionDefinition assure le bouclage:
      assertSame(function_definition,
                 function_definition.getDefinedVariable()
                    .getFunctionDefinition() );
      IAST4Factory factory = new CEASTFactory();
      IAST4functionDefinition fd =
          function_definition.normalize(normlexenv, normcommon, factory);
      assertSame(fd, fd.getDefinedVariable().getFunctionDefinition());
    }

    // normalisation d'une fonction (avant d'attaquer la normalisation
    // d'un programme).
    public void testFunction1 ()
    throws NormalizeException {
        // fonction zero-aire
        final CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                    new CEASTglobalFunctionVariable("foobar"),  // <--------
                    new IAST4variable[0],
                    new CEASTinteger("1") );
        // Le constructeur CEASTfunctionDefinition assure le bouclage:
        assertSame(function_definition,
                   function_definition.getDefinedVariable()
                      .getFunctionDefinition() );
        IAST4Factory factory = new CEASTFactory();
        IAST4functionDefinition fd =
            function_definition.normalize(normlexenv, normcommon, factory);
        assertTrue(fd instanceof CEASTfunctionDefinition);
        assertTrue(fd.getDefinedVariable()
                   instanceof CEASTglobalFunctionVariable);
        // le bouclage est toujours present:
        assertSame(fd, fd.getDefinedVariable().getFunctionDefinition());
        // la globale a ete placee dans common:
        assertSame(fd.getDefinedVariable(),
                   normcommon.isPresent(new CEASTvariable("foobar")) );
    }

    @SuppressWarnings("null")
    public void testNormalize2globalFunctionVariables ()
    throws NormalizeException {
        // fonction unaire
        final CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                    new CEASTglobalFunctionVariable("foobar"),  // <--------
                    new IAST4variable[] {
                            new CEASTlocalVariable("x"),
                    },
                    new CEASTinteger("1") );
        // Le constructeur CEASTfunctionDefinition assure le bouclage:
        assertSame(function_definition,
                   function_definition.getDefinedVariable()
                      .getFunctionDefinition() );
        // invocation unaire
        CEASTinvocation invocation =
            new CEASTinvocation(
                    new CEASTreference(new CEASTvariable("foobar")),  // <--------
                    new IAST4expression[] {
                        new CEASTinteger("2"),
                    } );
        // construire un programme entier
        CEASTprogram program = new CEASTprogram(
                new IAST4functionDefinition[] {
                        function_definition,
                },
                invocation );
        IAST4Factory factory = new CEASTFactory();
        IAST4 nceast = program.normalize(normlexenv, normcommon, factory);
        assertTrue(nceast instanceof CEASTprogram);
        CEASTprogram prog = (CEASTprogram) nceast;
        IAST4globalFunctionVariable gfv = null;
        CEASTglobalInvocation ninvocation = null;
        // retrouver la fonction foobar et la variable globale associee:
        for ( IAST4functionDefinition fd : prog.getFunctionDefinitions() ) {
            if ( "foobar".equals(fd.getFunctionName()) ) {
                gfv = fd.getDefinedVariable();
            } else {
                // retrouver l'invocation:
                assertNotNull(fd.getBody());
                ninvocation = (CEASTglobalInvocation) fd.getBody();
            }
        }
        assertNotNull(gfv);
        assertTrue(gfv instanceof CEASTglobalFunctionVariable);
        assertNotNull(ninvocation);
        assertSame(gfv, ninvocation.getFunctionGlobalVariable());
    }

    // les primitives (print) ne figurent pas dans les fonctions invoquees:
    public void testFindInvokedFunctionsOnPrimitive ()
      throws NormalizeException, FindingInvokedFunctionsException {
      CEASTprimitiveInvocation pi =
        new CEASTprimitiveInvocation(
                new CEASTglobalVariable("print"),
                new IAST4expression[] {
                    new CEASTinteger("2"),
                } );
      IAST4Factory factory = new CEASTFactory();
      IAST4 ceast = pi.normalize(normlexenv, normcommon, factory);
      ceast.computeInvokedFunctions();
      assertEquals(0, ceast.getInvokedFunctions().size());
    }

    // tester findInvokedFunctions. les primitives ne comptent pas!
    public void testFindInvokedFunctions1 ()
    throws NormalizeException, FindingInvokedFunctionsException {
      // invocation zero-aire
      CEASTinvocation invocation =
          new CEASTprimitiveInvocation(
                  new CEASTglobalVariable("newline"),
                  new IAST4expression[0] );
      IAST4Factory factory = new CEASTFactory();
      IAST4 ceast = invocation.normalize(normlexenv, normcommon, factory);
      ceast.computeInvokedFunctions();
      Set<IAST4globalFunctionVariable> gvs = ceast.getInvokedFunctions();
      assertEquals(0, gvs.size());
    }

    public void testFindInvokedFunctions2 ()
      throws NormalizeException, FindingInvokedFunctionsException {
        // fonction zero-aire
        final CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                    new CEASTglobalFunctionVariable("foobar"),
                    new IAST4variable[0],
                    new CEASTinteger("1") );
        // invocation zero-aire (checkArity est actif!)
        CEASTinvocation invocation =
            new CEASTinvocation(
                    new CEASTreference(function_definition.getDefinedVariable()),
                    new IAST4expression[0] );
        IAST4Factory factory = new CEASTFactory();
        IAST4 ceast = invocation.normalize(normlexenv, normcommon, factory);
        ceast.computeInvokedFunctions();
        Set<IAST4globalFunctionVariable> gvs = ceast.getInvokedFunctions();
        assertEquals(1, gvs.size());
        IAST4globalFunctionVariable[] gv =
                gvs.toArray(new IAST4globalFunctionVariable[0]);
        assertEquals("foobar", gv[0].getName());
    }

    // Premiere normalisation d'un programme entier
    public void testNormalizeProgram1 ()
    throws NormalizeException, FindingInvokedFunctionsException {
      // fonction zero-aire
      final IAST4expression expr =
          new CEASTinteger("1");
      // construire un programme entier
      CEASTprogram program = new CEASTprogram(
              new IAST4functionDefinition[0],
              expr );
      IAST4Factory factory = new CEASTFactory();
      IAST4 ceast = program.normalize(normlexenv, normcommon, factory);
      ceast.computeInvokedFunctions();
    }

    // Premiere normalisation d'un programme entier
    public void testNormalizeProgram2 ()
    throws NormalizeException, FindingInvokedFunctionsException {
      // fonction zero-aire
      final IAST4expression expr =
          new CEASTsequence(
                  new CEASTexpression[] {
                          new CEASTinteger("1"),
                  });
      // construire un programme entier
      CEASTprogram program = new CEASTprogram(
              new IAST4functionDefinition[0],
              expr );
      IAST4Factory factory = new CEASTFactory();
      IAST4 ceast = program.normalize(normlexenv, normcommon, factory);
      ceast.computeInvokedFunctions();
    }

    // normalisation d'un programme invoquant une fonction
    public void testFindInvokedFunctions3 ()
      throws NormalizeException, FindingInvokedFunctionsException {
        // fonction zero-aire
        final CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                    new CEASTglobalFunctionVariable("foobar"),
                    new IAST4variable[0],
                    new CEASTinteger("1") );
        // invocation zero-aire
        CEASTinvocation invocation =
            new CEASTinvocation(
                    new CEASTreference(function_definition.getDefinedVariable()),
                    new IAST4expression[0] );
        // construire un programme entier
        CEASTprogram program = new CEASTprogram(
                new IAST4functionDefinition[] {
                        function_definition,
                },
                invocation );

        IAST4Factory factory = new CEASTFactory();
        IAST4 ceast = program.normalize(normlexenv, normcommon, factory);
        ceast.computeInvokedFunctions();
        Set<IAST4globalFunctionVariable> gvs = ceast.getInvokedFunctions();
        assertEquals(1, gvs.size());
        Object[] objs = gvs.toArray();
        CEASTglobalVariable gv = (CEASTglobalVariable) objs[0];
        // Le corps du programme est reduit a l'invocation d'une fonction
        // globale ilpFUNC() qui elle-meme invoque foobar().
        assertTrue(gv.getName().equals("ilp_program"));
    }

    // Ces tests se concentrent sur la normalisation des invocations a des
    // fonctions globales.

    public void testCorrectArityZero() throws NormalizeException {
        // fonction nil-adique
        final CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                new CEASTglobalFunctionVariable("foobar"),
                new IAST4localVariable[]{},
                new CEASTinteger("1") );
        function_definition.getDefinedVariable()
            .setFunctionDefinition(function_definition);
        // invocation sans argument
        CEASTinvocation invocation =
            new CEASTinvocation(
                new CEASTreference(function_definition.getDefinedVariable()),
                new IAST4expression[]{} );
        CEASTprogram program = new CEASTprogram(
                new IAST4functionDefinition[] {
                        function_definition,
                },
                invocation );
        IAST4Factory factory = new CEASTFactory();
        IAST4program new_program = program.normalize(normlexenv, normcommon, factory);
        assertEquals(1+program.getFunctionDefinitions().length,
                     new_program.getFunctionDefinitions().length );
        // L'invocation generique a ete convertie en une invocation a une
        // fonction globale:
        assertSame(new_program.getBody().getClass(),
                   CEASTglobalInvocation.class);
    }

    public void testIncorrectArityZero() throws NormalizeException {
        // fonction unaire
        CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                new CEASTglobalFunctionVariable("foobar"),
                new IAST4variable[] {
                        new CEASTlocalVariable("x"),
                },
                new CEASTinteger("1") );
        function_definition.getDefinedVariable()
            .setFunctionDefinition(function_definition);
        // invocation sans argument
        CEASTinvocation invocation =
            new CEASTinvocation(
                new CEASTreference(function_definition.getDefinedVariable()),
                new IAST4expression[]{} );
        CEASTprogram program = new CEASTprogram(
                new IAST4functionDefinition[] {
                        function_definition,
                },
                invocation );
        try {
            IAST4Factory factory = new CEASTFactory();
            program.normalize(normlexenv, normcommon, factory);
            fail();
        } catch (Exception ne) {
            assertTrue(ne instanceof NormalizeException);
        }
    }

    public void testCorrectArityOne () throws NormalizeException {
        // fonction unaire
        CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                    new CEASTglobalFunctionVariable("foobar"),
                    new IAST4variable[] {
                            new CEASTlocalVariable("x"),
                    },
                    new CEASTinteger("1") );
        function_definition.getDefinedVariable()
            .setFunctionDefinition(function_definition);
        // invocation unaire
        CEASTinvocation invocation =
            new CEASTinvocation(
                    new CEASTreference(function_definition.getDefinedVariable()),
                    new IAST4expression[] {
                        new CEASTfloat("3.14"),
                    } );
        CEASTprogram program = new CEASTprogram(
                new IAST4functionDefinition[] {
                        function_definition,
                },
                invocation );
        IAST4Factory factory = new CEASTFactory();
        IAST4program new_program = program.normalize(normlexenv, normcommon, factory);
        // L'invocation generique a ete convertie en une invocation a une
        // fonction globale:
        assertSame(new_program.getBody().getClass(),
                   CEASTglobalInvocation.class);

    }

    public void testIncorrectArityOne () throws NormalizeException {
        // fonction unaire
        CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                    new CEASTglobalFunctionVariable("foobar"),
                    new IAST4variable[] {
                            new CEASTlocalVariable("x"),
                    },
                    new CEASTinteger("1") );
        function_definition.getDefinedVariable()
            .setFunctionDefinition(function_definition);
        // invocation binaire
        CEASTinvocation invocation =
            new CEASTinvocation(
                    new CEASTreference(function_definition.getDefinedVariable()),
                    new IAST4expression[] {
                        new CEASTfloat("3.14"),
                        new CEASTfloat("2.78"),
                    } );
        CEASTprogram program = new CEASTprogram(
                new IAST4functionDefinition[] {
                        function_definition,
                },
                invocation );
        try {
            IAST4Factory factory = new CEASTFactory();
            program.normalize(normlexenv, normcommon, factory);
            fail();
        } catch (Exception ne) {
            assertTrue(ne instanceof NormalizeException);
        }
    }

}
