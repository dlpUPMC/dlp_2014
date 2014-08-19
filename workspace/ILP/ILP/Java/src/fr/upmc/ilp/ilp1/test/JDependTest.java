package fr.upmc.ilp.ilp1.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.junit.Before;
import org.junit.Test;

/** Verifier que le compilateur ne depend que des interfaces d'ILP1 et non de 
 * l'interprete. Pour cela on utilise JDepend.
 */

public class JDependTest {
    
    JDepend jdepend;

    @Before 
    public void setUp () throws IOException {
        jdepend = new JDepend();
        jdepend.addDirectory("Java/bin/");
    }

    // Hélas! JDepend est en vieux Java peu précisément typé!
    @SuppressWarnings("unchecked")
    @Test
    public void analyzeTest () {
        Collection<JavaPackage> pkgs = jdepend.analyze();
        assert(pkgs.size() > 10);
        JavaPackage cgen = jdepend.getPackage("fr.upmc.ilp.ilp1.cgen");
        pkgs = cgen.getEfferents();
        for ( JavaPackage pkg : pkgs ) {
            String name = pkg.getName();
            assertTrue( (name.startsWith("fr.upmc.ilp"))
                    ? (name.equals("fr.upmc.ilp.ilp1.interfaces"))
                    : true );
        }
    }
}

// end of JDependTest.java
