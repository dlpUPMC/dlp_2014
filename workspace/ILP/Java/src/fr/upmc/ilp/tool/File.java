/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: File.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.tool;

import java.io.*;

public class File extends java.io.File
implements IContent {

    static final long serialVersionUID = +12345678900021000L;

    public File (String filename) {
        super(filename);
    }

    public File (java.io.File file) {
        super(file.getAbsolutePath());
    }

    /** Calculer le nom de base (sans repertoire ni suffixe). */

    public String getBaseName () {
        String name = getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex >= 0) {
            return name.substring(0, dotIndex);
        } else {
            return name;
        }
    }

    /** Calculer le nom complet du fichier hors son suffixe. */

    public String getNameWithoutSuffix () {
        return getParent() + File.separator + getBaseName();
    }

    /** Fournir le contenu d'un fichier sous la forme d'une chaine de
     * caracteres. */

    public String getContent() {
      try {
        return FileTool.slurpFile(this);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    /** Cette methode statique est utile pour recuperer le contenu d'une
     * instance de java.io.File. */

    public static String getContent (File file) {
        try {
          return FileTool.slurpFile(file);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
    }

}
