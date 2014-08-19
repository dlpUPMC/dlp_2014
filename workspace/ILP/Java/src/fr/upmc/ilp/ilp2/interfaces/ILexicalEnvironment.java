/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ILexicalEnvironment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;

/** Cette interface définit un environnement lexical pour une
 * évaluation. Un environnement est une structure de données présente
 * à l'exécution et contenant une suite de couples (on dit « liaison »)
 * (variable, valeur) de cette variable. */

public interface ILexicalEnvironment
extends IEnvironment<IAST2variable> {
    
    /** Un peu de covariance. */
    ILexicalEnvironment getNext ();
    @OrNull ILexicalEnvironment shrink (IAST2variable variable);
    
    Object lookup (IAST2variable variable)
    throws EvaluationException;

    /** Met à jour l'environnement d'interprétation en associant une
     * nouvelle valeur à une variable.
     *
     * @throws EvaluationException si la variable est absente.
     */

    void update (IAST2variable variable, Object value)
    throws EvaluationException;

    /** Étend l'environnement avec un nouveau couple variable-valeur. */
    ILexicalEnvironment extend (IAST2variable variable, Object value);
}

// end of ILexicalEnvironment.java
