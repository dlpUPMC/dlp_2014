/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2011 <Christian.Queinnec@lip6.fr>
 * $Id: File.java 735 2011-07-27 16:38:19Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.tool;

import java.io.File;
import java.io.IOException;

/**
 * Cet utilitaire contient une liste de répertoires où chercher un
 * fichier. On peut dynamiquement changer la liste des répertoires où
 * chercher. En cas d'anomalie, il signale des exceptions.
 * 
 * D'abord, on crée un Finder, on l'enrichit avec des répertoires (un 
 * par un ou en bloc) puis on recherche des fichiers avec ce Finder.
 */

public interface IFinder {

    /** Chercher un fichier par son petit nom dans les répertoires 
     * associés à ce Finder. Signale une exception si l'on ne le trouve
     * nulle part. */
    public File findFile (String baseFileName) throws IOException;
    
    /** Ajouter un répertoire à la fin de la liste des répertoires 
     * où chercher. Le répertoire doit être un vrai répertoire sinon
     * une exception est signalée. */
    public void addPath (File directory) throws IOException;
    /** Ajouter un répertoire à la fin de la liste des répertoires 
     * où chercher. Le répertoire doit être un vrai répertoire sinon
     * une exception est signalée. Le répertoire est ici spécifié par
     * une chaîne de caractères en convention URL (avec des slashes). */
    public void addPath (String directory) throws IOException;
    /** AJouter un répertoire mais seulement si c'est bien un répertoire. */
    public void addPossiblePath (String directory);
        
    /** Renvoyer la liste des répertoires où chercher. */
    public java.io.File[] getPaths ();
    
    /** Imposer la liste des répertoires où chercher. Tous doivent être
     * des vrais répertoires autrement une exception est signalée. */
    public void setPaths (File[] paths) throws IOException;
}
