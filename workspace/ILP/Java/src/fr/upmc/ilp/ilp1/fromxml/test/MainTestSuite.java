/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: MainTestSuite.java 1228 2012-08-31 16:48:30Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/** Regroupement de classes de tests pour le paquetage fromxml. */

public class MainTestSuite extends TestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new TestSuite(ASTParserTest.class));
    suite.addTest(new TestSuite(MainTest.class));
    return suite;
  }

}

// end of MainTestSuite.java
