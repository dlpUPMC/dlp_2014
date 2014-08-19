/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:WholeTestSuite.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp5.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** Regroupement de classes de tests pour le paquetage ilp5. */

@RunWith(value=Suite.class)
@SuiteClasses(value={
        // Tous les fichiers de tests un par un:
        fr.upmc.ilp.ilp5.test.ProcessTest.class,
        fr.upmc.ilp.ilp5.eval1.test.ProcessTest.class
})
public class WholeTestSuite {}

// end of WholeTestSuite.java
