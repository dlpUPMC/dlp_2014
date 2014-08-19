/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ProcessTest.java 1240 2012-09-12 15:49:06Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4mem.test;

import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;

import fr.upmc.ilp.ilp1.test.AbstractProcessTest;
import fr.upmc.ilp.ilp4mem.Process;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.Parameterized;
import fr.upmc.ilp.tool.Parameterized.Parameters;

@RunWith(value=Parameterized.class)
public class ProcessTest
extends fr.upmc.ilp.ilp4.test.ProcessTest {

    /** Le constructeur du test sur un fichier. */

    public ProcessTest (final File file) {
        super(file);
    }

    @Before
    @Override
    public void setUp () throws IOException {
        this.setProcess(new Process(finder));
        getProcess().setVerbose(options.verbose);
        doInterpret = false;
    }

    @Parameters
    public static Collection<File[]> data() throws Exception {
        initializeFromOptions();
        AbstractProcessTest.staticSetUp(samplesDir, "u\\d+-[1-4]");
        Collection<File[]> c2 = AbstractProcessTest.collectData();
        // Pour un (ou plusieurs) test(s) en particulier:
        AbstractProcessTest.staticSetUp(samplesDir, "mem-\\d+");
        c2.addAll(AbstractProcessTest.collectData());
        return c2;
    }
}
