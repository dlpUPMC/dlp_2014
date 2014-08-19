/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:IAST4.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.interfaces;

import java.util.Set;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp4.ast.FindingInvokedFunctionsException;
import fr.upmc.ilp.ilp4.ast.InliningException;

/** L'interface generaliste des AST à la fois interprétables et compilables.
 */

public interface IAST4
extends IAST4visitable, IAST2<CEASTparseException> {

    /** Calculer le graphe d'appel c'est-à-dire pour chaque expression,
     * les fonctions globales qu'elle invoque. */

    void computeInvokedFunctions ()
    throws FindingInvokedFunctionsException;

    /** Renvoyer l'ensemble des fonctions globales invoquées (et
     * précédemment calculées). */

    Set<IAST4globalFunctionVariable> getInvokedFunctions ();

    /** Intégrer les fonctions non récursives. */

    void inline (IAST4Factory factory) throws InliningException;

    /** Accepter le passage d'un visiteur. */

    <Data, Result, Exc extends Throwable> Result accept (
            IAST4visitor<Data, Result, Exc> visitor, 
            Data data) throws Exc;

}

// end of IAST4.java
