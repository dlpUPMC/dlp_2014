/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id:EASTParser.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Le but de cette classe est de transformer un document XML en un
 * AST avec évaluation conforme à fr.upmc.ilp.interfaces.IAST.
 *
 * Cet analyseur améliore ASTParser en ce sens qu'il est paramétré par
 * une fabrique de construction d'AST. À part sa construction, cet
 * analyseur s'utilise tout comme un ASTParser.
 */

public class EASTParser {

    /** Constructeur d'analyseur de document (ou noeud) DOM en un AST
     * dont les constructeurs particuliers sont fournis par une
     * fabrique. */

    public EASTParser (final IEASTFactory<EASTException> factory) {
        this.factory = factory;
    }

    protected final IEASTFactory<EASTException> factory;

    /** Analyseur de DOM en AST. Les noeuds de l'AST sont créés par la
     * fabrique spécifiée à la construction de l'EASTParser. Même les
     * exceptions sont signalées par cette même fabrique.
     */

    public EAST parse (final Node n)
    throws EASTException {
        try {
            switch ( n.getNodeType() ) {

            case Node.DOCUMENT_NODE: {
                final Document d = (Document) n;
                return this.parse(d.getDocumentElement());
            }

            case Node.ELEMENT_NODE: {
                final Element e = (Element) n;
                final NodeList nl = e.getChildNodes();
                final String name = e.getTagName();
                switch (name) {
                case "programme1": {
                    return factory.newProgram(parseList(nl));
                } 
                case "alternative": {
                    final EAST cond = findThenParseChild(nl, "condition");
                    final EAST conseq =
                        findThenParseChildAsInstructions(nl, "consequence");
                    try {
                        final EAST alt =
                            findThenParseChildAsInstructions(nl, "alternant");
                        return factory.newAlternative(cond, conseq, alt);
                    } catch (EASTException exc) {
                        return factory.newAlternative(cond, conseq);
                    }
                } 
                case "sequence": {
                    return factory.newSequence(this.parseList(nl));
                } 
                case "blocUnaire": {
                    final EASTvariable var =
                        (EASTvariable) findThenParseChild(nl, "variable");
                    final EAST init = findThenParseChild(nl, "valeur");
                    final EASTsequence body =
                        (EASTsequence) findThenParseChild(nl, "corps");
                    return factory.newUnaryBlock(var, init, body);
                } 
                case "variable": {
                    final String nick = e.getAttribute("nom");
                    return factory.newVariable(nick);
                } 
                case "invocationPrimitive": {
                    final String op = e.getAttribute("fonction");
                    final List<EAST> args = parseList(nl);
                    return factory.newInvocation(op, args);
                } 
                case "operationUnaire": {
                    final String op = e.getAttribute("operateur");
                    final EAST rand = findThenParseChild(nl, "operande");
                    return factory.newUnaryOperation(op, rand);
                } 
                case "operationBinaire": {
                    final String op = e.getAttribute("operateur");
                    final EAST gauche = findThenParseChild(nl, "operandeGauche");
                    final EAST droite = findThenParseChild(nl, "operandeDroit");
                    return factory.newBinaryOperation(op, gauche, droite);
                } 
                case "entier": {
                    return factory.newIntegerConstant(e.getAttribute("valeur"));
                } 
                case "flottant": {
                    return factory.newFloatConstant(e.getAttribute("valeur"));
                } 
                case "chaine": {
                    final String text = n.getTextContent();
                    //final String text = this.extractText(e);
                    return factory.newStringConstant(text);
                } 
                case "booleen": {
                    return factory.newBooleanConstant(e.getAttribute("valeur"));
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
                    return factory.newSequence(this.parseList(nl));
                } 
                default: {
                    final String msg = "Unknown element name: " + name;
                    return factory.throwParseException(msg);
                }
            }
            }

            default: {
                final String msg = "Unknown node type: " + n.getNodeName();
                return factory.throwParseException(msg);
            }
            }
        } catch (final EASTException e) {
            throw e;
        } catch (final Exception e) {
            throw new EASTException(e);
        }
    }

    /** Analyser une séquence d'éléments pour en faire un ASTlist
     * c'est-à-dire une séquence d'AST.
     */

    public List<EAST> parseList (final NodeList nl)
    throws EASTException {
        final List<EAST> result = new Vector<>();
        final int n = nl.getLength();
        LOOP:
            for ( int i = 0 ; i<n ; i++ ) {
                final Node nd = nl.item(i);
                switch ( nd.getNodeType() ) {

                case Node.ELEMENT_NODE: {
                    final EAST p = this.parse(nd);
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
     * un AST.  */

    public EAST findThenParseChild (final NodeList nl, final String childName)
    throws EASTException {
        EAST result = null;
        final int n = nl.getLength();
        for ( int i = 0 ; i<n ; i++ ) {
            final Node nd = nl.item(i);
            switch ( nd.getNodeType() ) {

            case Node.ELEMENT_NODE: {
                final Element e = (Element) nd;
                if ( childName.equals(e.getTagName()) ) {
                    if ( result == null ) {
                        result = this.parse(e);
                    } else {
                        final String msg = "Non unique child with name " + childName;
                        return factory.throwParseException(msg);
                    }
                }
                break;
            }

            default: {
                // On ignore tout ce qui n'est pas élément XML:
            }
            }
        }
        if ( result == null ) {
            final String msg = "No such child element " + childName;
            return factory.throwParseException(msg);
        }
        return result;
    }

    /** Trouver un élément d'après son nom et analyser son contenu pour
     * en faire un ASTsequence.
     *
     * @throws ILPException
     *   lorsqu'un tel élément n'est pas trouvé.
     */

    public EAST findThenParseChildAsInstructions (
            final NodeList nl,
            final String childName)
    throws EASTException {
        int n = nl.getLength();
        for ( int i = 0 ; i<n ; i++ ) {
            Node nd = nl.item(i);
            switch ( nd.getNodeType() ) {

            case Node.ELEMENT_NODE: {
                Element e = (Element) nd;
                if ( childName.equals(e.getTagName()) ) {
                    return factory.newSequence(this.parseList(e.getChildNodes()));
                }
                break;
            }

            default: {
                // On ignore tout ce qui n'est pas élément XML:
            }
            }
        }
        String msg = "No such child element " + childName;
        return factory.throwParseException(msg);
    }

    /** Analyser une suite comportant un unique élément.  */

    public EAST parseUniqueChild (final NodeList nl)
    throws EASTException {
        EAST result = null;
        final int n = nl.getLength();
        for ( int i = 0 ; i<n ; i++ ) {
            final Node nd = nl.item(i);
            switch ( nd.getNodeType() ) {

            case Node.ELEMENT_NODE: {
                final Element e = (Element) nd;
                if ( result == null ) {
                    result = this.parse(e);
                } else {
                    final String msg = "Non unique child";
                    return factory.throwParseException(msg);
                }
                break;
            }

            default: {
                // On ignore tout ce qui n'est pas élément XML:
            }
            }
        }
        if ( result == null ) {
            factory.throwParseException("No child at all");
        }
        return result;
    }

}

//end of EASTParser.java
