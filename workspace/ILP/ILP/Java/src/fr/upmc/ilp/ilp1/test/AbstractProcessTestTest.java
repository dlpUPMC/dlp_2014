package fr.upmc.ilp.ilp1.test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import fr.upmc.ilp.ilp1.interfaces.IProcess;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.Finder;
import fr.upmc.ilp.tool.IFinder;

/** Cette classe teste la classe AbstractProcessTest d'où son nom! */

public class AbstractProcessTestTest {
    
    @Before
    public void setUp () {
        this.finder = new Finder();
    }
    private IFinder finder;
    
    @Test
    public void testSingleFile () throws Throwable {
        final AbstractMainForProcessTest apt = 
            new fr.upmc.ilp.ilp1.test.ProcessTest(
                    new File("Grammars/Samples/u01-1.xml"));
        AbstractMainForProcessTest.initializeFromOptions();
        IProcess process = new fr.upmc.ilp.ilp1.Process(finder);
        apt.setProcess(process);
        process.setFinder(AbstractMainForProcessTest.finder);
        process.setVerbose(AbstractMainForProcessTest.options.verbose);
        process.setGrammar(AbstractMainForProcessTest.grammarFile);
        process.setCFile(AbstractMainForProcessTest.cFile);
        process.setCompileThenRunScript(AbstractMainForProcessTest.scriptFile);
        apt.handleFile();
    }
    
    @Test
    public void testManyFiles () throws Throwable {
        final AbstractMainForProcessTest apt = 
            new fr.upmc.ilp.ilp1.test.ProcessTest(
                    new File("Grammars/Samples/u01-1.xml"));
        AbstractMainForProcessTest.initializeFromOptions();
        IProcess process = new fr.upmc.ilp.ilp1.Process(finder);
        apt.setProcess(process);
        process.setFinder(AbstractMainForProcessTest.finder);
        process.setVerbose(AbstractMainForProcessTest.options.verbose);
        process.setGrammar(AbstractMainForProcessTest.grammarFile);
        process.setCFile(AbstractMainForProcessTest.cFile);
        process.setCompileThenRunScript(AbstractMainForProcessTest.scriptFile);
        assertTrue(
            apt.handleFiles(
                new String[] {
                        "Grammars/Samples/u01-1.xml",
                        "Grammars/Samples/u02-1.xml",
                        "Grammars/Samples/u03-1.xml"
                }));
    }
}
