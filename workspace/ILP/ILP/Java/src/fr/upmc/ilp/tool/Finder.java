/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2011 <Christian.Queinnec@lip6.fr>
 * $Id: File.java 735 2011-07-27 16:38:19Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.tool;

/** Implantation d'IFinder, une sorte de mécanisme de PATH listant une suite
 * de répertoires où chercher des fichiers. */

import java.io.IOException;
import java.util.Vector;
import java.io.File;

public class Finder implements IFinder {
    
    private final boolean verbose = false; // DEBUG
    
    public Finder () {
        this.directories = new Vector<>();
    }
    private final Vector<File> directories;
    
    @Override
    public String toString () {
        StringBuffer sb = new StringBuffer();
        sb.append("Finder[");
        for ( File d : directories ) {
            sb.append(d + ",");
        }
        sb.append("]");
        return sb.toString();
    }

    public File findFile (final String baseFileName) 
    throws IOException {
        File file = new File(baseFileName);
        if ( file.isAbsolute() ) {
            if ( file.exists() ) {
                if ( verbose ) {
                    System.err.println("Found " + file.getAbsolutePath());
                }
                return file;
            }
            throw new IOException("Inexistent absolute file");
        }
        for ( File dir : directories ) {
            if ( verbose ) {
                System.err.println("Searching " + dir.getAbsolutePath());
            }
            final String fileName = dir.getAbsolutePath()
                    + File.separator + baseFileName;
            file = new File(fileName);
            if ( file.exists() ) {
                if ( verbose ) {
                    System.err.println("Found " + file.getAbsolutePath());
                }
                return file;
            }
        }
        throw new IOException("Cannot find file " + baseFileName);
    }

    public void addPath (final File directory)
    throws IOException {
        if ( ! directory.isDirectory() ) {
            throw new IOException("Not a directory: "
               + directory.getAbsolutePath()
               + " !");        
        }
        if ( ! directories.contains(directory) ) {
            directories.add(directory);
        }
    }

    public void addPath (String directoryName)
    throws IOException {
        directoryName = directoryName.replaceAll("/", File.separator);
        final File directory = new File(directoryName);
        addPath(directory);
    }

    public void addPossiblePath (String directoryName) {
        try {
            addPath(directoryName);
        } catch (IOException e) {
            // Ce n'est pas grave!
        }
    }

    public java.io.File[] getPaths () {
        return directories.toArray(new File[0]);
    }

    public void setPaths (final File[] paths) throws IOException {
        directories.removeAllElements();
        for ( File dir : paths ) {
            addPath(dir);
        }
    }
}
