package fr.upmc.ilp.ilp4.test;

import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
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
public class NonInlinedEvaluationTest
extends fr.upmc.ilp.ilp4.test.ProcessTest {

    /** Le constructeur du test sur un fichier. */

    public NonInlinedEvaluationTest (final File file) {
        super(file);
    }

    @Before
    @Override
    public void setUp () throws IOException {
        this.setProcess(new NonInlinedEvaluationProcess(finder, this.file));
        getProcess().setFinder(finder);
        getProcess().setVerbose(options.verbose);
        //getProcess().setGrammar(grammarFile);
        getProcess().setCFile(cFile);
        // NON car le patron est specifié par new fr.upmc.ilp.ilp1.Process():
        //getProcess().setCTemplateFile(cTemplateFile);
        getProcess().setCompileThenRunScript(scriptFile);
    }

    @Parameters
    public static Collection<File[]> data() throws Exception {
        initializeFromOptions();
        AbstractProcessTest.staticSetUp(samplesDir, "u\\d+-[1-4]");
        // Pour un (ou plusieurs) test(s) en particulier:
        //AbstractProcessTest.staticSetUp("u26-2");
        return AbstractProcessTest.collectData();
    }

    // Le processus a effectuer pour cette suite de test.
    // On fait tout sauf l'integration des fonctions globales.

    public static class NonInlinedEvaluationProcess
    extends fr.upmc.ilp.ilp4.Process {

        public NonInlinedEvaluationProcess (IFinder finder, File file) 
        throws IOException {
            super(finder);
            this.file = file;
        }
        protected File file;

        @Override
        public void prepare () {
            try {
                final Document d = getDocument(this.rngFile);
                setCEAST(getParser().parse(d));
                
                // Attention! XMLwriter sait imprimer un AST correct mais peut
                // echouer sur un ast non normalisé.

                XMLwriter xmlWriter = new XMLwriter();
                String xml = "<!-- ecrivable ??? -->";
                xml = xmlWriter.process(getCEAST());
                FileTool.stuffFile(this.file.getNameWithoutSuffix() + "-A.xml", xml);

                // La premiere analyse statique (necessaire pour eval)
                setCEAST(performNormalization());
                xml = "<!-- ecrivable ??? -->";
                xml = xmlWriter.process(getCEAST());
                FileTool.stuffFile(this.file.getNameWithoutSuffix() + "-B.xml", xml);
                getCEAST().computeInvokedFunctions();
                xml = "<!-- ecrivable ??? -->";
                xml = xmlWriter.process(getCEAST());
                FileTool.stuffFile(this.file.getNameWithoutSuffix() + "-C.xml", xml);
                // Pas d'integration: getCEAST().inline();
                getCEAST().computeGlobalVariables();
                xml = "<!-- ecrivable ??? -->";
                xml = xmlWriter.process(getCEAST());
                FileTool.stuffFile(this.file.getNameWithoutSuffix() + "-D.xml", xml);
                System.out.println(xml);

                this.prepared = true;

            } catch (Throwable e) {
                this.preparationFailure = e;
            }
        }
    }

}
