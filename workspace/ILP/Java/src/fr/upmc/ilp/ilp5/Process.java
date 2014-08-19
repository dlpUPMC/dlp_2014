package fr.upmc.ilp.ilp5;

import java.io.IOException;

import org.w3c.dom.Document;

import fr.upmc.ilp.ilp5.ast.CEAST5Factory;
import fr.upmc.ilp.ilp5.ast.CEASTParser;
import fr.upmc.ilp.ilp5.ast.XMLwriter;
import fr.upmc.ilp.ilp5.interfaces.IAST5Factory;
import fr.upmc.ilp.tool.IFinder;

/**
 * Cette version introduit des fonctions localement definies. Le compilateur
 * reste à écrire.
 */

public class Process extends fr.upmc.ilp.ilp4.Process {

    /** Un constructeur utilisant toutes les valeurs par defaut possibles. */

    public Process (IFinder finder) throws IOException {
        super(finder);
        setGrammar(getFinder().findFile("grammar5.rng"));
        IAST5Factory factory = new CEAST5Factory();
        setFactory(factory);
        setParser(new CEASTParser(factory));
    }

    /** Initialisation: @see fr.upmc.ilp.tool.AbstractProcess. */

    /** Préparation (heritee) */

    @Override
    public void prepare() {
        try {
            final Document d = getDocument(this.rngFile);
            setCEAST(getParser().parse(d));

            // DEBUG
            XMLwriter xmlWriter = new XMLwriter();
            String xml = xmlWriter.process(getCEAST());
            System.out.println(xml);

            // Toutes les analyses statiques
            setCEAST(performNormalization());
            // DEBUG
            xml = xmlWriter.process(getCEAST());
            System.out.println(xml);

            getCEAST().computeInvokedFunctions();
            // DEBUG
            xml = xmlWriter.process(getCEAST());
            System.out.println(xml);

            getCEAST().inline(getFactory());
            // DEBUG
            xml = xmlWriter.process(getCEAST());
            System.out.println(xml);

            //getCEAST().computeGlobalVariables();
            // DEBUG
            //xml = xmlWriter.process(getCEAST());
            //System.out.println(xml);

            this.prepared = true;

        } catch (Throwable e) {
            this.preparationFailure = e;
        }
    }

    /** Interpretation (heritee) */

    /** Compilation vers C */

    @Override
    public void compile() {
        // On ne compile rien pour l'instant!
        this.compiled = true;
    }

    /** Exécution du programme compilé */
    
    @Override
    public void runCompiled() {
        // et on n'execute rien!
        this.executionPrinting = this.printing + this.result;
        this.executed = true;
    }
}
