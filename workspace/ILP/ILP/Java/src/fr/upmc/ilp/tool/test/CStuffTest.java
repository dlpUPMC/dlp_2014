/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: CStuffTest.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.tool.test;

import fr.upmc.ilp.tool.CStuff;
import junit.framework.TestCase;

public class CStuffTest extends TestCase {

  public void testMangle () {
    assertEquals("", CStuff.mangle(""));
    assertEquals("a", CStuff.mangle("a"));
    assertEquals("AZ9b", CStuff.mangle("AZ9b"));
    assertNotSame("az9b", CStuff.mangle("AZ9b"));
    assertNotSame("AZ9B", CStuff.mangle("AZ9b"));
    assertEquals("i_40Z", CStuff.mangle("i@Z"));
    assertEquals("_40Z", CStuff.mangle("@Z"));
    assertEquals("_40", CStuff.mangle("@"));
    assertEquals("i_3c0Z", CStuff.mangle("i\u03c0Z")); // Pi
  }

  public void testProtect () {
    assertEquals("", CStuff.protect(""));
    assertEquals("a", CStuff.protect("a"));
    assertEquals("aB?", CStuff.protect("aB?"));
    assertEquals("a\\\"", CStuff.protect("a\""));
    assertEquals("a\\\\n", CStuff.protect("a\\n"));
  }

}

// end of CStuffTest.java
