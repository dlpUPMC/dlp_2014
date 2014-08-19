/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTassignment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.Set;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.cgen.AssignDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2assignment;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

public class CEASTassignment
extends CEASTexpression
implements IAST2assignment<CEASTparseException> {

    public CEASTassignment (final IAST2variable variable,
                            final IAST2expression<CEASTparseException> value) {
        this.variable = variable;
        this.value = value;
    }
    private final IAST2variable variable;
    private final IAST2expression<CEASTparseException> value;

    public IAST2expression<CEASTparseException> getValue () {
        return this.value;
    }
    public IAST2variable getVariable () {
        return this.variable;
    }

    public static IAST2assignment<CEASTparseException> parse (
            final Element e, final IParser<CEASTparseException> parser)
    throws CEASTparseException {
        final String nick = e.getAttribute("nom");
        final IAST2variable variable = parser.getFactory().newVariable(nick);
        final IAST2expression<CEASTparseException> value =
            (IAST2expression<CEASTparseException>)
            parser.findThenParseChildAsUnique(e, "valeur");
        return parser.getFactory().newAssignment(variable, value);
    }

    //NOTE: Accès direct aux champs interdit à partir d'ici!

    public Object eval (final ILexicalEnvironment lexenv,
                        final ICommon common )
    throws EvaluationException {
        Object newValue = getValue().eval(lexenv, common);
        try {
            lexenv.update(getVariable(), newValue);
        } catch (EvaluationException e) {
            common.updateGlobal(getVariable().getName(), newValue);
        }
        return newValue;
    }

    public void compileExpression (final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final ICgenEnvironment common,
                                   final IDestination destination)
    throws CgenerationException {
        destination.compile(buffer, lexenv, common);
        buffer.append(" (");
        getValue().compileExpression(buffer, lexenv, common,
                new AssignDestination(getVariable()) );
        buffer.append(") ");
    }

    @Override
    public void findGlobalVariables(
            final Set<IAST2variable> globalvars,
            final ICgenLexicalEnvironment lexenv ) {
        if ( ! lexenv.isPresent(getVariable()) ) {
        	globalvars.add(getVariable());
        }
        getValue().findGlobalVariables(globalvars, lexenv);
    }

}

// end of CEASTassignment.java
