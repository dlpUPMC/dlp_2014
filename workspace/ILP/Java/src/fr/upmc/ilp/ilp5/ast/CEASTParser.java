/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTParser.java 698 2007-11-04 14:08:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp5.ast;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp5.interfaces.IAST5Factory;
import fr.upmc.ilp.ilp5.interfaces.IParser5;

/** Transformer un document XML en un CEAST. */

public class CEASTParser
extends fr.upmc.ilp.ilp4.ast.CEASTParser
implements IParser5<CEASTparseException> {

    @Override
    public IAST5Factory getFactory () {
        return CEAST5.narrowToIAST5Factory(super.getFactory());
    }

    public CEASTParser (IAST5Factory factory) {
        super(factory);
        addParser("codefinitions", EASTcodefinedLocalFunctions.class);
    }
}

// end of CEASTParser.java
