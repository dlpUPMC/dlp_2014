/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTlocalAssignment.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4localAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

/** Affectation à des variables locales.
 * Cette classe est nouvelle dans ILP4.  */

public class CEASTlocalAssignment
extends CEASTassignment
implements IAST4localAssignment {

    public CEASTlocalAssignment (final IAST4localVariable variable,
                                 final IAST4expression value) {
        super(variable, value);
    }

    @Override
    @ILPvariable
    public IAST4localVariable getVariable () {
      return CEAST.narrowToIAST4localVariable(getDelegate().getVariable());
    }

    /** Interprétation. */

    @Override
    public Object eval (final ILexicalEnvironment lexenv, final ICommon common)
    throws EvaluationException {
        final Object newValue = getValue().eval(lexenv, common);
        lexenv.update(getVariable(), newValue);
        return newValue;
    }

    @Override
    public IAST4localAssignment normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory)
    throws NormalizeException {
        return factory.newLocalAssignment(
          CEAST.narrowToIAST4localVariable(
                  getVariable().normalize(lexenv, common, factory)),
          getValue().normalize(lexenv,common, factory) );
    }

    @Override
    public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
}
