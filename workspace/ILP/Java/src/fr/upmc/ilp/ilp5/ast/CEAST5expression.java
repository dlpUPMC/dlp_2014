package fr.upmc.ilp.ilp5.ast;

import javax.xml.parsers.ParserConfigurationException;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.ast.CEASTexpression;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionDefinition;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionVariable;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitable;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitor;

public abstract class CEAST5expression
extends CEASTexpression
implements IAST5visitable {

    @Override
    public String toString () {
        try {
            return new XMLwriter().process((IAST5visitable)this);
        } catch (ParserConfigurationException t) {
            final String msg = "Generation XML problematique: ";
            return msg + t + " " + super.toString();
        }
    }

    public static final <Data, Result, Exc extends Throwable> IAST5visitor<Data, Result, Exc>
     narrowToIAST5visitor (IAST4visitor<Data, Result, Exc> visitor) {
        if ( visitor instanceof IAST5visitor<?,?,?> ) {
            return (IAST5visitor<Data, Result, Exc>) visitor;
        } else {
            final String msg = "Not an IAST5visitor " + visitor;
            throw new ClassCastException(msg);
        }
    }

    public static IAST5localFunctionVariable
            narrowToIAST5localFunctionVariable (IAST4variable var) {
        return (IAST5localFunctionVariable) var;
    }
    public static IAST5localFunctionDefinition
            narrowToIAST5localFunctionDefinition (IAST4 iast) {
        return (IAST5localFunctionDefinition) iast;
    }

    @Override
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common,
                         final IDestination destination)
    throws CgenerationException {
        throw new RuntimeException("Not implemented!");
    }
    @Override
    public void compileExpression(
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv, 
            final ICgenEnvironment common,
            final IDestination destination) throws CgenerationException {
        compile(buffer, lexenv, common, destination);
        
    }
    @Override
    public void compileInstruction(
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv, 
            final ICgenEnvironment common,
            final IDestination destination) throws CgenerationException {
        compile(buffer, lexenv, common, destination);
    }
}
