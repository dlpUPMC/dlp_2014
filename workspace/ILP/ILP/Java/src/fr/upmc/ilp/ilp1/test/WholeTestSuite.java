/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2007 <Christian.Queinnec@lip6.fr>
 * $Id:TestsSuite.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp1.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** Regroupement de classes de tests d'evaluation et de compilation. */

@RunWith(value=Suite.class)
@SuiteClasses(value={
        // ajout des tests de fabrication d'AST:
        fr.upmc.ilp.ilp1.fromxml.test.ASTParserTest.class,
        // ajout des tests d'evaluation:
        fr.upmc.ilp.ilp1.eval.test.EASTTest.class,
        fr.upmc.ilp.ilp1.eval.test.EASTPrimitiveTest.class,
        fr.upmc.ilp.ilp1.eval.test.EASTFileTest.class,
        // ajout des tests de compilation:
        fr.upmc.ilp.ilp1.cgen.test.CgeneratorTest.class,
        // Un test de couverture:
        //fr.upmc.ilp.ilp1.CoverageTest.class,
        // Et enfin tous les fichiers de tests un par un:
        fr.upmc.ilp.ilp1.test.ProcessTest.class
})
public class WholeTestSuite {}

//end of WholeTestSuite.java
