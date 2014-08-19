package fr.upmc.ilp.ilp4.jsgen.test;

import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;

import fr.upmc.ilp.ilp1.test.AbstractProcessTest;
import fr.upmc.ilp.ilp4.jsgen.Process;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.Parameterized;
import fr.upmc.ilp.tool.Parameterized.Parameters;

@RunWith(value=Parameterized.class)
public class ProcessTest 
extends fr.upmc.ilp.ilp4.test.ProcessTest {

    public ProcessTest(final File file) {
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
        // Sauter u13 (car pas d'entier en js)
        // sauter u645 (pas une erreur en js)
        AbstractProcessTest.staticSetUp(samplesDir, "u(0\\d+"
          + "|1[^3]\\d*"
          + "|[2-5]\\d+"
          + "|6[^4]\\d*|64[^5]\\d*"
          + "|[7-9]\\d+)-[1-4]");
        // Pour un (ou plusieurs) test(s) en particulier:
        //AbstractProcessTest.staticSetUp("u52-2");
        return AbstractProcessTest.collectData();
    }
}
