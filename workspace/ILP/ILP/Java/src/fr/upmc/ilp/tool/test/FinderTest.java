/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2011 <Christian.Queinnec@lip6.fr>
 * $Id: File.java 735 2011-07-27 16:38:19Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.tool.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.Finder;
import fr.upmc.ilp.tool.IFinder;

public class FinderTest {

    @Test
    public void testInstantiation() {
        final IFinder finder = new Finder();
        assertTrue(finder != null);
    }
    
    @Test(expected = IOException.class)
    public void findNothing () throws IOException {
        final IFinder finder = new Finder();
        finder.findFile("laksdjlajsd");
        fail("Should not appear!");
    }
    
    @Test
    public void addHomeDirectory () throws IOException {
        final IFinder finder = new Finder();
        String homeDir = System.getProperty("user.dir");
        assertTrue(homeDir != null);
        java.io.File dir = new java.io.File(homeDir);
        finder.addPath(dir);
        assertTrue(finder.getPaths().length == 1);
    }
    
    @Test(expected = IOException.class)
    public void addBadDirectory () throws IOException {
        final IFinder finder = new Finder();
        String homeDir = System.getProperty("user.dir");
        assertTrue(homeDir != null);
        String unexistentDir = homeDir + File.separator + "lajdsla";
        java.io.File dir = new java.io.File(unexistentDir);
        finder.addPath(dir);
    }
    
    @Test
    public void find1inTmp () throws IOException {
        final IFinder finder = new Finder();
        String tmpDir = System.getProperty("java.io.tmpdir");
        assertTrue(tmpDir != null);
        java.io.File tmpdir = new java.io.File(tmpDir);
        finder.addPath(tmpdir);
        assertTrue(finder.getPaths().length == 1);
        java.io.File tmpFile = new java.io.File(
                tmpDir + File.separator + "ilpAA");
        tmpFile.delete();
        assertTrue(tmpFile.createNewFile());
        java.io.File foundFile = finder.findFile(tmpFile.getName());
        assert(foundFile != null);
        assert(foundFile.getAbsolutePath().equals(tmpFile.getAbsolutePath()));
    }
    
    @Test
    public void find1in2Tmp () throws IOException {
        final IFinder finder = new Finder();
        String homeDir = System.getProperty("user.dir");
        assertTrue(homeDir != null);
        java.io.File dir = new java.io.File(homeDir);
        finder.addPath(dir);
        String tmpDir = System.getProperty("java.io.tmpdir");
        assertTrue(tmpDir != null);
        java.io.File tmpdir = new java.io.File(tmpDir);
        finder.addPath(tmpdir);
        assertTrue(finder.getPaths().length == 2);
        java.io.File tmpFile = new java.io.File(
                tmpDir + File.separator + "ilpBB");
        tmpFile.delete();
        assertTrue(tmpFile.createNewFile());
        java.io.File foundFile = finder.findFile(tmpFile.getName());
        assert(foundFile != null);
        assert(foundFile.getAbsolutePath().equals(tmpFile.getAbsolutePath()));
    }
    
    @Test
    public void addPaths () throws IOException {
        final IFinder finder = new Finder();
        String homeDir = System.getProperty("user.dir");
        String tmpDir = System.getProperty("java.io.tmpdir");
        finder.setPaths(new java.io.File[]{
                new java.io.File(homeDir),
                new java.io.File(tmpDir)
        });
        assertTrue(finder.getPaths().length == 2);
        java.io.File tmpFile = new java.io.File(
                tmpDir + File.separator + "ilpBB");
        tmpFile.delete();
        assertTrue(tmpFile.createNewFile());
        java.io.File foundFile = finder.findFile(tmpFile.getName());
        assert(foundFile != null);
        assert(foundFile.getAbsolutePath().equals(tmpFile.getAbsolutePath()));
    }

}
