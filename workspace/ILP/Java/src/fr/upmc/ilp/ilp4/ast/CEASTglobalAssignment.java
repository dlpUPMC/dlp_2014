/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTglobalAssignment.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4assignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

/** Affectations à des variables globales.
 * Cette classe est nouvelle dans ILP4. */

public class CEASTglobalAssignment
extends CEASTassignment
implements IAST4globalAssignment {

    public CEASTglobalAssignment (final IAST4globalVariable variable,
                                  final IAST4expression value) {
        super(variable, value);
    }

    @Override
    @ILPvariable
    public IAST4globalVariable getVariable () {
      return CEAST.narrowToIAST4globalVariable(getDelegate().getVariable());
    }

    /** Interprétation. */

    @Override
    public Object eval (final ILexicalEnvironment lexenv, final ICommon common)
    throws EvaluationException {
        final Object newValue = getValue().eval(lexenv, common);
        common.updateGlobal(getVariable().getName(), newValue);
        return newValue;
    }

    @Override
    public IAST4assignment normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory)
    throws NormalizeException {
        return factory.newGlobalAssignment(
                CEAST.narrowToIAST4globalVariable(
                        getVariable().normalize(lexenv, common, factory) ),
                getValue().normalize(lexenv,common, factory) );
    }

    @Override
    public <Data, Result, Exc extends Throwable> Result 
      accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
}

// end of CEASTglobalAssignment.java
