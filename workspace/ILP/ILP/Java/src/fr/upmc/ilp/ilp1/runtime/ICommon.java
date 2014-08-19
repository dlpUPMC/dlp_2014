/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ICommon.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;

/**
 * Cette interface définit les caractéristiques globales d'un interprète du
 * langage ILP1. On y trouve, notamment, la définition des opérateurs du
 * langage.
 */

public interface ICommon {

    /** Appliquer un opérateur unaire sur un opérande. */
    Object applyOperator(String opName, Object operand)
            throws EvaluationException;

    /** Appliquer un opérateur binaire sur deux opérandes. */
    Object applyOperator(String opName, Object leftOperand, Object rightOperand)
            throws EvaluationException;

}

// end of ICommon.java
