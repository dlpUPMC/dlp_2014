package fr.upmc.ilp.ilp5.eval1;

import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.runtime.LexicalEnvironment;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitor;

public class VisitableUserFunction
implements IVisitableUserFunction {

    public VisitableUserFunction (String name,
                                  IAST4variable[] variables,
                                  IAST4expression body ) {
        this(name, variables, body, 
                LexicalEnvironment.EmptyLexicalEnvironment.create());
    }
    public VisitableUserFunction (String name,
                                  IAST4variable[] variables,
                                  IAST4expression body,
                                  ILexicalEnvironment lexenv ) {
        this.name = name;
        this.variables = variables;
        this.body = body;
        this.lexenv = lexenv;
    }
    private final String name;
    private final IAST4variable[] variables;
    private final IAST4expression body;
    private ILexicalEnvironment lexenv;

    public IAST4variable[] getVariables() {
        return this.variables;
    }
    public IAST4expression getBody() {
        return this.body;
    }
    public ILexicalEnvironment getEnvironment() {
        return this.lexenv;
    }

    @Override
    public Object invoke(
            Object[] arguments,
            IAST5visitor<IContext, Object, VisitorEvaluationException> visitor,
            IContext context)
    throws VisitorEvaluationException {
        IAST4variable[] variables = getVariables();
        if ( variables.length != arguments.length ) {
            final String msg = "Wrong arity for function:" + name;
            throw new VisitorEvaluationException(msg);
        }
        ILexicalEnvironment lexenv = getEnvironment();
        for ( int i = 0 ; i<variables.length ; i++ ) {
            lexenv = lexenv.extend(variables[i], arguments[i]);
        }
        ICommon common = context.getCommonEnvironment();
        IContext newContext = new Context(lexenv, common);
        return getBody().accept(visitor, newContext);
    }
}
