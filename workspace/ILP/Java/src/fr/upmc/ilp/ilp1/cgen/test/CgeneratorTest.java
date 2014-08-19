/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CgeneratorTest.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.cgen.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.upmc.ilp.ilp1.cgen.CgenEnvironment;
import fr.upmc.ilp.ilp1.cgen.CgenLexicalEnvironment;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.cgen.Cgenerator;
import fr.upmc.ilp.ilp1.cgen.ICgenEnvironment;
import fr.upmc.ilp.ilp1.cgen.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp1.eval.EAST;
import fr.upmc.ilp.ilp1.eval.EASTException;
import fr.upmc.ilp.ilp1.eval.EASTFactory;
import fr.upmc.ilp.ilp1.eval.EASTParser;
import fr.upmc.ilp.ilp1.eval.EASTinteger;
import fr.upmc.ilp.ilp1.eval.EASTsequence;
import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.ProgramCaller;

/** Tests (JUnit) pour la compilation vers C. Il faut etre dans le repertoire
 * qui contient Grammars et Java afin que ce code puisse trouver les fichiers
 * de test a compiler. */

public class CgeneratorTest extends TestCase {

    @Override
    public void setUp () {
    final ICgenEnvironment common = new CgenEnvironment();
    compiler = new Cgenerator(common);
    factory = new EASTFactory();
    lexenv = CgenLexicalEnvironment.Empty.create();
    lexenv = common.extendWithPrintPrimitives(lexenv);
    lexenv = common.extendWithPredefinedConstants(lexenv);
  }
  private Cgenerator compiler;
  private EASTFactory factory;
  private ICgenLexicalEnvironment lexenv;

  /** Verifier que l'on ne peut compiler en C un tres grand entier. */

  public void testBigInteger1 () {
    final List<EAST> args = new Vector<>();
    args.add(new EASTinteger("12345678909876543210123456789"));
    args.add(new EASTinteger("1"));
    final EAST east = new EASTsequence(args);
    // Compiler vers C
    try {
      compiler.compile(east, lexenv, "return");
    } catch ( CgenerationException e) {
      assertTrue(true);
    }
  }

  // La detection des grands entiers non compilable etait erronee
  // comme l'a remarqué Nicolas.Bros@gmail.com. Il faut donc tester
  // aussi les grands entiers negatifs.

  public void testBigInteger2 () {
    final List<EAST> args = new Vector<>();
    args.add(new EASTinteger("-12345678909876543210123456789"));
    args.add(new EASTinteger("1"));
    final EAST east = new EASTsequence(args);
    // Compiler vers C
    try {
      compiler.compile(east, lexenv, "return");
    } catch ( CgenerationException e) {
      assertTrue(true);
    }
  }
  /** Les caractéristiques des programmes XML à tester: */

  private final File directory = new File(
          "Grammars" + File.separator + "Samples" + File.separator );
  private final Pattern p = Pattern.compile("^u\\d+-1.xml$");
  private final Pattern pfull =
    Pattern.compile("(.*" + File.separator + "u\\d+-1).xml$");

  /** Tester un programme c'est-à-dire le lire depuis XML, le
   * convertir en un EAST, le compiler en C, compiler ce code C avec
   * un patron standard et comparer le résultat avec ce qui était
   * attendu. */

  protected void handleFile (String basefilename)
    throws IOException, ParserConfigurationException, SAXException,
           EASTException, CgenerationException {
    // Imprimer le fichier scheme ?
    File xmlFile = new File(basefilename + ".xml");
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document d = db.parse(xmlFile);
    EASTParser parser = new EASTParser(factory);
    EAST east = (EAST) parser.parse(d);
    // Compiler vers C
    String ccode = compiler.compile(east, lexenv, "return");
    FileTool.stuffFile(basefilename + ".c", ccode);
    // appeler indent,                                             FUTURE
    String program = "bash "
      + "C/compileThenRun.sh "
      + basefilename + ".c";
    ProgramCaller pc = new ProgramCaller(program);
    pc.setVerbose();
    pc.run();
    String expectedResult = FileTool.readExpectedResult(basefilename);
    String expectedPrinting = FileTool.readExpectedPrinting(basefilename);
    assertEquals(expectedPrinting + expectedResult, pc.getStdout().trim());
  }

  /** Rechercher tous les fichiers u*-1.xml et les tester. */

  public void testUFiles ()
    throws IOException, ParserConfigurationException, SAXException,
           EASTException, CgenerationException {
    FilenameFilter ff = new FilenameFilter() {
        @Override
        public boolean accept (File dir, String name) {
          final Matcher m = p.matcher(name);
          return m.matches();
        }
      };
    final File[] testFiles = directory.listFiles(ff);
    // Si cette assertion est fausse, on n'est probablement pas dans le
    // bon repertoire.
    assertNotNull(testFiles);
    // Au moins 38 programmes de test:
    assertTrue(testFiles.length >= 38);
    // Trier les noms de fichiers en place:
    java.util.Arrays.sort(testFiles, new java.util.Comparator<File>() {
        @Override
        public int compare (File f1, File f2) {
           return f1.getName().compareTo(f2.getName());
        }
      });
    for ( int i = 0 ; i<testFiles.length ; i++ ) {
      final String filename = testFiles[i].getPath();
      System.err.println("Testing " + filename);
      final Matcher m = pfull.matcher(filename);
      assertTrue(m.matches());
      handleFile(m.group(1));
    }
  }

}

// end of CgeneratorTest.java
