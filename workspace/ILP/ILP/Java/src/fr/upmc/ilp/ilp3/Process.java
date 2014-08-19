/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: Process.java 1299 2013-08-27 07:09:39Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.ilp3;

import java.io.IOException;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.cgen.CgenEnvironment;
import fr.upmc.ilp.ilp2.cgen.CgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.runtime.CommonPlus;
import fr.upmc.ilp.ilp2.runtime.ConstantsStuff;
import fr.upmc.ilp.ilp2.runtime.LexicalEnvironment;
import fr.upmc.ilp.ilp2.runtime.PrintStuff;
import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.IFinder;
import fr.upmc.ilp.tool.ProgramCaller;

/** Cette classe précise comment est traité un programme d'ILP3.
 *
 *  Je n'herite volontairement pas de fr.upmc.ilp.ilp2.Process afin
 *  de rassembler toutes les informations utiles en une seule classe ici.
 */

public class Process extends fr.upmc.ilp.ilp2.Process {

    /** Un constructeur utilisant toutes les valeurs par defaut possibles. 
     * @throws IOException */

    public Process (IFinder finder) throws IOException {
        super(finder); // pour mémoire!
        setGrammar(getFinder().findFile("grammar3.rng"));
        IAST3Factory<CEASTparseException> factory = new CEASTFactory();
        setFactory(factory);
        setParser(new CEASTParser(factory));
    }

    /** Initialisation: @see fr.upmc.ilp.tool.AbstractProcess. */

    /** Préparation. Héritée! */

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

            this.result = getCEAST().eval(intlexenv, intcommon);
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
            final ICgenEnvironment common = new CgenEnvironment();
            common.bindPrimitive("throw");
            final ICgenLexicalEnvironment lexenv =
                CgenLexicalEnvironment.CgenEmptyLexicalEnvironment.create();
            this.ccode = getCEAST().compile(lexenv, common);

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
            String indentProgram = "indent " + this.cFile.getAbsolutePath();
            ProgramCaller pcindent = new ProgramCaller(indentProgram);
            pcindent.run();

            // et le compiler:
            String program = "bash "
                + this.compileThenRunScript.getAbsolutePath() + " "
                + this.cFile.getAbsolutePath()
                + " C/ilpError.o C/ilpException.o";
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
