package fr.upmc.ilp.ilp3.jsgen.test;

import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;

import fr.upmc.ilp.ilp1.test.AbstractProcessTest;
import fr.upmc.ilp.ilp3.jsgen.Process;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.Parameterized;
import fr.upmc.ilp.tool.Parameterized.Parameters;

@RunWith(value=Parameterized.class)
public class ProcessTest 
extends fr.upmc.ilp.ilp3.test.ProcessTest {

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
     // On ne teste pas u35-1 u36-1 u645-3 et u13-1 car il y a des problemes avec le floats
        AbstractProcessTest.staticSetUp(samplesDir, "u(0\\d+"
                + "|1[^3]\\d*"
                + "|2\\d+"
                + "|[4-5]\\d+"
                //+ "|3[^5]\\d*|3[^6]\\d*"
                + "|6[^4]\\d*|64[^5]\\d*"
                + "|[7-9]\\d+)-[1-3]");
        // Pour un (ou plusieurs) test(s) en particulier:
        //AbstractProcessTest.staticSetUp("u26-1");
       return AbstractProcessTest.collectData();
    }
    
    
    
    
}
