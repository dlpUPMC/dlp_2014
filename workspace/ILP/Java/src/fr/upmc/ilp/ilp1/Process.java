/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id:Process.java 505 2006-10-11 06:58:35Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1;

import java.io.IOException;

import org.w3c.dom.Document;

import fr.upmc.ilp.ilp1.cgen.CgenEnvironment;
import fr.upmc.ilp.ilp1.cgen.CgenLexicalEnvironment.Empty;
import fr.upmc.ilp.ilp1.cgen.Cgenerator;
import fr.upmc.ilp.ilp1.cgen.ICgenEnvironment;
import fr.upmc.ilp.ilp1.cgen.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp1.eval.EAST;
import fr.upmc.ilp.ilp1.eval.EASTFactory;
import fr.upmc.ilp.ilp1.eval.EASTParser;
import fr.upmc.ilp.ilp1.runtime.CommonPlus;
import fr.upmc.ilp.ilp1.runtime.ConstantsStuff;
import fr.upmc.ilp.ilp1.runtime.EmptyLexicalEnvironment;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;
import fr.upmc.ilp.ilp1.runtime.PrintStuff;
import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.IFinder;
import fr.upmc.ilp.tool.ProgramCaller;

/** Cette classe précise comment est traité un programme d'ILP1. */

public class Process extends AbstractProcess {

    public Process (IFinder finder) throws IOException {
        super(finder);
        // Initialiser ici les caractéristiques non liées aux tests et,
        // éventuellement indiquer où chercher ces fichiers:
        setGrammar(getFinder().findFile("grammar1.rng"));
    }

    /** Initialisation: @see fr.upmc.ilp.tool.AbstractProcess. */

    /** Préparation. On analyse syntaxiquement le texte du programme,
     * on effectue quelques analyses et on l'amène à un état où il
     * pourra être interprété ou compilé. Toutes les analyses communes
     * à ces deux fins sont partagées ici.
     */

    @Override
    public void prepare() {
        try {
            assert this.rngFile != null;
            final Document d = getDocument(this.rngFile);

            final EASTFactory factory = new EASTFactory();
            final EASTParser parser = new EASTParser(factory);
            this.east = parser.parse(d);
            setIAST(this.east);

            this.prepared = true;

        } catch (Throwable e) {
            this.preparationFailure = e;
            if ( this.verbose ) {
                System.err.println(e);
            }
        }
    }
    protected EAST east; // Hack car IAST insuffisant pour eval().

    /** Interprétation */

    @Override
    public void interpret() {
        try {
            assert this.prepared;
            final ICommon intcommon = new CommonPlus();
            ILexicalEnvironment intlexenv = EmptyLexicalEnvironment.create();
            final PrintStuff intps = new PrintStuff();
            intlexenv = intps.extendWithPrintPrimitives(intlexenv);
            final ConstantsStuff intcs = new ConstantsStuff();
            intlexenv = intcs.extendWithPredefinedConstants(intlexenv);

            this.result = this.east.eval(intlexenv, intcommon);
            this.printing = intps.getPrintedOutput().trim();

            this.interpreted = true;

        } catch (Throwable e) {
            this.interpretationFailure = e;
            if ( this.verbose ) {
                System.err.println(e);
            }
        }
    }

    /** Compilation vers C. */

    @Override
    public void compile() {
        try {
            assert this.prepared;
            final ICgenEnvironment common = new CgenEnvironment();
            final Cgenerator compiler = new Cgenerator(common);
            ICgenLexicalEnvironment lexenv = Empty.create();
            lexenv = common.extendWithPrintPrimitives(lexenv);
            lexenv = common.extendWithPredefinedConstants(lexenv);
            this.ccode = compiler.compile(this.east, lexenv, "return");

            this.compiled = true;

        } catch (Throwable e) {
            this.compilationFailure = e;
            if ( this.verbose ) {
                System.err.println(e);
            }
        }
    }

    /** Exécution du programme compilé: */

    @Override
    public void runCompiled() {
        try {
            assert this.compiled;
            assert this.cFile != null;
            assert this.compileThenRunScript != null;
            FileTool.stuffFile(this.cFile, ccode);

            // et le compiler:
            String program = "bash "
                + this.compileThenRunScript.getAbsolutePath() + " "
                + this.cFile.getAbsolutePath();
            ProgramCaller pc = new ProgramCaller(program);
            pc.setVerbose();
            pc.run();
            this.executionPrinting = pc.getStdout().trim();

            this.executed = ( pc.getExitValue() == 0 );

        } catch (Throwable e) {
            this.executionFailure = e;
            if ( this.verbose ) {
                System.err.println(e);
            }
        }
    }

}

// end of Process.java
