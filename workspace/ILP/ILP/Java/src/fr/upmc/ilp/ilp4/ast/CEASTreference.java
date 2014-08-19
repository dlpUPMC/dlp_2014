package fr.upmc.ilp.ilp4.ast;

import java.util.Set;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2reference;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4reference;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

public class CEASTreference
extends CEASTdelegableExpression
implements IAST4reference {

    public CEASTreference(IAST4variable variable) {
        this.delegate =
            new fr.upmc.ilp.ilp2.ast.CEASTreference(variable);
    }
    private final fr.upmc.ilp.ilp2.ast.CEASTreference delegate;

    @Override
    public fr.upmc.ilp.ilp2.ast.CEASTreference getDelegate () {
        return this.delegate;
    }

    @ILPvariable
    public IAST4variable getVariable() {
        return CEAST.narrowToIAST4variable(getDelegate().getVariable());
    }

    public static IAST2reference<CEASTparseException> parse (
            final Element e, final IParser parser) {
        return fr.upmc.ilp.ilp2.ast.CEASTreference.parse(e, parser);
    }

    //NOTE: Accès direct aux champs interdit à partir d'ici!

    @Override
    public Object eval (final ILexicalEnvironment lexenv,
                        final ICommon common )
    throws EvaluationException {
        return getVariable().eval(lexenv, common);
    }

    @Override
    public void compileExpression (final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final ICgenEnvironment common,
                                   final IDestination destination)
    throws CgenerationException {
        destination.compile(buffer, lexenv, common);
        buffer.append(" ");
        try {
            buffer.append(lexenv.compile(getVariable()));
        } catch (CgenerationException e) {
            buffer.append(common.compileGlobal(getVariable()));
        }
        buffer.append(" ");
    }

    /** Une variable est globale si elle n'est pas locale. */

    @Override
    public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                   final ICgenLexicalEnvironment lexenv ) {
        if ( ! lexenv.isPresent(getVariable()) ) {
            globalvars.add(getVariable());
        }
    }

    @Override
    public IAST4reference normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
    throws NormalizeException {
        return factory.newReference(
                    getVariable().normalize(lexenv, common, factory));
    }

    public <Data, Result, Exc extends Throwable> Result 
      accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
}
