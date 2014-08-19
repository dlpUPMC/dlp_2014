/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASTfromXML.java 1228 2012-08-31 16:48:30Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml.test;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import fr.upmc.ilp.ilp1.fromxml.AST;
import fr.upmc.ilp.ilp1.fromxml.ASTException;
import fr.upmc.ilp.ilp1.fromxml.ASTParser;
import fr.upmc.ilp.ilp1.interfaces.IAST;

/** Utilitaires pour convertir un fragment d'XML en un AST. Ces
 * utilitaires sont utiles pour les tests avec JUnit (voir
 * ASTParserTest.java par exemple).
 */

public class ASTfromXML {

  /** Convertir un fragment d'XML en AST. */

  public static AST toAST (String xml)
    throws ASTException {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      StringReader sr = new StringReader(xml);
      InputSource is = new InputSource(sr);
      Document d = db.parse(is);
      ASTParser ap = new ASTParser();
      AST ast = ap.parse(d);
      return ast;
    } catch (ASTException exc) {
      throw exc;
    } catch (Throwable cause) {
      throw new ASTException(cause);
    }
  }

  /** Convertir un fragment d'XML en un IAST. */

  public static IAST toIAST (String xml)
    throws ASTException {
    return ASTfromXML.toAST(xml);
  }

}
