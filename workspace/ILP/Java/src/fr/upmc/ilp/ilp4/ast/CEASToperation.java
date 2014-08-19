/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASToperation.java 918 2010-01-15 16:51:15Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2operation;
import fr.upmc.ilp.ilp4.interfaces.IAST4operation;

/** Les opérations (unaires ou binaires). */

public abstract class CEASToperation
extends CEASTdelegableExpression
implements IAST4operation {

    public CEASToperation () {}

    //NOTE: une methode abstraite heritee raffinant le type.
    @Override
    public abstract IAST2operation<CEASTparseException> getDelegate ();

    public String getOperatorName () {
        return getDelegate().getOperatorName();
    }
    public int getArity () {
        return getDelegate().getArity();
    }

}

// end of CEASToperation.java
