package fr.upmc.ilp.ilp2.ast;

import java.util.Set;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2reference;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

public class CEASTreference
extends CEASTexpression
implements IAST2reference<CEASTparseException> {

    public CEASTreference(IAST2variable variable) {
        this.variable = variable;
    }
    private final IAST2variable variable;

    public IAST2variable getVariable() {
        return this.variable;
    }

    public static IAST2reference<CEASTparseException> parse (
            final Element e, final IParser<CEASTparseException> parser) {
        String name = e.getAttribute("nom");
        IAST2variable variable = parser.getFactory().newVariable(name);
        return parser.getFactory().newReference(variable);
    }

    //NOTE: Accès direct aux champs interdit à partir d'ici!

    public Object eval (final ILexicalEnvironment lexenv,
                        final ICommon common )
    throws EvaluationException {
        try {
          return lexenv.lookup(getVariable());
        } catch (Exception e) {
          return common.globalLookup(getVariable());
        }
    }

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
            String v = common.compileGlobal(getVariable());
            //if ( common.isPresent(getVariable().getName())) {
            //    // FUTUR Separer les variables globales des fonctions globales
            //    buffer.append("ILP_globalIfInitialized(" + v + ")");
            //} else {
                buffer.append(v);
            //}
        }
        buffer.append(" ");
    }

    /** Une variable est globale si elle n'est ni locale, ni predefinie. */

    @Override
    public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                   final ICgenLexicalEnvironment lexenv ) {
        if ( ! lexenv.isPresent(getVariable()) ) {
        	globalvars.add(getVariable());
        }
    }
}
