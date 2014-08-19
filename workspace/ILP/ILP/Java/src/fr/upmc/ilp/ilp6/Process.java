/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: Process.java 1299 2013-08-27 07:09:39Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6;

import java.io.IOException;

import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.runtime.ConstantsStuff;
import fr.upmc.ilp.ilp3.ThrowPrimitive;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.ast.NormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.runtime.LexicalEnvironment;
import fr.upmc.ilp.ilp4.runtime.PrintStuff;
import fr.upmc.ilp.ilp6.ast.CEAST6;
import fr.upmc.ilp.ilp6.ast.CEAST6Factory;
import fr.upmc.ilp.ilp6.ast.CEASTParser;
import fr.upmc.ilp.ilp6.ast.NormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.cgen.CgenEnvironment6;
import fr.upmc.ilp.ilp6.cgen.CgenLexicalEnvironment6;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6program;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.runtime.CommonPlus;
import fr.upmc.ilp.ilp6.runtime.ICommon;
import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.IFinder;
import fr.upmc.ilp.tool.ProgramCaller;

/** Cette classe précise comment est traité un programme d'ILP6. */

public class Process extends fr.upmc.ilp.ilp4.Process {

    /** Un constructeur utilisant toutes les valeurs par defaut possibles. */

    public Process (IFinder finder) throws IOException {
        super(finder); // pour mémoire!
        setGrammar(getFinder().findFile("grammar6.rng"));
        IAST6Factory factory = new CEAST6Factory();
        setFactory(factory);
        setParser(new CEASTParser(factory));
    }

    /** Profitons de la covariance! */
    @Override
    public IAST6program getCEAST () {
        return CEAST6.narrowToIAST6program(super.getCEAST());
    }
    @Override
    public IAST6Factory getFactory () {
        return CEAST6.narrowToIAST6Factory(super.getFactory());
    }

    /** Initialisation: @see fr.upmc.ilp.tool.AbstractProcess. */

    /** Préparation (heritee) */

    /** Normalisation */
    
    @Override
    public IAST6program performNormalization()
    throws NormalizeException {
        final IAST6Factory factory = getFactory();
        final INormalizeLexicalEnvironment normlexenv =
            new NormalizeLexicalEnvironment.EmptyNormalizeLexicalEnvironment();
        final INormalizeGlobalEnvironment normcommon =
            new NormalizeGlobalEnvironment();
        normcommon.addPrimitive(factory.newGlobalVariable("print"));
        normcommon.addPrimitive(factory.newGlobalVariable("newline"));
        normcommon.addPrimitive(factory.newGlobalVariable("throw"));
        final IAST6program program = 
            getCEAST().normalize6(normlexenv, normcommon, factory);
        return program;
    }

    /** Interpretation */

    @Override
    public void interpret() {
        try {
            assert this.prepared;
            final ICommon intcommon = new CommonPlus();
            intcommon.bindPrimitive("throw", ThrowPrimitive.create());
            final ILexicalEnvironment intlexenv =
                LexicalEnvironment.EmptyLexicalEnvironment.create();
            final PrintStuff intps = new PrintStuff();
            intps.extendWithPrintPrimitives(intcommon);
            final ConstantsStuff csps = new ConstantsStuff();
            csps.extendWithPredefinedConstants(intcommon);

            this.result = getCEAST().eval6(intlexenv, intcommon);
            this.printing = intps.getPrintedOutput().trim();

            this.interpreted = true;

        } catch (Throwable e) {
            this.interpretationFailure = e;
        }
    }

    /** Compilation vers C. */

    @Override
    public void compile() {
        try {
            assert this.prepared;
            ICgenEnvironment common = new CgenEnvironment6();
            common.bindPrimitive("throw");
            ICgenLexicalEnvironment lexenv =
                CgenLexicalEnvironment6.CgenEmptyLexicalEnvironment6.create();
            this.ccode = getCEAST().compile6(lexenv, common);

            this.compiled = true;

        } catch (Throwable e) {
            this.compilationFailure = e;
        }
    }

    /** Exécution du programme compilé: */

    @Override
    public void runCompiled() {
        try {
            assert this.compiled;
            assert this.cFile != null;
            assert this.compileThenRunScript != null;
            FileTool.stuffFile(this.cFile, this.ccode);

            // Optionnel: mettre en forme le programme:
            String indentProgram = "indent -npcs " + this.cFile.getAbsolutePath();
            ProgramCaller pcindent = new ProgramCaller(indentProgram);
            pcindent.run();

            // et le compiler:
            String program = "bash "
                + this.compileThenRunScript.getAbsolutePath() + " "
                + " +gc "
                + this.cFile.getAbsolutePath()
                + " C/ilpObj.o ";
            ProgramCaller pc = new ProgramCaller(program);
            pc.setVerbose();
            pc.run();
            this.executionPrinting = pc.getStdout().trim();

            this.executed = ( pc.getExitValue() == 0 );

        } catch (Throwable e) {
            this.executionFailure = e;
        }
    }

}

// end of Process.java
