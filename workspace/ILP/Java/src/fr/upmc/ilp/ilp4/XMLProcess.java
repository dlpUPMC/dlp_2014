package fr.upmc.ilp.ilp4;

import java.io.IOException;

import org.w3c.dom.Document;

import fr.upmc.ilp.ilp4.ast.XMLwriter;
import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.IFinder;

/**  Cette classe traite un programme ILP4 en imprimant l'AST en XML apres
 * chaque phase. Ca peut etre utile pour tracer la forme de l'arbre ou pour
 * rendre persistant un tel AST.
 * */

public class XMLProcess extends Process {

    public XMLProcess (IFinder finder, String basename) throws IOException {
        super(finder);
        this.basename = basename;
    }
    protected String basename;

    @Override
    public void prepare() {
        try {
            final Document d = getDocument(this.rngFile);
            setCEAST(getParser().parse(d));

            XMLwriter xmlWriter = new XMLwriter();
            FileTool.stuffFile(basename + "-A.xml", xmlWriter.process(getCEAST()));
            // Les analyses statiques
            setCEAST(performNormalization());
            FileTool.stuffFile(basename + "-B.xml", xmlWriter.process(getCEAST()));
            getCEAST().computeInvokedFunctions();
            FileTool.stuffFile(basename + "-C.xml", xmlWriter.process(getCEAST()));
            getCEAST().inline(getFactory());
            FileTool.stuffFile(basename + "-D.xml", xmlWriter.process(getCEAST()));
            getCEAST().computeGlobalVariables();
            FileTool.stuffFile(basename + "-E.xml", xmlWriter.process(getCEAST()));

            this.prepared = true;

        } catch (Throwable e) {
            this.preparationFailure = e;
        }
    }
}
