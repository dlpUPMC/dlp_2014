package fr.upmc.ilp.ilp6.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.CEASTvariable;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6self;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IParser6;
import fr.upmc.ilp.ilp6.runtime.ICommon;

public class CEASTself
extends CEAST6expression
implements IAST6self {

    public CEASTself () {
        this(new CEASTselfVariable());
    }
    private final IAST4variable variable;

    public CEASTself (IAST4variable variable) {
        this.variable = variable;
    }

    public IAST4variable getLocalVariable () {
        return this.variable;
    }

    public static class CEASTselfVariable extends CEASTvariable
    implements IAST4localVariable {
        public CEASTselfVariable () {
            super("ilp_Self");
        }
    }

    public static IAST6self parse (final Element e, final IParser6 parser)
    throws CEASTparseException {
        return parser.getFactory().newSelf(new CEASTselfVariable());
    }

    @Override
    public Object eval6 (final ILexicalEnvironment lexenv,
                         final ICommon common)
    throws EvaluationException {
        return lexenv.lookup(getLocalVariable());
    }

    @Override
    public void compile6 (final StringBuffer buffer,
                          final ICgenLexicalEnvironment lexenv,
                          final ICgenEnvironment common,
                          final IDestination destination)
    throws CgenerationException {
        destination.compile(buffer, lexenv, common);
        buffer.append("ilp_Self;\n");
    }

    @Override
    public IAST6self normalize6 (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            IAST6Factory factory )
    throws NormalizeException {
        return factory.newSelf(lexenv.isPresent(this.getLocalVariable()));
    }
    
    @Deprecated
    IAST4expression normalize (INormalizeLexicalEnvironment lexenv,
                               INormalizeGlobalEnvironment common )
    throws NormalizeException {
        return new CEASTself(lexenv.isPresent(this.getLocalVariable()));
    }

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
