/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id:TestsSuite.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp1.test;

import org.hansel.CoverageRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/** La mode est aux annotations!
 * La classe n'a plus de corps seulement des annotations indiquant
 * comment construire une instance et agir.
 * Cette suite de tests realise egalement des tests de couverture
 * a l'aide d'Hansel @see{http://hansel.sourceforge.net/}.
 *
 * Attention, pour tourner ce test, il faut passer les options
 *  <code>-javaagent:Java/jars/hansel.jar</code> a la JVM sous-jacente.
 */

@RunWith(CoverageRunner.class)
@Suite.SuiteClasses({
    fr.upmc.ilp.ilp1.eval.test.EASTFileTest.class,
    fr.upmc.ilp.ilp1.cgen.test.CgeneratorTest.class
})
@CoverageRunner.CoverClasses({
    fr.upmc.ilp.ilp1.eval.EAST.class,
    fr.upmc.ilp.ilp1.eval.EASTalternative.class,
    fr.upmc.ilp.ilp1.eval.EASTunaryBlock.class,
    fr.upmc.ilp.ilp1.eval.EASTboolean.class,
    fr.upmc.ilp.ilp1.eval.EASTstring.class,
    fr.upmc.ilp.ilp1.eval.EASTConstant.class,
    fr.upmc.ilp.ilp1.eval.EASTinteger.class,
    fr.upmc.ilp.ilp1.eval.EASTException.class,
    fr.upmc.ilp.ilp1.eval.EASTFactory.class,
    fr.upmc.ilp.ilp1.eval.EASTfloat.class,
    fr.upmc.ilp.ilp1.eval.EASTinvocation.class,
    fr.upmc.ilp.ilp1.eval.EASTinvocationPrimitive.class,
    fr.upmc.ilp.ilp1.eval.EASToperation.class,
    fr.upmc.ilp.ilp1.eval.EASTbinaryOperation.class,
    fr.upmc.ilp.ilp1.eval.EASTunaryOperation.class,
    fr.upmc.ilp.ilp1.eval.EASTParser.class,
    fr.upmc.ilp.ilp1.eval.EASTsequence.class,
    fr.upmc.ilp.ilp1.eval.EASTvariable.class,
    fr.upmc.ilp.ilp1.eval.EASTFactory.class,
})
public class CoverageTest {}

//end of CoverageTest.java
