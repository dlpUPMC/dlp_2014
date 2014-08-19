/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: IAST4visitor.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version=2
 *
 * D'apr�s une suggestion d'Aur�lien Moreau <aurelien.moreau@yienyien.net>
 * ******************************************************************/

package fr.upmc.ilp.ilp4.interfaces;

/** Un visiteur d'IAST procure une methode pour traiter chaque type
 * de noeud present dans un IAST4.
 *
 *  NOTE: Autant que faire se peut, on utilise des interfaces mais toutes les
 *  classes ne sont pas cachables derriere des IAST* (while par
 *  exemple) et certaines classes raffinent une meme interface (les
 *  variables et les constantes par exemple).
 */

public interface IAST4visitor<Data, Result, Exc extends Throwable> {
    // visiter les expressions:
    Result visit (IAST4alternative iast, Data data) throws Exc;
    Result visit (IAST4assignment iast, Data data) throws Exc;
    Result visit (IAST4localAssignment iast, Data data) throws Exc;
    Result visit (IAST4globalAssignment iast, Data data) throws Exc;
    Result visit (IAST4constant iast, Data data) throws Exc;
    Result visit (IAST4invocation iast, Data data) throws Exc;
    Result visit (IAST4globalInvocation iast, Data data) throws Exc;
    Result visit (IAST4computedInvocation iast, Data data) throws Exc;
    Result visit (IAST4primitiveInvocation iast, Data data) throws Exc;
    Result visit (IAST4functionDefinition iast, Data data) throws Exc;
    Result visit (IAST4localBlock iast, Data data) throws Exc;
    Result visit (IAST4reference iast, Data data) throws Exc;
    Result visit (IAST4unaryBlock iast, Data data) throws Exc;
    Result visit (IAST4binaryOperation iast, Data data) throws Exc;
    Result visit (IAST4unaryOperation iast, Data data) throws Exc;
    Result visit (IAST4program iast, Data data) throws Exc;
    Result visit (IAST4sequence iast, Data data) throws Exc;
    Result visit (IAST4try iast, Data data) throws Exc;
    Result visit (IAST4while iast, Data data) throws Exc;
    // et aussi les variables referencees:
    Result visit (IAST4variable iast, Data data) throws Exc;

}
