/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTinstruction.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;

/** La classe abstraite des instructions. */

public abstract class CEASTinstruction
  extends CEAST
  implements IAST2instruction<CEASTparseException> {

    /** Renvoyer une instruction vide qui ne fait rien et, comme il faut
     * rendre une valeur, on renvoie FALSE.  */
    public static IAST2instruction<CEASTparseException> voidInstruction () {
        return CEASTexpression.voidExpression();
    }

    /** Une constante utile pour les conversions entre liste et tableau. */
    @SuppressWarnings("unchecked")
    public final static
    IAST2instruction<CEASTparseException>[] EMPTY_INSTRUCTION_ARRAY =
        new IAST2instruction[0];
}

// end of CEASTInstruction.java
