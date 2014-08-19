package fr.upmc.ilp.ilp5.eval1;

import java.io.IOException;

import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.runtime.ConstantsStuff;
import fr.upmc.ilp.ilp4.runtime.CommonPlus;
import fr.upmc.ilp.ilp4.runtime.LexicalEnvironment;
import fr.upmc.ilp.ilp4.runtime.PrintStuff;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitor;
import fr.upmc.ilp.tool.IFinder;

/**
 * Cette variante remplace la méthode eval par un visiteur (@see 
 * fr.upmc.ilp.ilp5.eval1.VisitorEvaluator). Les deux environnements
 * (lexenv et common) sont regroupés en un contexte, le visiteur renvoie
 * la valeur obtenue. Les effets sont, comme avant, accumulés dans le flux
 * de sortie.
 */
public class Process extends fr.upmc.ilp.ilp5.Process {

    /** Un constructeur utilisant toutes les valeurs par defaut possibles. 
     * @throws IOException */

    public Process (IFinder finder) throws IOException {
        super(finder);
    }

    /** Initialisation: @see fr.upmc.ilp.tool.AbstractProcess. */

    /** Préparation (heritee) */

    /** Interpretation (heritee) */

    @Override
    public void interpret() {
        try {
            final ICommon intcommon = new CommonPlus();
            intcommon.bindPrimitive("throw", ThrowPrimitive.create());
            final ILexicalEnvironment intlexenv =
                LexicalEnvironment.EmptyLexicalEnvironment.create();
            final PrintStuff intps = new PrintStuff();
            intps.extendWithPrintPrimitives(intcommon);
            final ConstantsStuff csps = new ConstantsStuff();
            csps.extendWithPredefinedConstants(intcommon);

            IContext context = new Context(intlexenv, intcommon);
            IAST5visitor<IContext, Object, VisitorEvaluationException> visitor 
                = new VisitorEvaluator();

            this.result = getCEAST().accept(visitor, context);
            this.printing = intps.getPrintedOutput().trim();

            this.interpreted = true;

        } catch (Throwable e) {
            this.interpretationFailure = e;
        }
    }

    /** Compilation vers C (heritee) */

    /** Exécution du programme compilé (heritee) */
}
