/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: ToolTestSuite.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.tool.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hansel.CoverageDecorator;

import fr.upmc.ilp.tool.FileTool;

/** Regroupement de classes de tests pour le paquetage fr.upmc.ilp.tool. 
 * Si tourne directement a partir d'Eclipse, le repertoire doit etre
 * Java/src/ 
 */

public final class ToolTestSuite extends TestSuite {

    public static FileTool ft;
    
        /**
         * Tester les classes de test. Realiser
         * egalement une analyse du taux de couverture des tests
         *
         * @return Test - la suite de tests a effectuer
         */
    
    public static Test suite () {
        final TestSuite suite = new TestSuite();
        suite.addTest(
                new CoverageDecorator(
                        FileToolTest.class,
                        new Class[] {fr.upmc.ilp.tool.FileTool.class}));
        suite.addTest(
                new CoverageDecorator(
                        CStuffTest.class,
                        new Class[] {fr.upmc.ilp.tool.CStuff.class}));
        suite.addTestSuite(ProgramCallerTest.class);
        return suite;
    }
    
}

//end of ToolTestSuite.java
