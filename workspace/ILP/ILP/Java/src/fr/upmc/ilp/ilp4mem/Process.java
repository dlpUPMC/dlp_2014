/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: Process.java 1299 2013-08-27 07:09:39Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4mem;

import java.io.IOException;

import fr.upmc.ilp.ilp2.cgen.CgenEnvironment;
import fr.upmc.ilp.ilp2.cgen.CgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.ast.NormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.tool.IFinder;

/** Cette classe précise comment est traité un programme d'ILP3. */

public class Process extends fr.upmc.ilp.ilp4.Process {

    /** Un constructeur utilisant toutes les valeurs par defaut possibles. 
     * @throws IOException */

    public Process (IFinder finder) throws IOException {
        super(finder); // pour mémoire!
        IAST4Factory factory = new CEASTFactory();
        setFactory(factory);
        setParser(new CEASTParser(factory));
    }

    /** Initialisation: @see fr.upmc.ilp.tool.AbstractProcess. */

    /** Préparation. Héritée! */

    @Override
    public IAST4program performNormalization()
    throws NormalizeException {
        IAST4Factory factory = getFactory();
        final INormalizeLexicalEnvironment normlexenv =
            new NormalizeLexicalEnvironment.EmptyNormalizeLexicalEnvironment();
        final INormalizeGlobalEnvironment normcommon =
            new NormalizeGlobalEnvironment();
        normcommon.addPrimitive(factory.newGlobalVariable("print"));
        normcommon.addPrimitive(factory.newGlobalVariable("newline"));
        normcommon.addPrimitive(factory.newGlobalVariable("throw"));
        return getCEAST().normalize(normlexenv, normcommon, factory);
    }

    /** Interpretation. Héritée */
   
    /** Compilation vers C. */

    @Override
    public void compile() {
        try {
            assert this.prepared;
            final ICgenEnvironment common = new CgenEnvironment();
            common.bindPrimitive("throw");
            common.bindPrimitive("memoryGet");
            common.bindPrimitive("memoryReset");
            final ICgenLexicalEnvironment lexenv =
                CgenLexicalEnvironment.CgenEmptyLexicalEnvironment.create();
            this.ccode = getCEAST().compile(lexenv, common);

            this.compiled = true;

        } catch (Throwable e) {
            this.compilationFailure = e;
        }
    }

    /** Exécution du programme compilé: */
}

// end of Process.java
