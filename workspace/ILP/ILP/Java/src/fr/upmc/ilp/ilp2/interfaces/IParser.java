/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: IParser.java 505 2006-10-11 06:58:35Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.interfaces;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * L'interface des analyseurs syntaxiques devant analyser un DOM et le
 * convertir en un AST. Toutes ces methodes peuvent signaler des exceptions
 * provenant de la conversion.
 */

public interface IParser<Exc extends Exception> {

    /** Rendre la fabrique associee a cet analyseur. */

    IAST2Factory<Exc> getFactory ();

    /** Analyser un noeud de DOM et le convertir en un AST. */

    IAST2program<Exc> parse (Document n) throws Exc;
    IAST2<Exc> parse (Node n) throws Exc;

    /** Analyser une suite de noeuds en une suite d'AST. */

    List<IAST2<Exc>> parseList (NodeList nl) throws Exc;
    // FUTUR: changer plutot pour IAST2[] ?

    /** Trouver un sous-noeud de nom donné et le convertir en un AST.
     * On peut fournir un noeud ou une suite de noeuds. */

    IAST2<Exc> findThenParseChild (NodeList nl, String childName)
    throws Exc;
    IAST2<Exc> findThenParseChild (Node n, String childName)
    throws Exc;

    /** Trouver un sous-noeud donné et convertir ses fils en une suite d'AST.
     * On peut fournir un noeud ou une suite de noeuds. */

    List<IAST2<Exc>> findThenParseChildAsList (NodeList nl, String childName)
    throws Exc;
    List<IAST2<Exc>> findThenParseChildAsList (Node n, String childName)
    throws Exc;

    /** Trouver un sous-noeud donné et convertir ses fils en une séquence
     * d'instructions.
     * On peut fournir un noeud ou une suite de noeuds. */

    IAST2sequence<Exc> findThenParseChildAsSequence (NodeList nl, String childName)
    throws Exc;
    IAST2sequence<Exc> findThenParseChildAsSequence (Node n, String childName)
    throws Exc;

    /** Faire une sequence d'une suite de noeuds. */

    IAST2sequence<Exc> parseChildrenAsSequence (NodeList nl)
    throws Exc;

    /** Trouver un sous-noeud donné et convertir son unique fils en un AST.
     * On peut fournir un noeud ou une suite de noeuds.*/

    IAST2<Exc> findThenParseChildAsUnique (Node n, String childName)
    throws Exc;
    IAST2<Exc> findThenParseChildAsUnique (NodeList nl, String childName)
    throws Exc;
}

// end of IParser.java
