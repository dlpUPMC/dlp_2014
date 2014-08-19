/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASTParser.java 1299 2013-08-27 07:09:39Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Le but de cette classe est de transformer un document XML en un
 * AST conforme à fr.upmc.ilp.interfaces.IAST.  */

public class ASTParser {

  /** Constructeur.  */

  public ASTParser () {}

  /** Transformer un document (ou un nœud) DOM en un AST (et donc un
   * IAST).
   *
   * C'est une grande méthode monolithique qui analyse le DOM (une
   * structure de données arborescente correspondant au document XML
   * initial) en un AST où chaque nœud est typé suivant sa catégorie
   * syntaxique. Il est difficile d'avoir un style objet sur ce type
   * de code qui correspond à une construction. Nous verrons une
   * nouvelle organisation de ce code bientôt.
   *
   * @throws ASTException en cas de problème.
   * NOTA: comme le document d'entrée est supposé être valide pour le
   * schéma RelaxNG approprié, de nombreux risques d'erreur sont ainsi
   * éliminés et ne sont donc pas explicitement testés. Par exemple,
   * le fait qu'un fils « condition » apparaît toujours sous «
   * alternative, » que l'attribut « valeur » est présent dans «
   * entier, » etc. Tous ces faits sont assumés corrects.
   */

  public AST parse (Node n)
    throws ASTException {
    switch ( n.getNodeType() ) {

    case Node.DOCUMENT_NODE: {
      Document d = (Document) n;
      return this.parse(d.getDocumentElement());
    }

    case Node.ELEMENT_NODE: {
      Element e = (Element) n;
      NodeList nl = e.getChildNodes();
      String name = e.getTagName();

      switch (name) {
      case "programme1" : {
          return new ASTprogram(this.parseList(nl));
      }
      case "alternative" : {
        AST cond = findThenParseChild(nl, "condition");
        AST conseq = findThenParseChildAsInstructions(nl, "consequence");
        try {
          AST alt = findThenParseChildAsInstructions(nl, "alternant");
          return new ASTalternative(cond, conseq, alt);
        } catch (ASTException exc) {
          return new ASTalternative(cond, conseq);
        }
      } 
      case "sequence": {
        return new ASTsequence(this.parseList(nl));
      } 
      case "blocUnaire": {
        ASTvariable var = (ASTvariable) findThenParseChild(nl, "variable");
        AST init = findThenParseChild(nl, "valeur");
        ASTsequence body = (ASTsequence) findThenParseChild(nl, "corps");
        return new ASTunaryBlock(var, init, body);
      } 
      case "variable": {
        // La variable sera, suivant les contextes, encapsulée
        // dans une référence.
        String nick = e.getAttribute("nom");
        return new ASTvariable(nick);
      } 
      case "invocationPrimitive": {
        String op = e.getAttribute("fonction");
        List<AST> largs = parseList(nl);
        return new ASTinvocationPrimitive(op, largs.toArray(new AST[0]));
      } 
      case "operationUnaire": {
        String op = e.getAttribute("operateur");
        AST rand = findThenParseChild(nl, "operande");
        return new ASTunaryOperation(op, rand);
      } 
      case "operationBinaire": {
        String op = e.getAttribute("operateur");
        AST gauche = findThenParseChild(nl, "operandeGauche");
        AST droite = findThenParseChild(nl, "operandeDroit");
        return new ASTbinaryOperation(op, gauche, droite);
      } 
      case "entier": {
        return new ASTinteger(e.getAttribute("valeur"));
      } 
      case "flottant": {
        return new ASTfloat(e.getAttribute("valeur"));
      } 
      case "chaine": {
        String text = e.getTextContent();
        return new ASTstring(text);
      } 
      case "booleen": {
        return new ASTboolean(e.getAttribute("valeur"));
      }
      // Une série d'éléments devant être analysés comme des expressions:
      case "operandeGauche":
      case "operandeDroit":
      case "operande":
      case "condition":
      case "consequence":
      case "alternant": 
      case "valeur": {
        return this.parseUniqueChild(nl);
      }
      // Une série d'éléments devant être analysée comme une séquence:
      case "corps": {
        return new ASTsequence(this.parseList(nl));
      }
      default: {
        String msg = "Unknown element name: " + name;
        throw new ASTException(msg);
      }
    }
    }

    default: {
      String msg = "Unknown node type: " + n.getNodeName();
      throw new ASTException(msg);
    }
    }
  }

  /** Analyser une séquence d'éléments pour en faire un ASTlist
   * c'est-à-dire une séquence d'AST.
   */

  protected List<AST> parseList (NodeList nl)
    throws ASTException {
    List<AST> result = new Vector<>();
    int n = nl.getLength();
    LOOP:
    for ( int i = 0 ; i<n ; i++ ) {
      Node nd = nl.item(i);
      switch ( nd.getNodeType() ) {

      case Node.ELEMENT_NODE: {
        AST p = this.parse(nd);
        result.add(p);
        continue LOOP;
      }

      default: {
        // On ignore tout ce qui n'est pas élément XML:
      }
      }
    }
    return result;
  }

  /** Trouver un élément d'après son nom et l'analyser pour en faire
   * un AST.
   *
   * @throws ASTException
   *   lorsqu'un tel élément n'est pas trouvé.
   */

  protected AST findThenParseChild (NodeList nl, String childName)
    throws ASTException {
    int n = nl.getLength();
    for ( int i = 0 ; i<n ; i++ ) {
      Node nd = nl.item(i);
      switch ( nd.getNodeType() ) {

      case Node.ELEMENT_NODE: {
        Element e = (Element) nd;
        if ( childName.equals(e.getTagName()) ) {
          return this.parse(e);
        }
        break;
      }

      default: {
        // On ignore tout ce qui n'est pas élément XML:
      }
      }
    }
    String msg = "No such child element " + childName;
    throw new ASTException(msg);
  }

  /** Trouver un élément d'après son nom et analyser son contenu pour
   * en faire un ASTsequence.
   *
   * @throws ASTException
   *   lorsqu'un tel élément n'est pas trouvé.
   */

  protected ASTsequence findThenParseChildAsInstructions (
          NodeList nl, String childName)
    throws ASTException {
    int n = nl.getLength();
    for ( int i = 0 ; i<n ; i++ ) {
      Node nd = nl.item(i);
      switch ( nd.getNodeType() ) {

      case Node.ELEMENT_NODE: {
        Element e = (Element) nd;
        if ( childName.equals(e.getTagName()) ) {
          return new ASTsequence(this.parseList(e.getChildNodes()));
        }
        break;
      }

      default: {
        // On ignore tout ce qui n'est pas élément XML:
      }
      }
    }
    String msg = "No such child element " + childName;
    throw new ASTException(msg);
  }

  /** Analyser une suite comportant un unique élément.
   *
   * @throws ASTException
   *   lorsque la suite ne contient pas exactement un seul élément.
   */

  protected AST parseUniqueChild (NodeList nl)
  throws ASTException {
      AST result = null;
      int n = nl.getLength();
      for ( int i = 0 ; i<n ; i++ ) {
          Node nd = nl.item(i);
          switch ( nd.getNodeType() ) {

          case Node.ELEMENT_NODE: {
              Element e = (Element) nd;
              if ( result == null ) {
                  result = this.parse(e);
              } else {
                  String msg = "Non unique child";
                  throw new ASTException(msg);
              }
              break;
          }

          default: {
              // On ignore tout ce qui n'est pas élément XML:
          }
          }
      }
      if ( result == null ) {
          throw new ASTException("No child at all");
      }
      return result;
  }

}

// fin de ASTParser.java
