/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:EASTFileTest.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.upmc.ilp.ilp1.eval.EAST;
import fr.upmc.ilp.ilp1.eval.EASTException;
import fr.upmc.ilp.ilp1.eval.EASTFactory;
import fr.upmc.ilp.ilp1.eval.EASTParser;
import fr.upmc.ilp.ilp1.runtime.CommonPlus;
import fr.upmc.ilp.ilp1.runtime.ConstantsStuff;
import fr.upmc.ilp.ilp1.runtime.EmptyLexicalEnvironment;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;
import fr.upmc.ilp.ilp1.runtime.PrintStuff;
import fr.upmc.ilp.tool.FileTool;

/** Test d'evaluation de programmes XML.
 *
 * Ces fichiers (situés en Grammars/Samples/) ont été préparés par des
 * programmes rédigés en Scheme (situés en Grammars/Scheme/), ils sont
 * accompagnes d'un fichier .result qui représente la valeur attendue
 * et un fichier .print qui contient ce qui est imprimé. Cette classe
 * doit etre executee dans le repertoire qui contient Java et Grammars
 * (car Eclipse ne semble pas permettre que l'on indique Java/ tout court!).
 */

public class EASTFileTest extends TestCase {

  /** Établir l'environnement de test. */

  @Override
  public void setUp () {
    common = new CommonPlus();
    lexenv = EmptyLexicalEnvironment.create();
    sw = new StringWriter();
    ps = new PrintStuff(sw);
    lexenv = ps.extendWithPrintPrimitives(lexenv);
    cs = new ConstantsStuff();
    lexenv = cs.extendWithPredefinedConstants(lexenv);
    factory = new EASTFactory();
  }
  protected ILexicalEnvironment lexenv;
  protected ICommon common;
  protected EASTFactory factory;
  protected StringWriter sw;
  protected PrintStuff ps;
  protected ConstantsStuff cs;

  /** Les caractéristiques des programmes XML à tester: */

  protected final File directory = new File(
          "Grammars" + File.separator + "Samples" + File.separator );
  protected final Pattern p = Pattern.compile("^u\\d+-1.xml$");
  protected final Pattern pfull =
    Pattern.compile("(.*" + File.separator + "u\\d+-1).xml$");

  /** Tester un programme c'est-à-dire le lire depuis XML, le
   * convertir en un EAST, l'évaluer et comparer le résultat avec ce
   * qui était attendu.  */

  protected void handleFile (String basefilename)
    throws IOException, ParserConfigurationException, SAXException,
           EASTException, EvaluationException {
    // Vider le tampon de sortie
    sw.getBuffer().delete(0, sw.getBuffer().length());
    File xmlFile = new File(basefilename + ".xml");
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document d = db.parse(xmlFile);
    EASTParser parser = new EASTParser(factory);
    EAST east = (EAST) parser.parse(d);
    // Évaluer et comparer les résultats:
    Object result = east.eval(lexenv, common);
    System.err.println("Obtained result is: " + result);
    String expectedResult = FileTool.readExpectedResult(basefilename);
    System.err.println("Expected result was: " + expectedResult);
    if ( result instanceof Double ) {
        double fExpectedResult = Double.parseDouble(expectedResult);
        assertEquals(fExpectedResult, (Double)result, 0.1);
    } else {
        // Comme noté par Olivier.Tran@etu.upmc.fr, il faut équeuter
        // (par trim()) les deux termes de la comparaison:
        assertEquals(expectedResult, result.toString().trim());
    }
    // Comparer les impressions:
    String expectedPrinting = FileTool.readExpectedPrinting(basefilename);
    String printing = ps.getPrintedOutput();
    System.err.println("Obtained printing:" + printing);
    System.err.println("Expected printing:" + expectedPrinting);
    assertEquals(expectedPrinting, printing.trim());
  }

  /** Rechercher tous les fichiers u*-1.xml et les tester. */

  public void testUFiles ()
    throws IOException, ParserConfigurationException, SAXException,
           EASTException, EvaluationException {
    FilenameFilter ff = new FilenameFilter() {
        public boolean accept (File dir, String name) {
          final Matcher m = p.matcher(name);
          return m.matches();
        }
      };
    final File[] testFiles = directory.listFiles(ff);
    // Si cette assertion n'est pas remplie, on n'est certainement pas
    // dans le bon repertoire (celui qui contient Grammars).
    assertNotNull(testFiles);
    // Au moins 38 programmes de test:
    assertTrue(testFiles.length >= 38);
    // Trier les noms de fichiers en place:
    java.util.Arrays.sort(testFiles, new java.util.Comparator<File>() {
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

  /**
   * Un point d'entree pour lancer cette classe de Test pour Junit3
   * comme une application Java. Elle requiert sur la ligne de
   * commande, le nom d'un fichier ILP a evaluer.
   */

  public static void main (String[] argument)
  throws Exception {
      if ( argument.length != 1 ) {
          String msg = "Missing ILP filename!";
          throw new RuntimeException(msg);
      }
      File f = new File(argument[0]);
      if ( f.exists() && f.canRead() ) {
          String baseFileName = f.getAbsolutePath();
          if ( baseFileName.endsWith(".xml") ) {
              int n = baseFileName.lastIndexOf(".xml");
              baseFileName = baseFileName.substring(0, n);
          }
          EASTFileTest tc = new EASTFileTest();
          tc.setUp();
          tc.handleFile(baseFileName);
      }
  }

}

// end of EASTFileTest.java
