/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id:Process.java 505 2006-10-11 06:58:35Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2;

import java.io.IOException;

import org.w3c.dom.Document;

import fr.upmc.ilp.ilp1.AbstractProcess;
import fr.upmc.ilp.ilp2.ast.CEASTFactory;
import fr.upmc.ilp.ilp2.ast.CEASTParser;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.cgen.CgenEnvironment;
import fr.upmc.ilp.ilp2.cgen.CgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IAST2Factory;
import fr.upmc.ilp.ilp2.interfaces.IAST2program;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;
import fr.upmc.ilp.ilp2.runtime.CommonPlus;
import fr.upmc.ilp.ilp2.runtime.ConstantsStuff;
import fr.upmc.ilp.ilp2.runtime.LexicalEnvironment;
import fr.upmc.ilp.ilp2.runtime.PrintStuff;
import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.IFinder;
import fr.upmc.ilp.tool.ProgramCaller;

/** Cette classe précise comment est traité un programme d'ILP2.
 */

public class Process extends AbstractProcess {

    /** Un constructeur utilisant toutes les valeurs par defaut possibles. 
     * @throws IOException */

    public Process (IFinder finder) throws IOException {
        super(finder);
        setGrammar(getFinder().findFile("grammar2.rng"));
        IAST2Factory<CEASTparseException> factory = new CEASTFactory();
        setFactory(factory);
        setParser(new CEASTParser(factory));
    }

    public IParser<CEASTparseException> getParser () {
        return this.parser;
    }
    private IParser<CEASTparseException> parser;
    public void setParser(IParser<CEASTparseException> parser) {
        this.parser = parser;
    }
    
    public IAST2Factory<CEASTparseException> getFactory () {
        return this.factory;
    }
    private IAST2Factory<CEASTparseException> factory;
    public void setFactory(IAST2Factory<CEASTparseException> factory) {
        this.factory = factory;
    }

    public IAST2program<CEASTparseException> getCEAST () {
        return this.ceast;
    }
    protected IAST2program<CEASTparseException> ceast;
    public void setCEAST (IAST2program<CEASTparseException> ceast) {
        this.ceast = ceast;
    }

    /** Initialisation: @see fr.upmc.ilp.tool.AbstractProcess. */

    /** Préparation. On analyse syntaxiquement le texte du programme,
     * on effectue quelques analyses et on l'amène à un état où il
     * pourra être interprété ou compilé. Toutes les analyses communes
     * à ces deux fins sont partagées ici.
     */

    public void prepare() {
        try {
            assert this.rngFile != null;
            final Document d = getDocument(this.rngFile);
            setCEAST(getParser().parse(d));

            this.prepared = true;

        } catch (Throwable e) {
            this.preparationFailure = e;
            if ( this.verbose ) {
                System.err.println(e);
            }
        }
    }

    /** Interpretation */

    public void interpret() {
        try {
            assert this.prepared;
            final ICommon intcommon = new CommonPlus();
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
            if ( this.verbose ) {
                System.err.println(e);
            }
        }
    }

    /** Compilation vers C. */

    public void compile() {
        try {
            assert this.prepared;
            final ICgenEnvironment common = new CgenEnvironment();
            final ICgenLexicalEnvironment lexenv =
                CgenLexicalEnvironment.CgenEmptyLexicalEnvironment.create();
            this.ccode = getCEAST().compile(lexenv, common);

            this.compiled = true;

        } catch (Throwable e) {
            this.compilationFailure = e;
            if ( this.verbose ) {
                System.err.println(e);
            }
        }
    }

    /** Exécution du programme compilé: */

    public void runCompiled() {
        try {
            assert this.compiled;
            assert this.cFile != null;
            assert this.compileThenRunScript != null;
            FileTool.stuffFile(this.cFile, ccode);

            // Optionnel: mettre en forme le programme:
            String indentProgram = "indent " + this.cFile.getAbsolutePath();
            ProgramCaller pcindent = new ProgramCaller(indentProgram);
            pcindent.run();

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
