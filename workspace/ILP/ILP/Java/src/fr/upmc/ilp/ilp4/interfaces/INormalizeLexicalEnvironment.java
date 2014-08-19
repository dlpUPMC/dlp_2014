/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: INormalizeLexicalEnvironment.java 869 2009-10-23 16:48:43Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.interfaces;

public interface INormalizeLexicalEnvironment {
    
    /** Étend l'environnement avec une nouvelle variable. */
    INormalizeLexicalEnvironment extend(IAST4variable variable);

    /** Vérifie qu'une variable est présente dans le seul environnement
     * lexical. Si elle est présente, elle est renvoyée en résultat
     * autrement null est renvoyé. */
    IAST4variable isPresent (IAST4variable variable);
}

// end of INormalizeLexicalEnvironment.java
