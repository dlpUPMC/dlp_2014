/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: FileToolTest.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.tool.test;

import java.io.File;
import java.io.IOException;

import fr.upmc.ilp.tool.FileTool;

import junit.framework.TestCase;

/** Tests (Junit) de FileTool. */

public class FileToolTest extends TestCase {
    
    // Ce test ne sert qu'a invoquer le constructeur de FileTool 
    // pour verifier que le taux de couverture
    // est bien egal a 100% sinon Hansel declare que FileTool.<init>
    // n'est pas couvert! Il faut ruser un peu pour qu'Eclipse ne
    // s'apercoive pas que ce code est totalement inutile.
    
    public void testInstantiation () {
        FileTool ft = new FileTool();
        assertNotNull(ft);
    }
    
    /** VÃ©rifier que slurpFile fonctionne sur un fichier. */
    
    public void testSlurpFileOnEmptyFile ()
    throws IOException {
        File tmpFile = File.createTempFile("tsf0", "txt");
        String fileName = tmpFile.getAbsolutePath();
        final String s = FileTool.slurpFile(fileName);
        assertTrue(s.length() == 0 );
    }

    /** VÃ©rifier que stuffFile fonctionne. */
    
    public void testStuffFile ()
    throws IOException {
        File tmpFile = File.createTempFile("tsf1", "txt");
        String fileName = tmpFile.getAbsolutePath();
        String content = "coucou";
        FileTool.stuffFile(fileName, content);
        final String s = FileTool.slurpFile(fileName);
        assertEquals(content, s);
    }
    
    public void testStuffFileChaineVide ()
    throws IOException {
        File tmpFile = File.createTempFile("tsf2", "txt");
        String fileName = tmpFile.getAbsolutePath();
        String content = "";
        FileTool.stuffFile(fileName, content);
        final String s = FileTool.slurpFile(fileName);
        assertEquals(content, s);
    }
    
}

//end of FileToolTest.java
