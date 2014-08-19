/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: IContent.java 801 2009-09-07 15:30:20Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.tool;

import java.io.IOException;

/** Une interface fournissant un contenu à savoir un programme ILP.
 * Cette interface sert le plus souvent à masquer un fichier mais ce
 * pourrait être également une chaîne, une URL ou un générateur de
 * tests. */

public interface IContent {

    /** Fournir la chaîne de caractères représentant le programme ILP
     * auquel on s'intéresse.
     *
     * @return String
     */
    String getContent () throws IOException;

}
