/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ProcessTest.java 1281 2013-01-04 18:26:59Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.test;

import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;

import fr.upmc.ilp.ilp1.test.AbstractProcessTest;
import fr.upmc.ilp.ilp4.Process;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.Parameterized;
import fr.upmc.ilp.tool.Parameterized.Parameters;

@RunWith(value=Parameterized.class)
public class ProcessTest extends fr.upmc.ilp.ilp3.test.ProcessTest {

    /** Le constructeur du test sur un fichier. */

    public ProcessTest (final File file) {
        super(file);
    }

    @Before
    @Override
    public void setUp () throws IOException {
        this.setProcess(new Process(finder));
        getProcess().setFinder(finder);
    }

    @Parameters
    public static Collection<File[]> data() throws Exception {
        initializeFromOptions();
        AbstractProcessTest.staticSetUp(samplesDir, "u\\d+-[1-4]");
        // Pour un (ou plusieurs) test(s) en particulier:
        //AbstractProcessTest.staticSetUp(samplesDir, "u59-2");
        return AbstractProcessTest.collectData();
    }
}
