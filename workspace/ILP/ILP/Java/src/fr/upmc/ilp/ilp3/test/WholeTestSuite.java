/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: WholeTestSuite.java 1238 2012-09-12 15:31:08Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp3.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** Regroupement de classes de tests pour le paquetage ilp3. */

@RunWith(value=Suite.class)
@SuiteClasses(value={
        // Tous les fichiers de tests un par un:
        fr.upmc.ilp.ilp3.test.ProcessTest.class
})
public class WholeTestSuite {}

// end of WholeTestSuite.java
