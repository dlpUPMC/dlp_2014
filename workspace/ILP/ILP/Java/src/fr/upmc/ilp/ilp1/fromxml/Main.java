/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: Main.java 1228 2012-08-31 16:48:30Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import com.thaiopensource.validate.*;

/** Lit puis évalue un programme. Le langage utilisé est ILP1 défini
 * par grammar1.rnc
 *
 * Usage: java fr.upmc.ilp.fromxml.Main grammaire.rng programme.xml
 */

public class Main {

  public static void main (String[] argument)
    throws ASTException {
    Main m = new Main(argument);
    m.run();
  }

  public Main (String[] argument)
    throws ASTException {
    if ( argument.length < 2 ) {
      throw new ASTException("Usage: Main grammaire.rng programme.xml");
    }
    this.rngfile = new File(argument[0]);
    this.xmlfile = new File(argument[1]);
    if ( !this.rngfile.exists() ) {
      throw new ASTException("Fichier .rng introuvable.");
    }
    if ( !this.xmlfile.exists() ) {
      throw new ASTException("Fichier .xml introuvable.");
    }
  }

  private File rngfile;
  private File xmlfile;

  public void run ()
    throws ASTException {

    try {

      // (1) validation vis-à-vis de RNG:
      // MOCHE: c'est redondant avec (2) car le programme est relu encore une
      // fois avec SAX. Les phases 1 et 2 pourraient s'effectuer ensemble.
      ValidationDriver vd = new ValidationDriver();
      InputSource isg = ValidationDriver.fileInputSource(
            rngfile.getAbsolutePath());
      vd.loadSchema(isg);
      InputSource isp = ValidationDriver.fileInputSource(
            this.xmlfile.getAbsolutePath());
      if ( ! vd.validate(isp) ) {
        throw new ASTException("programme XML non valide");
      }

      // (2) convertir le fichier XML en DOM:
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document d = db.parse(this.xmlfile);

      // (3) conversion vers un AST donc un IAST:
      ASTParser ap = new ASTParser();
      AST ast = ap.parse(d);

      // (3bis) Impression en XML:
      System.out.println(ast.toXML());

    } catch (ASTException e) {
      throw e;
    } catch (Throwable cause) {
      throw new ASTException(cause);
    }
  }

}

// fin de Main.java
