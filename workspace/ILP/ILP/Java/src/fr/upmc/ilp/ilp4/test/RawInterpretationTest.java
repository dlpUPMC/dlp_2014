package fr.upmc.ilp.ilp4.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import fr.upmc.ilp.ilp1.test.AbstractProcessTest;
import fr.upmc.ilp.ilp4.ast.XMLwriter;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.IFinder;
import fr.upmc.ilp.tool.Parameterized;
import fr.upmc.ilp.tool.Parameterized.Parameters;


@RunWith(value=Parameterized.class)
public class RawInterpretationTest
extends fr.upmc.ilp.ilp4.test.ProcessTest {

    /** Le constructeur du test sur un fichier. */

    public RawInterpretationTest (final File file) {
        super(file);
    }

    @Before
    @Override
    public void setUp () throws IOException {
        this.setProcess(new RawInterpretationProcess(finder, this.file));
    }

    @Parameters
    public static Collection<File[]> data() throws Exception {
        initializeFromOptions();
        AbstractProcessTest.staticSetUp(samplesDir, "u\\d+-[1-4]");
        // Pour un (ou plusieurs) test(s) en particulier:
        //AbstractProcessTest.staticSetUp("u64-3");
        return AbstractProcessTest.collectData();
    }

    // Le processus a effectuer pour cette suite de test. Il vise
    // a simplement convertir DOM en IAST4, a normaliser puis a evaluer.

    public static class RawInterpretationProcess
    extends fr.upmc.ilp.ilp4.Process {

        public RawInterpretationProcess (IFinder finder, File file) 
        throws IOException {
            super(finder);
            this.basename = file.getNameWithoutSuffix(); 
        }
        protected String basename;

        @Override
        public void prepare () {
            try {
                final Document d = getDocument(this.rngFile);
                setCEAST(getParser().parse(d));

                XMLwriter xmlWriter = new XMLwriter();
                String xml = xmlWriter.process(getCEAST());
                FileTool.stuffFile(basename + "-A.xml", xml);

                // La premiere analyse statique (necessaire pour eval)
                setCEAST(performNormalization());
                xml = xmlWriter.process(getCEAST());
                FileTool.stuffFile(basename + "-B.xml", xml);
                System.out.println(xml);
                // On verifie que toutes les balises dont le nom comporte un .
                // sont bien dans ilp4.
                final Pattern tagPattern = Pattern.compile("<([^?/][^>]+?)>");
                Matcher tagMatcher = tagPattern.matcher(xml);
                while (tagMatcher.find()) {
                    String tag = tagMatcher.group(1);
                    if (tag.indexOf('.') >= 0) {
                        assertTrue(tag.matches(".*\\.ilp4.*"));
                    }
                }
                // Verifier aussi: la nature des variables, references et
                // invocations doivent etre precisee.  FUTURE

                this.prepared = true;

            } catch (Throwable e) {
                this.preparationFailure = e;
            }
        }
    }

    /** Tester chaque programme ILP. */

    @Test //(timeout=5000)
    @Override
    public void handleFile ()
    throws Exception {
        System.err.println("Testing " + this.file.getAbsolutePath() + " ...");
        final fr.upmc.ilp.tool.File xmlFile =
            new fr.upmc.ilp.tool.File(this.file);
        assertTrue(xmlFile.exists());

        this.process.initialize(xmlFile);
        assertTrue(this.process.isInitialized());

        this.process.prepare();
        assertTrue(this.process.isPrepared());

        final String expectedResult =
            FileTool.readExpectedResult(this.file.getNameWithoutSuffix());
        final String expectedPrinting =
            FileTool.readExpectedPrinting(this.file.getNameWithoutSuffix());
        
        {
            // Interpretation:
            this.process.interpret();
            assertTrue(this.process.isInterpreted());
            final Object result = this.process.getInterpretationValue();
            if ( result instanceof Double ) {
                double fExpectedResult = Double.parseDouble(expectedResult);
                assertEquals(fExpectedResult, (Double)result, 0.1);
            } else {
                assertEquals(expectedResult, result.toString());
            }
            String printing = this.process.getInterpretationPrinting();
            assertEquals(expectedPrinting, printing);
        }
    }
}
