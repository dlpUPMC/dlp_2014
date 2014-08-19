package fr.upmc.ilp.ilp4.ast;

import java.util.Set;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4delegable;

public abstract class CEASTdelegableExpression 
extends CEASTexpression
implements IAST4delegable {

    /** Profiter de la contravariance pour raffiner le type du delegue.
     * Attention! les expressions d'ilp4 ne sont malheureusement pas toutes 
     * des expressions d'ilp2! Les expressions d'ilp4 contiennent les 
     * instructions d'ilp4 qui sont deleguees a des instruction d'ilp2 qui 
     * ne sont pas des expressions d'ilp2. */

    public abstract IAST2instruction<CEASTparseException> getDelegate ();

    /** Evaluer un AST */

    @Override
    public Object eval (final ILexicalEnvironment lexenv,
                        final ICommon common)
    throws EvaluationException {
        return getDelegate().eval(lexenv, common);
    }

    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common,
                         final IDestination destination)
    throws CgenerationException {
        final IAST2instruction<CEASTparseException> delegate = getDelegate();
        delegate.compileInstruction(buffer, lexenv, common, destination);
    }

    /** Par compatibilité avec le délégué. */
    @Override
    public void compileInstruction (final StringBuffer buffer,
                                    final ICgenLexicalEnvironment lexenv,
                                    final ICgenEnvironment common,
                                    final IDestination destination)
    throws CgenerationException {
        this.compile(buffer, lexenv, common, destination);
        buffer.append(";\n");
    }

    /** Calculer l'ensemble des variables globales de cette instruction.
     * Par défaut, il n'y a pas de variable globale.
     *
     * NOTE: Set&lt;IAST2variable&gt; n'est pas Set&lt;IAST4variable&gt; */
    @Override
    public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                   final ICgenLexicalEnvironment lexenv ) {
        getDelegate().findGlobalVariables(globalvars, lexenv);
    }
}
