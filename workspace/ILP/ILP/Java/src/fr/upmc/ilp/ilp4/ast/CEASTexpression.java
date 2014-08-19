/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTexpression.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.cgen.NoDestination;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

/** La classe abstraite des expressions. */

public abstract class CEASTexpression
extends CEAST
implements IAST4expression {

    /** Renvoyer une expression vide qui ne fait rien d'interessant. */

    public static IAST4expression voidExpression() {
        return new CEASTboolean("false");
    }

    /** Une constante utile pour les conversions entre liste et tableau. */

    public static final IAST4expression[] EMPTY_EXPRESSION_ARRAY =
            new IAST4expression[0];

    /** Rendre la version integrée de l'expression. */

    public IAST4expression getInlined () {
        return this;
    }

    @Override
    public IAST4expression normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
    throws NormalizeException {
        return this;
    }

    public void compileExpression (final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final ICgenEnvironment common)
    throws CgenerationException {
        this.compile(buffer, lexenv, common, NoDestination.create());
    }
    
    @Override
    @Deprecated
    public void compileInstruction (final StringBuffer buffer,
                                    final ICgenLexicalEnvironment lexenv,
                                    final ICgenEnvironment common,
                                    final IDestination destination)
    throws CgenerationException {
        this.compile(buffer, lexenv, common, destination);
    }

    @Override
    @Deprecated
    public void compileExpression (final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final ICgenEnvironment common,
                                   final IDestination destination)
    throws CgenerationException {
        this.compile(buffer, lexenv, common, destination);
    }

}

// end of CEASTExpression.java
