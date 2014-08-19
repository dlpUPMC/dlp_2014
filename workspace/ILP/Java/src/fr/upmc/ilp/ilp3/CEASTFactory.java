package fr.upmc.ilp.ilp3;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2program;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;

public class CEASTFactory
extends fr.upmc.ilp.ilp2.ast.CEASTFactory
implements IAST3Factory<CEASTparseException> {

    public CEASTFactory () {
        super();
    }
    
    @Override
    public IAST2program<CEASTparseException> newProgram(
            IAST2functionDefinition<CEASTparseException>[] functions,
            IAST2instruction<CEASTparseException> instruction) {
        return new CEASTprogram(functions, instruction);
    }

    public IAST3try<CEASTparseException> newTry(
            IAST2instruction<CEASTparseException> body,
            IAST2variable caughtExceptionVariable,
            IAST2instruction<CEASTparseException> catcher,
            IAST2instruction<CEASTparseException> finallyer) {
        return new CEASTtry(body, caughtExceptionVariable, catcher, finallyer);
    }
}
