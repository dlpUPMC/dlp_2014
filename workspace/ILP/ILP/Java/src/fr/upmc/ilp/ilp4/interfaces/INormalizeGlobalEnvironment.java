/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: INormalizeGlobalEnvironment.java 934 2010-08-21 14:22:22Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.annotation.OrNull;

/** Normaliser les variables globales veut dire utiliser un unique
 * objet pour toutes les références à une variable globale. Il est
 * ainsi possible de partager simplement de l'information sur cette
 * variable globale depuis tous les endroits où elle est référencée.
 */
public interface INormalizeGlobalEnvironment {
    
    /** Étendre l'environnement global avec une nouvelle variable. */
    void add(IAST4globalVariable variable);

    /** Vérifie qu'une variable est présente dans l'environnement
     * global. Si elle est présente, elle est renvoyée en résultat
     * autrement null est renvoyé. */
    @OrNull IAST4globalVariable isPresent (IAST4variable variable);

    /** Ajouter une primitive à l'environnement global. */
    void addPrimitive (IAST4globalVariable variable);

    /** Vérifie qu'une variable correspond au nom d'une primitive. Si
     * elle est présente, elle est renvoyée en résultat autrement null
     * est renvoyé. */
    @OrNull IAST4globalVariable isPrimitive (IAST4variable variable);
}

// end of INormalizeGlobalEnvironment.java
