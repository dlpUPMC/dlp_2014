package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.annotation.OrNull;

/** Beaucoup d'environnements associent des variables a des valeurs.
 * Cet environnement contient les fonctions primordiales qu'on
 * attend d'eux. Cet environnement n'est fondamentalement qu'une 
 * simple liste chainée. */

public interface IEnvironment<V> {

    /** Retourne le premier maillon concernant la variable (s'il existe) */
    @OrNull IEnvironment<V> shrink (V variable);

    /** Vérifie qu'une variable est présente dans l'environnement. */
    boolean isPresent (V variable);
    // isPresent s'implante facilement avec shrink!
    
    // Introspection des environnements

    /** L'environnement est-il vide ? */
    boolean isEmpty ();

    /** Si l'environnement n'est pas vide, rendre le maillon suivant. */
    IEnvironment<V> getNext ();

    /** Quelle est la variable concernée par le maillon courant. */
    V getVariable ();
}
