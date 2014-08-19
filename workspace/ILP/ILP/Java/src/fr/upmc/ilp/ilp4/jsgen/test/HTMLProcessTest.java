package fr.upmc.ilp.ilp4.jsgen.test;

import java.io.IOException;

import org.junit.Before;
import org.junit.runner.RunWith;

import fr.upmc.ilp.ilp4.jsgen.HTMLProcess;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.Parameterized;

@RunWith(value=Parameterized.class)
public class HTMLProcessTest extends ProcessTest {

    public HTMLProcessTest(final File file) {
        super(file);
    }

    @Before
    @Override
    public void setUp () throws IOException {
        this.setProcess(new HTMLProcess(finder));
    }

}
