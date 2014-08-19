/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IAST4variable.java 936 2010-08-21 14:47:11Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp3.jsgen;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;

/** L'interface décrivant les divers types de variables. Parmi elles
 * se trouvent les variables globales, prédéfinies et locales. */

public interface IAST3variable
extends IAST2variable {

    /** Valeur d'une variable. */

    Object eval (ILexicalEnvironment lexenv, ICommon common)
        throws EvaluationException;

    /** Normaliser l'AST et notamment alpha-convertir les variables. */
}

// end of IAST4variable.java
