package fr.upmc.ilp.ilp2.ast;

import java.util.List;

import fr.upmc.ilp.ilp2.interfaces.IAST2Factory;
import fr.upmc.ilp.ilp2.interfaces.IAST2alternative;
import fr.upmc.ilp.ilp2.interfaces.IAST2assignment;
import fr.upmc.ilp.ilp2.interfaces.IAST2binaryOperation;
import fr.upmc.ilp.ilp2.interfaces.IAST2boolean;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2float;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2integer;
import fr.upmc.ilp.ilp2.interfaces.IAST2invocation;
import fr.upmc.ilp.ilp2.interfaces.IAST2localBlock;
import fr.upmc.ilp.ilp2.interfaces.IAST2primitiveInvocation;
import fr.upmc.ilp.ilp2.interfaces.IAST2program;
import fr.upmc.ilp.ilp2.interfaces.IAST2reference;
import fr.upmc.ilp.ilp2.interfaces.IAST2sequence;
import fr.upmc.ilp.ilp2.interfaces.IAST2string;
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryBlock;
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryOperation;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.IAST2while;

public class CEASTFactory
implements IAST2Factory<CEASTparseException> {

    public CEASTFactory () {}
    
    public IAST2program<CEASTparseException> newProgram(
            IAST2functionDefinition<CEASTparseException>[] functions,
            IAST2instruction<CEASTparseException> instruction) {
        return new CEASTprogram(functions, instruction);
    }

    public IAST2alternative<CEASTparseException> newAlternative(
            IAST2expression<CEASTparseException> condition,
            IAST2instruction<CEASTparseException> consequent,
            IAST2instruction<CEASTparseException> alternant) {
        return new CEASTalternative(condition, consequent, alternant);
    }

    public IAST2alternative<CEASTparseException> newAlternative(
            IAST2expression<CEASTparseException> condition,
            IAST2instruction<CEASTparseException> consequent) {
        return new CEASTalternative(condition, consequent);
    }

    public IAST2assignment<CEASTparseException> newAssignment(
            IAST2variable variable, IAST2expression<CEASTparseException> value) {
        return new CEASTassignment(variable, value);
    }

    public IAST2binaryOperation<CEASTparseException> newBinaryOperation(
            String operatorName,
            IAST2expression<CEASTparseException> leftOperand,
            IAST2expression<CEASTparseException> rightOperand) {
        return new CEASTbinaryOperation(operatorName, leftOperand, rightOperand);
    }

    public IAST2boolean<CEASTparseException> newBooleanConstant(String value) {
        return new CEASTboolean(value);
    }

    public IAST2float<CEASTparseException> newFloatConstant(String value) {
        return new CEASTfloat(value);
    }

    public IAST2functionDefinition<CEASTparseException> newFunctionDefinition(
            String functionName,
            IAST2variable[] variables,
            IAST2instruction<CEASTparseException> body) {
        return new CEASTfunctionDefinition(functionName, variables, body);
    }

    public IAST2integer<CEASTparseException> newIntegerConstant(String value) {
        return new CEASTinteger(value);
    }

    public IAST2invocation<CEASTparseException> newInvocation(
            IAST2expression<CEASTparseException> function,
            IAST2expression<CEASTparseException>[] arguments) {
        return new CEASTinvocation(function, arguments);
    }

    public IAST2localBlock<CEASTparseException> newLocalBlock(
            IAST2variable[] variables,
            IAST2expression<CEASTparseException>[] initializations,
            IAST2instruction<CEASTparseException> body) {
        return new CEASTlocalBlock(variables, initializations, body);
    }

    public IAST2primitiveInvocation<CEASTparseException> newPrimitiveInvocation(
            String primitiveName,
            IAST2expression<CEASTparseException>[] arguments) {
        return new CEASTprimitiveInvocation(primitiveName, arguments);
    }

    public IAST2reference<CEASTparseException> newReference(
            IAST2variable variable) {
        return new CEASTreference(variable);
    }

    public IAST2sequence<CEASTparseException> newSequence(
            List<IAST2instruction<CEASTparseException>> asts) {
        return new CEASTsequence(asts);
    }

    public IAST2string<CEASTparseException> newStringConstant(String value) {
        return new CEASTstring(value);
    }

    public IAST2unaryBlock<CEASTparseException> newUnaryBlock(
            IAST2variable variable,
            IAST2expression<CEASTparseException> initialization,
            IAST2instruction<CEASTparseException> body) {
        return new CEASTunaryBlock(variable, initialization, body);
    }

    public IAST2unaryOperation<CEASTparseException> newUnaryOperation(
            String operatorName, IAST2expression<CEASTparseException> operand) {
        return new CEASTunaryOperation(operatorName, operand);
    }

    public IAST2variable newVariable(String name) {
        return new CEASTvariable(name);
    }

    public IAST2while<CEASTparseException> newWhile(
            IAST2expression<CEASTparseException> condition,
            IAST2instruction<CEASTparseException> body) {
        return new CEASTwhile(condition, body);
    }
}
