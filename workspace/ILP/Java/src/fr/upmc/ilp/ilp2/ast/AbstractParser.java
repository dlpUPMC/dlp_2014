/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: AbstractParser.java 1243 2012-09-16 08:01:36Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp2.interfaces.IAST2Factory;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2sequence;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** Une classe abstraite contenant quelques utilitaires communs aux différents
 * types d'analyseurs syntaxiques d'ILP2 et de ses extensions. */

public abstract class AbstractParser
implements IParser<CEASTparseException> {

    public AbstractParser (final IAST2Factory<CEASTparseException> factory) {
        this.factory = factory;
    }
    protected final IAST2Factory<CEASTparseException> factory;

    public IAST2Factory<CEASTparseException> getFactory () {
        return this.factory;
    }

    public IAST2<CEASTparseException> parseAsExpression (final NodeList nl)
    throws CEASTparseException {
        if ( 1 == nl.getLength() ) {
            return this.parse(nl.item(0));
        } else {
            final String msg = "Should contain a single DOM node!";
            throw new CEASTparseException(msg);
        }
    }

    /** Trouver un élément d'après son nom et l'analyser pour en faire
     * un CEAST.
     *
     * @throws CEASTparseException
     *   lorsqu'un tel élément n'est pas trouvé.
     */

    public IAST2<CEASTparseException> 
    findThenParseChild (final Node n, final String childName)
    throws CEASTparseException {
        return findThenParseChild(n.getChildNodes(), childName);
    }

    public IAST2<CEASTparseException> 
    findThenParseChild (final NodeList nl, final String childName)
    throws CEASTparseException {
        final int n = nl.getLength();
        for ( int i = 0 ; i<n ; i++ ) {
            final Node nd = nl.item(i);
            switch ( nd.getNodeType() ) {

            case Node.ELEMENT_NODE: {
                final Element e = (Element) nd;
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
        final String msg = "No such child element " + childName;
        throw new CEASTparseException(msg);
    }

    /** Trouver un élément d'après son nom et analyser son contenu pour
     * en faire une liste de CEAST.
     *
     * @throws CEASTparseException
     *   lorsqu'un tel élément n'est pas trouvé.
     */

    public List<IAST2<CEASTparseException>> findThenParseChildAsList (
            final Node n, final String childName)
    throws CEASTparseException {
        return findThenParseChildAsList(n.getChildNodes(), childName);
    }

    /** Trouver un certain élément d'après son nom et analyser son contenu pour
     * en faire une liste de CEAST. 
     *
     * @throws CEASTparseException
     *   lorsqu'un tel élément n'est pas trouvé. 
     */
    
    public List<IAST2<CEASTparseException>> findThenParseChildAsList (
            final NodeList nl, final String childName)
    throws CEASTparseException {
        final int n = nl.getLength();
        for ( int i = 0 ; i<n ; i++ ) {
            final Node nd = nl.item(i);
            switch ( nd.getNodeType() ) {

            case Node.ELEMENT_NODE: {
                final Element e = (Element) nd;
                if ( childName.equals(e.getTagName()) ) {
                    return this.parseList(e.getChildNodes());
                }
                break;
            }

            default: {
                // On ignore tout ce qui n'est pas élément XML:
            }
            }
        }
        final String msg = "No such node " + childName;
        throw new CEASTparseException(msg);
    }

    /** Trouver un sous-noeud d'un certain type et analyser son unique
     * fils comme une unique entité */

    public IAST2<CEASTparseException> findThenParseChildAsUnique (
            final Node n,
            final String childName)
            throws CEASTparseException {
        return findThenParseChildAsUnique(n.getChildNodes(), childName);
    }

    /** Trouver un certain sous-noeud d'un certain type et analyser son unique
     * fils comme une unique entité */

    public IAST2<CEASTparseException> findThenParseChildAsUnique (
            final NodeList nl,
            final String childName)
            throws CEASTparseException {
        final List<IAST2<CEASTparseException>> results =
            findThenParseChildAsList(nl, childName);
        if ( 1 == results.size() ) {
            return results.get(0);
        } else {
            final String msg = "Should be an unique DOM node!";
            throw new CEASTparseException(msg);
        }
    }

    /** Analyser une séquence d'élément pour en faire une suite de CEAST.
     * Cette fonction sert à lire les
     * instructions d'une séquence, les arguments d'une invocation, les
     * variables d'une définition de fonction, etc.
     *
     * @throws CEASTparseException
     *   lorsque l'analyse d'un sous-noeud provoque une exception.
     */

    public List<IAST2<CEASTparseException>> parseList (NodeList nl)
    throws CEASTparseException {
        final List<IAST2<CEASTparseException>> result = new Vector<>();
        final int n = nl.getLength();
        LOOP:
            for ( int i = 0 ; i<n ; i++ ) {
                final Node nd = nl.item(i);
                switch ( nd.getNodeType() ) {

                case Node.ELEMENT_NODE: {
                    final IAST2<CEASTparseException> p = this.parse(nd);
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

    /** Trouver un sous-noeud donné et convertir ses fils en une séquence
     * d'instructions. */

    public IAST2sequence<CEASTparseException> findThenParseChildAsSequence (
            NodeList nl, String childName)
    throws CEASTparseException {
        List<IAST2<CEASTparseException>> li =
            findThenParseChildAsList(nl, childName);
        List<IAST2instruction<CEASTparseException>> lins = new Vector<>();
        for (IAST2<CEASTparseException> i : li) {
            lins.add((IAST2instruction<CEASTparseException>) i);
        }
        return getFactory().newSequence(lins);
    }

    /** Trouver un certain sous-noeud et convertir ses fils en une séquence
     * d'instructions. */

    public IAST2sequence<CEASTparseException> findThenParseChildAsSequence (
            Node n, String childName)
    throws CEASTparseException {
        return findThenParseChildAsSequence(n.getChildNodes(), childName);
    }
    
    /** Réunir tous les sous-noeuds en une séquence. */

    public IAST2sequence<CEASTparseException> parseChildrenAsSequence (
            NodeList nl)
    throws CEASTparseException {
        List<IAST2<CEASTparseException>> li = parseList(nl);
        List<IAST2instruction<CEASTparseException>> lins = new Vector<>();
        for (IAST2<CEASTparseException> i : li) {
            lins.add((IAST2instruction<CEASTparseException>) i);
        }
        return getFactory().newSequence(lins);
    }

}
