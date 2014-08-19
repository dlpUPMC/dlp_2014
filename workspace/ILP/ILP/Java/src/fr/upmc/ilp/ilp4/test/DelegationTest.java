package fr.upmc.ilp.ilp4.test;

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
import fr.upmc.ilp.tool.IFinder;
import fr.upmc.ilp.tool.Parameterized;
import fr.upmc.ilp.tool.Parameterized.Parameters;

@RunWith(value=Parameterized.class)
public class DelegationTest
extends fr.upmc.ilp.ilp4.test.ProcessTest {

    /** Le constructeur du test sur un fichier. */

    public DelegationTest (final File file) {
        super(file);
    }

    @Before
    @Override
    public void setUp () throws IOException {
        this.setProcess(new DelegationProcess(finder, this.file.getBaseName()));
    }

    @Parameters
    public static Collection<File[]> data() throws Exception {
        initializeFromOptions();
        AbstractProcessTest.staticSetUp(samplesDir,"u\\d+-[1-4]");
        // Pour un (ou plusieurs) test(s) en particulier:
        //AbstractProcessTest.staticSetUp("u41-1");
        return AbstractProcessTest.collectData();
    }

    // Le processus a effectuer pour cette suite de test. Il vise
    // a simplement convertir DOM en IAST4.

    public static class DelegationProcess
    extends fr.upmc.ilp.ilp4.Process {

        public DelegationProcess (IFinder finder, String basename) 
        throws IOException {
            super(finder);
            this.basename = basename;
        }
        protected String basename;

        @Override
        public void prepare () {
            try {
                final Document d = getDocument(this.rngFile);
                setCEAST(getParser().parse(d));

                XMLwriter xmlWriter = new XMLwriter();
                String xml = xmlWriter.process(getCEAST());
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

                this.prepared = true;

            } catch (Throwable e) {
                this.preparationFailure = e;
            }
        }
    }

    /** Tester chaque programme ILP (seulement sur la preparation) */

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
    }
}
