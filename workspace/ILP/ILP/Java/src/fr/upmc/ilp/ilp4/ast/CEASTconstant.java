/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTconstant.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2constant;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4constant;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;

/** Les constantes et leur interprétation. */

public abstract class CEASTconstant
extends CEASTdelegableExpression
implements IAST4constant {

    public CEASTconstant (final IAST2constant<CEASTparseException> delegate) {
        this.delegate = delegate;
    }
    protected IAST2constant<CEASTparseException> delegate;

    @Override
    public IAST2constant<CEASTparseException> getDelegate () {
        return this.delegate;
    }

    public Object getValue () {
        return getDelegate().getValue();
    }
    public String getDescription () {
        return getDelegate().getDescription();
    }

    /** Toutes les constantes valent leur propre valeur. */
    @Override
    public Object eval (final ILexicalEnvironment lexenv, final ICommon common)
    {
        return getDelegate().getValue();
    }

    public <Data, Result, Exc extends Throwable> Result 
      accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
}

// end of CEASTConstant.java
