package fr.upmc.ilp.ilp1.test;

/** Cette classe introduit un point d'entree main() pour etre utilisable
 * depuis la ligne de commandes avec une bardee d'options (decodees avec
 * jcommander) pour parametrer les tests. Cette classe est separee de
 * AbstractProcessTest afin de ne pas melanger les points de vue.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.Result;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.Finder;
import fr.upmc.ilp.tool.IFinder;
import fr.upmc.ilp.tool.Parameterized.Parameters;

public class AbstractMainForProcessTest extends AbstractProcessTest {

    /** Le constructeur d'un test concernant un programme ILP à tester. */
    public AbstractMainForProcessTest(final File file) {
        super(file);
    }

    /**
     * Teste une serie de programmes ILP. Chaque fichier est testé avec la meme
     * instance de ProcessTest. Attention, ne pas oublier d'indiquer le Process
     * a utiliser avec setProcess().
     */

    public boolean handleFiles(final String[] fileNames) {
        try {
            for (String fileName : fileNames) {
                this.setFile(new File(fileName));
                this.handleFile();
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Cette méthode peut être raffinée si l'on souhaite plus contrôler la
     * collection de fichiers à tester. Ce calcul est effectué au chargement de
     * la classe, c'est donc un calcul statique.
     */

    @Parameters
    public static Collection<File[]> data() throws Exception {
        initializeFromOptions();
        AbstractProcessTest.staticSetUp(samplesDir, pattern);
        // Pour un (ou plusieurs) test(s) en particulier:
        // AbstractProcessTest.staticSetUp(samplesDir, "u0[13]*-1");
        return AbstractProcessTest.collectData();
    }

    /**
     * La méthode main() ci-dessous permet d'utiliser ce test JUnit comme une
     * application à lancer avec des options indiquant où se trouvent les
     * différentes ressources nécessaires.
     * 
     * Les options possibles pour cette application. -v pour que le processus
     * soit plus verbeux -path repertoire un repertoire ou chercher des fichiers
     * [Comme l'on ne sait pas forcement bien quel est le repertoire courant, il
     * est souvent sage d'utiliser des repertoires en notation absolue.]
     * -grammar nom de la grammaire. Le nom peut etre relatif a l'un des
     * repertoires mentionnes dans -path. -samples le repertoire ou se trouvent
     * les programmes ILP a tourner -pattern la regexp identifiant les
     * programmes ILP a tourner -cfile nom du fichier ou sera ecrit le C
     * engendré -script nom du script compilant et executant le programme C
     * engendré -interpretonly -compileonly
     * 
     * Les valeurs par défaut stockent le fichier C engendré dans le repertoire
     * courant et supposent que ce dernier contient les sous- repertoires
     * Grammars/, Grammars/Samples/ et C/. Ces defauts permettent de tourner les
     * tests JUnit depuis Eclipse.
     */
    public static class Options {
        @Parameter(names = { "-v", "-verbose" }, description = "Level of verbosity")
        public boolean verbose = false;

        @Parameter(names = { "-I", "-path" }, description = "Option multiple indiquant les répertoires "
                + "où chercher la grammaire, le script, etc.")
        public List<String> path = new ArrayList<>();

        @Parameter(names = { "-grammar" }, description = "Le nom de la grammaire .rng à utiliser")
        public String grammarFileName = "Grammars" + File.separator
                + "grammar1.rng";

        @Parameter(names = { "-cfile" }, description = "Le nom du fichier C à engendrer")
        public String cFileName = "theProgram.c";

        @Parameter(names = { "-samples" }, description = "Le repertoire où trouver les programmes ILP à tester")
        public String samplesDirName = "Grammars" + File.separator + "Samples";

        @Parameter(names = { "-pattern" }, description = "La regexp des noms des programmes ILP à tester")
        public String patternName = "u\\d+-1";

        @Parameter(names = { "-script" }, description = "Le nom du shell script compilant et exécutant "
                + "le programme C engendré")
        public String scriptName = "C" + File.separator + "compileThenRun.sh";
        @Parameter(names = { "-interpretonly" })
        public Boolean interpretonly = false;
        @Parameter(names = { "-compileonly" })
        public Boolean compileonly = false;

        @SuppressWarnings("unused")
        public void parse (String[] arguments) {
            new JCommander(this, arguments);
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (String p : path) {
                sb.append("\npath[]: " + p);
            }
            sb.append("\ngrammarFileName: " + grammarFileName);
            sb.append("\nsamplesDirName: " + samplesDirName);
            sb.append("\npatternName: " + patternName);
            sb.append("\nscriptName: " + scriptName);
            sb.append("\ncFileName: " + cFileName);
            sb.append("\n");
            return sb.toString();
        }
    }

    public static void initializeFromOptions() throws IOException {
        try {
            options.parse(mainArguments);
            // Valider les options -path pour creer le Finder:
            finder = new Finder();
            if (options.path.size() == 0) {
                options.path.add(".");
            }
            for (String path : options.path) {
                java.io.File dir = new File(path);
                finder.addPath(dir);
            }
            // Chercher les autres fichiers à l'aide du Finder:
            grammarFile = finder.findFile(options.grammarFileName);
            scriptFile = finder.findFile(options.scriptName);
            // cFile n'a pas à être recherché: c'est là où sera écrit le C:
            cFile = new java.io.File(options.cFileName);
            if (!cFile.isAbsolute()) {
                // Utiliser le tempDir de la machine, partagé entre tous les
                // utilisateurs est risqué. On utilise le répertoire courant
                // pour éviter des problèmes de droit.
                // final File tempDir = new
                // File(System.getProperty("java.io.tmpdir"));
                final File tempDir = new File(System.getProperty("user.dir"));
                cFile = new java.io.File(tempDir, options.cFileName);
            }

            // Chercher le repertoire des programmes ILP a traiter:
            samplesDir = finder.findFile(options.samplesDirName);
            if (!samplesDir.isDirectory()) {
                final String msg = "SamplesDir not a directory!";
                throw new RuntimeException(msg);
            }
            pattern = options.patternName;
            // collectData() verifie qu'il y a bien des programmes ILP a
            // traiter!
            // Interpreter ou compiler seulement ?
            doInterpret = true;
            doCompile = true;
            if (options.interpretonly) {
                doCompile = false;
            } else if (options.compileonly) {
                doInterpret = false;
            }
        } catch (Throwable e) {
            System.err.println("PROBLEM " + e);
            throw e;
        }
    }

    protected static final Options options = new Options();
    protected static IFinder finder;
    protected static java.io.File grammarFile;
    protected static java.io.File cFile;
    protected static java.io.File samplesDir;
    protected static java.io.File scriptFile;

    /**
     * Un point d'entree pour des tests depuis shell. Ce code ne sert que
     * d'exemple pour des main() dans des sous-classes de ProcessTest. Lorsque
     * les tests sont lances par main(), on a la sequence suivante: 0 - main() 1
     * - data() qui doit executer initializeFromOptions() 2 - new ProcessTest()
     * 3 - setUp() Lorsque les tests sont lancés directement par Eclipse ou
     * Junit, la sequence commence à 1 (et saute l'etape 0).
     */

    public static void main(final String[] arguments) throws Exception {
        mainArguments = arguments;
        // Modifier les defauts de Options avec, par exemple:
        // options.patternName = "u\\d+-[12](tme2)?";
        // avant d'invoquer initializeFromOptions() ou la specialiser.
        Class<?>[] classes = new Class[] {
        // FUTUR: charger la classe par son nom passé en argument!
        // ProcessTest.class // Indiquer ici la classe de test
        };
        Result r = org.junit.runner.JUnitCore.runClasses(classes);
        String msg = "[[[" + r.getRunCount() + " Tests, " + r.getFailureCount()
                + " Failures]]]\n";
        System.err.println(msg);
    }

    protected static String[] mainArguments = new String[0];
}
