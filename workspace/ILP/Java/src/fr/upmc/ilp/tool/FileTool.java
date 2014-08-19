/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: FileTool.java 801 2009-09-07 15:30:20Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

/** Utilitaire pour lire ou écrire des fichiers entiers. */

public final class FileTool {
    
    public FileTool () { 
        super();
    }
    
    /** Convertir un fichier en une chaîne de caracteres. 
     * @return le contenu du fichier lu
     * @param  file le fichier a lire
     */
    
    public static String slurpFile (final File file)
            throws IOException {
        final int length = (int) file.length();
        final char[] buffer = new char[length];
        try (final FileReader fr = new FileReader(file)) {
            int offset = 0;
            while ( offset < length ) {
                int read = fr.read(buffer, offset, length-offset);
                offset += read;
            }
        }
        return new String(buffer);
    }
    
    public static String slurpFile (final Reader fr)
    throws IOException {
        final StringBuffer sb = new StringBuffer();
        final BufferedReader br = new BufferedReader(fr);
        final char[] buffer = new char[4096];
        while ( true ) {
            int count = br.read(buffer);
            if (count < 0) {
                return sb.toString();
            }
            sb.append(buffer, 0, count);
        }
    }

    /** Convertir un fichier en une chaîne de caracteres. 
     * @return le contenu du fichier lu
     * @param filename le nom du fichier a lire
     */
    
    public static String slurpFile (final String filename)
    throws IOException {
        return FileTool.slurpFile(new File(filename));
    }

    /** Lire un fichier correspondant au résultat attendu d'un programme ILP.
     * Cette methode est utile pour tester ILP car elle retire les 
     * separateurs (blancs et retours a la ligne) superflus ce qui permet
     * de compenser les differences d'impression entre Scheme, Java et C. */

    public static String readExpectedResult (String basefilename)
      throws IOException {
      File resultFile = new File(basefilename + ".result");
      return FileTool.slurpFile(resultFile).trim();
    }

    public static String readExpectedResult (fr.upmc.ilp.tool.File file)
      throws IOException {
      File resultFile = new File(file.getNameWithoutSuffix() + ".result");
      return FileTool.slurpFile(resultFile).trim();
    }
  
    // TODO enlever les trim() en trop dans les CompilerTest

    /** Lire le fichier correspondant aux impressions attendues d'un 
     * programme ILP. Cette methode est utile pour tester ILP car elle 
     * retire les separateurs (blancs et retours a la ligne) superflus 
     * ce qui permet de compenser les differences d'impression entre Scheme, 
     * Java et C. */

    public static String readExpectedPrinting (String basefilename)
      throws IOException {
      File resultFile = new File(basefilename + ".print");
      return FileTool.slurpFile(resultFile).trim();
    }

    public static String readExpectedPrinting (fr.upmc.ilp.tool.File file)
      throws IOException {
      File resultFile = new File(file.getNameWithoutSuffix() + ".print");
      return FileTool.slurpFile(resultFile).trim();
    }
  
    /** Écrire une chaîne dans un fichier.
     * @param filename le nom du fichier a ecrire
     * @param s le nouveau contenu du fichier
     */
    
    public static void stuffFile (final String filename, final String s)
    throws IOException {
        FileTool.stuffFile(new File(filename), s);
    }

    /** Écrire une chaîne dans un fichier.
     * @param file le fichier a ecrire
     * @param s le nouveau contenu du fichier
     */
    
    public static void stuffFile (final File file, final String s)
    throws IOException {
        //final FileWriter fw = new FileWriter(file);
        //try {
        //    fw.write(s, 0, s.length());
        //} finally {
        //    fw.close();
        //}
        // Nouvelle écriture pour Java7:
        try (final FileWriter fw = new FileWriter(file)) {
            fw.write(s, 0, s.length());
        }
    }
}

//end of FileTool.java
