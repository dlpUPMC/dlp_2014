package fr.upmc.ilp.ilp5.eval1;

import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitor;

public interface IVisitableUserFunction {

    Object invoke (Object[] arguments, IAST5visitor<IContext, Object, VisitorEvaluationException> visitor, IContext context)
    throws VisitorEvaluationException;

    /** Obtenir les composantes d'une fonction utilisateur. */
    IAST4variable[] getVariables ();
    IAST4expression getBody ();
    ILexicalEnvironment getEnvironment ();
}
