/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id:ProcessTest.java 505 2006-10-11 06:58:35Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.test;

import java.io.IOException;

import org.junit.Before;

import fr.upmc.ilp.ilp1.Process;
import fr.upmc.ilp.tool.File;

/** Cette classe de test utilise JUnit4 ainsi qu'une extension permettant de
 * donner (fr.upmc.ilp.tool.Parameterized) de jolis noms aux divers tests.
 * 
 * NOTA: l'annotation RunWith sera héritée par les sous-classes.
 */

public class ProcessTest extends AbstractMainForProcessTest {

    /** Le constructeur du test sur un fichier à tester. */

    public ProcessTest (final File file) {
        super(file);
    }

    /** Cette méthode initialise le Process à tester avec des valeurs par
     * défaut. Comme cette méthode est appelée après le constructeur, elle
     * ne doit pas modifier les valeurs définies par le constructeur (notamment
     * la grammaire ou le patron). */
    @Before
    public void setUp () throws IOException {
        this.setProcess(new Process(finder));
        getProcess().setVerbose(options.verbose);
    }

    // La méthode effective de test est héritée d'AbstractProcessTest
}
