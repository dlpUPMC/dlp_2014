/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ILPmethod.java 932 2010-08-21 13:12:32Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.runtime;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.runtime.LexicalEnvironment;
import fr.upmc.ilp.ilp2.runtime.UserFunction;
import fr.upmc.ilp.ilp4.ast.CEASTlocalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;

public class ILPmethod
// on n'herite pas de UserGlobalFunction
extends UserFunction {

    public ILPmethod (final String name,
                      final IAST2variable[] variable,
                      final IAST2instruction<CEASTparseException> body) {
        super(variable,
              body,
              LexicalEnvironment.EmptyLexicalEnvironment.create() );
        this.name = name;
    }
    protected final String name;

    public String getMethodName () {
        return this.name;
    }

    // On retrouve la methode courante par cette pseudo-variable.
    public static IAST4localVariable cmv =
        new CEASTlocalVariable("ilp_CurrentMethod");
    protected static IAST4localVariable cmargs =
        new CEASTlocalVariable("ilp_CurrentArguments");

    public void setDefiningClass (ILPClass clazz) {
        this.definingClass = clazz;
    }
    private ILPClass definingClass;

    public ILPClass getDefiningClass () {
        return this.definingClass;
    }

    public Object callSuper (final ILexicalEnvironment lexenv,
                             final ICommon common )
    throws EvaluationException {
        Object[] arguments = (Object[]) lexenv.lookup(cmargs);
        return getDefiningClass().getSuperClass()
            .send(getMethodName(), arguments, common);
    }

    @Override
    public Object invoke (final Object[] arguments,
                          final ICommon common)
      throws EvaluationException {
        IAST2variable[] variables = getVariables();
        if ( variables.length != arguments.length ) {
            final String msg = "Wrong arity";
            throw new EvaluationException(msg);
        }
        ILexicalEnvironment lexenv = getEnvironment()
            .extend(cmv, this)
            .extend(cmargs, arguments);
        for ( int i = 0 ; i<variables.length ; i++ ) {
            lexenv = lexenv.extend(variables[i], arguments[i]);
        }
        return getBody().eval(lexenv, common);
    }

}

// end of ILPmethod.java
