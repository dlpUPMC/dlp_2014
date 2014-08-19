package fr.upmc.ilp.ilp6.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp6.cgen.CgenMethodLexicalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6methodDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6super;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IParser6;
import fr.upmc.ilp.ilp6.runtime.ICommon;
import fr.upmc.ilp.ilp6.runtime.ILPmethod;

public class CEASTsuper
extends CEAST6expression
implements IAST6super {

    public CEASTsuper() {}

    public static IAST6super parse (final Element e, final IParser6 parser)
    throws CEASTparseException {
        return parser.getFactory().newSuper();
    }

    @Override
    public Object eval6 (final ILexicalEnvironment lexenv,
                         final ICommon common)
    throws EvaluationException {
        ILPmethod currentMethod = (ILPmethod) lexenv.lookup(ILPmethod.cmv);
        return currentMethod.callSuper(lexenv, common);
    }

    @Override
    public void compile6 (final StringBuffer buffer,
                          final ICgenLexicalEnvironment lexenv,
                          final ICgenEnvironment common,
                          final IDestination destination)
    throws CgenerationException {
        IAST6methodDefinition currentMethod = null;
        ICgenLexicalEnvironment le = lexenv;
        while ( ! le.isEmpty() ) {
            if ( le instanceof CgenMethodLexicalEnvironment ) {
                CgenMethodLexicalEnvironment cmle =
                    (CgenMethodLexicalEnvironment) le;
                currentMethod = cmle.getMethodDefinition();
                break;
            } else {
                le = le.getNext();
            }
        }
        if ( currentMethod != null ) {
            destination.compile(buffer, lexenv, common);
            buffer.append("ILP_FindAndCallSuperMethod(");
            buffer.append(currentMethod.getRealArity());
            buffer.append(");\n");
        } else {
            final String msg = "No supermethod!";
            throw new CgenerationException(msg);
        }
    }

    @Override
    public IAST6super normalize6 (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST6Factory factory )
    throws NormalizeException {
        return factory.newSuper();
    }
    // NOTE: Raisonnable car pas de classe imbriquees.

    public <Data, Result, Exc extends Throwable> Result 
    accept (IAST6visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
    public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return CEAST6.narrowToIAST6visitor(visitor).visit(this, data);
    }
    // NOTE: double methode surchargee.

}
