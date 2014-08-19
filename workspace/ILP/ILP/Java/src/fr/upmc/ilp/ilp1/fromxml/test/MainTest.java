/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: MainTest.java 1228 2012-08-31 16:48:30Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml.test;

import java.io.File;

import fr.upmc.ilp.ilp1.fromxml.ASTException;
import fr.upmc.ilp.ilp1.fromxml.Main;

import junit.framework.TestCase;

/** Tests (JUnit3) simples d'equivalence depuis fichier XML -> AST ->
 * XML. Tests a completer (notamment pour verifier que l'XML engendre
 * est valide.
 */

public class MainTest extends TestCase {
    
    public void processFile (String grammarName, String fileName)
    throws ASTException {
        Main m = new Main(new String[] {
                grammarName,
                fileName
        });
        m.run();
    }

  public void testP1 () throws ASTException {
    processFile("Grammars" + File.separator + "grammar1.rng",
                "Grammars" + File.separator + "Samples" + File.separator + "p1-1.xml");
  }

  public void testP2 () throws ASTException {
    processFile("Grammars" + File.separator + "grammar1.rng",
                "Grammars" + File.separator + "Samples" + File.separator + "p2-1.xml");
  }

  public void testP2un () throws ASTException {
    processFile("Grammars" + File.separator + "grammar1.rng",
                "Grammars" + File.separator + "Samples" + File.separator + "p2un-1.xml");
  }

  public void testP3if () throws ASTException {
    processFile("Grammars" + File.separator + "grammar1.rng",
                "Grammars" + File.separator + "Samples" + File.separator + "p3if-1.xml");
  }

  public void testP3if2 () throws ASTException {
    processFile("Grammars" + File.separator + "grammar1.rng",
                "Grammars" + File.separator + "Samples" + File.separator + "p3if2-1.xml");
  }

  public void testP4seq () throws ASTException {
    processFile("Grammars" + File.separator + "grammar1.rng",
                "Grammars" + File.separator + "Samples" + File.separator + "p4seq-1.xml");
  }

  public void testP5let1 () throws ASTException {
    processFile("Grammars" + File.separator + "grammar1.rng",
                "Grammars" + File.separator + "Samples" + File.separator + "p5let1-1.xml");
  }

  public void testP6letvar () throws ASTException {
    processFile("Grammars" + File.separator + "grammar1.rng",
                "Grammars" + File.separator + "Samples" + File.separator + "p6letvar-1.xml");
  }

  // et tous les programmes u*-1.xml

}

// end of MainTest.java
