/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: INormalizeGlobalEnvironment.java 872 2009-10-26 10:31:31Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.interfaces;

/** Normaliser les variables globales veut dire utiliser un unique
 * objet pour toutes les références à une variable globale. Il est
 * ainsi possible de partager simplement de l'information sur cette
 * variable globale depuis tous les endroits où elle est référencée.
 */

public interface INormalizeGlobalEnvironment 
extends fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment,
        IClassEnvironment {
}

// end of INormalizeGlobalEnvironment.java
