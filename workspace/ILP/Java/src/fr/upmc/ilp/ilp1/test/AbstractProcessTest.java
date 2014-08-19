package fr.upmc.ilp.ilp1.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.upmc.ilp.ilp1.interfaces.IProcess;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.Parameterized;

/** Cette classe de test utilise JUnit4 ainsi qu'une extension permettant de
 * donner (fr.upmc.ilp.tool.Parameterized) de jolis noms aux divers tests.
 * Elle concentre l'essentiel des methodes statiques qui permettent de tester
 * l'interprete et le compilateur sur une serie de fichiers specifies par un
 * repertoire et une expression rationnelle. Elle est generique sur la classe
 * implantant IProcess a tester.
 */

@RunWith(Parameterized.class)
public abstract class AbstractProcessTest {

    /** Le process à tester. */
    protected IProcess process;

    /** Initialiser le repertoire et l'expression rationnelle indiquant
     *  ou trouver les programmes ILP a tester.  */
    public static void staticSetUp (java.io.File theDirectory,
                                    String thePattern ) {
        staticSetUp(theDirectory);
        staticSetUp(thePattern);
    }
    public static void staticSetUp (java.io.File theDirectory) {
        directory = theDirectory;
    }
    public static void staticSetUp (String thePattern) {
        pattern = thePattern;
    }

    /** Le répertoire où trouver les exemples de programmes ILP à traiter.
     * C'est usuellement le répertoire Grammars/Samples.  */
    protected static java.io.File directory;
    /** Le motif (une regexp) indiquant les programmes ILP à traiter. */
    protected static String pattern;

    /** Chercher tous les programmes à tester.
     *
     * Comme c'est calculé au chargement de la classe, ce calcul doit être
     * statique afin de pouvoir instancier la classe avec chaque fichier
     * à tester. */

    public static Collection<File[]> collectData() throws Exception {
        final Pattern p = Pattern.compile("^" + pattern + ".xml$");
        final FilenameFilter ff = new FilenameFilter() {
            public boolean accept (java.io.File dir, String name) {
                final Matcher m = p.matcher(name);
                return m.matches();
            }
        };
        final java.io.File[] testFiles = directory.listFiles(ff);
        
        // Vérifier qu'il y a au moins un programme à tester sinon on pourrait
        // avoir l'erreur bête que tout marche puisqu'aucune erreur n'a été vue!
        if ( testFiles.length == 0 ) {
            final String msg = "ILP: Cannot find a single test like " + pattern;
            throw new RuntimeException(msg);
        }

        // Trier les noms de fichiers en place:
        java.util.Arrays.sort(testFiles,
                new java.util.Comparator<java.io.File>() {
            public int compare (java.io.File f1, java.io.File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });

       Collection<File[]> result = new Vector<>();
        for ( final java.io.File f : testFiles ) {
            result.add(new File[] { new File(f) });
        }
        return result;
    }
    
    

    /** Le constructeur d'un test concernant un programme ILP à tester. */
    public AbstractProcessTest (final File file) {
        this.file = file;
    }
    public void setFile (File file) {
        this.file = file;
    }
    public IProcess getProcess () {
        return this.process;
    }
    public void setProcess (IProcess process) {
        this.process = process;
    }
    // C'est un fichier avec un nom relatif comme "Grammars/Samples/u01-1.xml"
    // Attention, c'est un fr.upmc.ilp.tool.File et non un java.io.File!
    protected File file;

    /** Tester le programme ILP. */

    @Test //(timeout=5000)
    public void handleFile ()
    throws Exception {
        System.err.println("Testing " + this.file.getAbsolutePath() + " ...");

        final fr.upmc.ilp.tool.File xmlFile =
            new fr.upmc.ilp.tool.File(this.file);
        assertTrue(xmlFile.exists());

        this.getProcess().initialize(xmlFile);
        if ( ! this.getProcess().isInitialized() ) {
            System.err.println(this.getProcess().getInitializationFailure());
        }

        this.getProcess().prepare();
        if ( ! this.getProcess().isPrepared() ) {
            System.err.println(this.getProcess().getPreparationFailure());
        }

        // Ces verifications sont individualisées dans des methodes pour
        // être ainsi sous-classées au cas où.

        prepareComparison();

        // Ici, on interprete puis on compile. Dans la vraie vie, on ferait
        // l'un ou l'autre mais pas les deux!
        if ( doInterpret) {
            interpretFile();
        }

        if ( doCompile ) {
            compileFile();
        }
    }
    protected static boolean doInterpret = true;
    protected static boolean doCompile = true;
    
    /** Preparation de l'examen des resultats. 
     * Chercher les resultats attendus (pour interprete et compilateur). */
    public void prepareComparison () throws IOException {
        expectedResult = FileTool.readExpectedResult(this.file);
        expectedPrinting = FileTool.readExpectedPrinting(this.file);
        compareDouble = false;
        fExpectedResult = 0.0;        
    }
    protected String expectedResult;
    protected String expectedPrinting;
    protected boolean compareDouble = false;
    protected double fExpectedResult = 0.0;

    /** Test de l'interprete */
    public void interpretFile () throws Exception {
        this.getProcess().interpret();
        if ( !this.getProcess().isInterpreted() ) {
            System.err.println(this.getProcess().getInterpretationFailure());
        }
        final Object result = this.getProcess().getInterpretationValue();
        if ( result instanceof Double ) {
            // Cas particulier de la comparaison de flottants!
            fExpectedResult = Double.parseDouble(expectedResult);
            assertEquals(fExpectedResult, (Double)result, 0.1);
            compareDouble = true;
        } else {
            assertEquals(expectedResult, result.toString());
        }
        String printing = this.getProcess().getInterpretationPrinting();
        assertEquals(expectedPrinting, printing);
    }

    /** Test du compilateur */
    public void compileFile () throws Exception {
        this.getProcess().compile();
        if ( !this.getProcess().isCompiled() ) {
            System.err.println(this.getProcess().getCompilationFailure());
        }
        this.getProcess().runCompiled();
        // Si erreur, voir this.process.executionFailure
        assertTrue(this.getProcess().isExecuted());
        String printing = this.getProcess().getExecutionPrinting();
        if ( compareDouble ) {
            // Cas particulier de la comparaison de flottants!
            // On assume que rien d'autre n'est imprimé que le double
            double fPrinting = Double.parseDouble(printing.trim());
            assertEquals(fExpectedResult, fPrinting, 0.1);
        } else {
            // On retire les retours a la ligne internes
            printing = printing.replaceAll("\n", "");
            String expected = expectedPrinting + expectedResult;
            expected = expected.replaceAll("\n", "");
            assertEquals(expected.trim(), printing.trim());
        }
    }
}
