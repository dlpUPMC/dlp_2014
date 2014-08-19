/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: ProgramCallerTest.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.tool.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.upmc.ilp.tool.ProgramCaller;

import junit.framework.TestCase;

/** Tests (Junit) de ProgramCaller. */

public class ProgramCallerTest extends TestCase {

    public void testProgramCallerInexistentVerbose () {
        final String programName = "lasdljsdfousadfl lsjd";
        ProgramCaller pc = new ProgramCaller(programName);
        assertNotNull(pc);
        pc.setVerbose();
        pc.run();
        assertTrue(pc.getExitValue() != 0);
    }

    public void testProgramCallerInexistent () {
        final String programName = "lasdljsdfousadfl lsjd";
        ProgramCaller pc = new ProgramCaller(programName);
        pc.run();
        final String result = pc.getStderr();
        System.err.println("Result: " + result); // DEBUG
        Pattern p = Pattern.compile(".*(not found|Cannot run).*");
        Matcher m = p.matcher(result);
        assertTrue(m.matches());
        assertTrue(pc.getExitValue() != 0);
    }

    public void testProgramCallerEchoVerbose () {
        final ProgramCaller pc = new ProgramCaller("echo cou cou  cou");
        assertNotNull(pc);
        pc.setVerbose();
        pc.run();
        assertTrue(pc.getExitValue() == 0);
        final String result = pc.getStdout();
        System.err.println("Result: " + result); // DEBUG
        assertNotNull(result);
        assertTrue(result.length() > 0);
        assertEquals(result.trim(), "cou cou cou");
    }

    public void testProgramCallerEcho () {
        final ProgramCaller pc = new ProgramCaller("echo cou cou  cou");
        assertNotNull(pc);
        pc.run();
        assertTrue(pc.getExitValue() == 0);
        final String result = pc.getStdout();
        System.err.println("Result: " + result); // DEBUG
        assertNotNull(result);
        assertTrue(result.length() > 0);
        assertEquals(result.trim(), "cou cou cou");
    }

    public void testProgramCallerGccOnStdout () {
        final ProgramCaller pc = new ProgramCaller("gcc -v");
        assertNotNull(pc);
        pc.run();
        assertTrue(pc.getExitValue() == 0);
        final String result = pc.getStdout();
        System.err.println("Result: " + result); // DEBUG
        assertNotNull(result);
        assertTrue(result.length() == 0);
        assertEquals(result.trim(), "");
    }

    public void testProgramCallerGccOnStderr () {
        final ProgramCaller pc = new ProgramCaller("gcc -c unexistent.c");
        assertNotNull(pc);
        pc.run();
        assertTrue(pc.getExitValue() != 0);
        final String errors = pc.getStderr();
        System.err.println("Errors: " + errors); // DEBUG
        assertNotNull(errors);
        assertTrue(errors.length() > 0);
        Pattern p = Pattern.compile(".*gcc.*", Pattern.DOTALL);
        Matcher m = p.matcher(errors);
        assertTrue(m.matches());
    }

    public void testProgramCallerGccOnStderrVerbose () {
        final ProgramCaller pc = new ProgramCaller("gcc -c unexistent.c");
        assertNotNull(pc);
        pc.setVerbose();
        pc.run();
        assertTrue(pc.getExitValue() != 0);
        final String errors = pc.getStderr();
        System.err.println("Errors: " + errors); // DEBUG
        assertNotNull(errors);
        assertTrue(errors.length() > 0);
        Pattern p = Pattern.compile(".*gcc.*", Pattern.DOTALL);
        Matcher m = p.matcher(errors);
        assertTrue(m.matches());
    }

    public void testProgramCallerVerbose () {
        final ProgramCaller pc = new ProgramCaller("echo Voir ce qui se passe");
        assertNotNull(pc);
        pc.setVerbose();
        pc.run();
        assertTrue(pc.getExitValue() == 0);
        final String result = pc.getStdout();
        System.err.println("Result: " + result); // DEBUG
        assertNotNull(result);
        assertTrue(result.length() > 0);
        Pattern p1 = Pattern.compile(".*Voir ce qui se passe.*");
        Matcher m1 = p1.matcher(result.trim());
        assertTrue(m1.matches());
        // verifier verbosite sur stderr FUTURE
        final String errors = pc.getStderr();
        System.err.println("Errors: " + errors); // DEBUG
        assertNotNull(errors);
        assertTrue(errors.length() == 0);
    }

    // test de 8 taches en parallele:

    public void testMultipleProgramCallers () {
        final int max = 8;
        final ProgramCaller[] pcs = new ProgramCaller[max];
        for ( int i = max ; i>0 ; i-- ) {
            pcs[i-1] = new ProgramCaller("sleep " + i);
            pcs[i-1].run();
        }
        for ( int i = 0 ; i<max ; i++ ) {
            assertTrue(pcs[i].getExitValue() == 0);
        }
    }

}

// end of ProgramCallerTest.java
