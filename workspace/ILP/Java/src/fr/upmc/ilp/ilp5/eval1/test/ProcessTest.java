/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ProcessTest.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp5.eval1.test;

import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;

import fr.upmc.ilp.ilp1.test.AbstractProcessTest;
import fr.upmc.ilp.ilp5.eval1.Process;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.Parameterized;
import fr.upmc.ilp.tool.Parameterized.Parameters;

@RunWith(value=Parameterized.class)
public class ProcessTest
extends fr.upmc.ilp.ilp5.test.ProcessTest {

    /** Le constructeur du test sur un fichier. */

    public ProcessTest (final File file) {
        super(file);
    }

    @Before
    @Override
    public void setUp () throws IOException {
        this.setProcess(new Process(finder));
    }

    @Parameters
    public static Collection<File[]> data() throws Exception {
        initializeFromOptions();
        AbstractProcessTest.staticSetUp(samplesDir, "ur?\\d+-[1-5]");
        // Pour un (ou plusieurs) test(s) en particulier:
        //AbstractProcessTest.staticSetUp("ur10-5");
        return AbstractProcessTest.collectData();
    }
}
