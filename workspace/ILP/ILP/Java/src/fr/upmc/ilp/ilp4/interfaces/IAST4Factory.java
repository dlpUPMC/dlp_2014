package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp3.IAST3Factory;

public interface IAST4Factory
extends IAST3Factory<CEASTparseException>
{
    IAST4expression newVoidExpression ();
    
    IAST4program newProgram(IAST4functionDefinition[] defs, 
                            IAST4expression body);

    IAST4sequence newSequence (IAST4expression[] asts);
    
    IAST4globalFunctionVariable newGlobalFunctionVariable (
            String name );
     IAST4functionDefinition newFunctionDefinition (
            IAST4globalFunctionVariable global,
            IAST4variable[] variables,
            IAST4expression body );
    
    // On raffine ces signatures:
    
    IAST4try newTry (
            IAST4expression body,
            IAST4variable caughtExceptionVariable,
            IAST4expression catcher,
            IAST4expression finallyer);
    
    IAST4alternative newAlternative(
            IAST4expression condition,
            IAST4expression consequent);

    IAST4alternative newAlternative(
            IAST4expression condition,
            IAST4expression consequent,
            IAST4expression alternant);

    IAST4variable newVariable(String name);
    IAST4localVariable newLocalVariable(String name);
    IAST4globalVariable newGlobalVariable(String name);
    
    IAST4reference newReference(IAST4variable variable);

    IAST4assignment newAssignment(
            IAST4variable variable,
            IAST4expression value);
    IAST4localAssignment newLocalAssignment(
            IAST4localVariable variable,
            IAST4expression value);
    IAST4globalAssignment newGlobalAssignment(
            IAST4globalVariable variable,
            IAST4expression value);

    IAST4invocation newInvocation(
            IAST4expression function,
            IAST4expression[] arguments);
    IAST4computedInvocation newComputedInvocation(
            IAST4expression function,
            IAST4expression[] arguments);
    IAST4globalInvocation newGlobalInvocation(
            IAST4globalFunctionVariable function,
            IAST4expression[] arguments);
    IAST4primitiveInvocation newPrimitiveInvocation(
            IAST4globalVariable function,
            IAST4expression[] arguments);

    IAST4unaryOperation newUnaryOperation(
            String operatorName,
            IAST4expression operand);

    IAST4binaryOperation newBinaryOperation(
            String operatorName,
            IAST4expression leftOperand,
            IAST4expression rightOperand);

    IAST4integer newIntegerConstant(String value);
    IAST4float newFloatConstant(String value);
    IAST4string newStringConstant(String value);
    IAST4boolean newBooleanConstant(String value);

    IAST4localBlock newLocalBlock(
            IAST4variable[] variables,
            IAST4expression[] initializations,
            IAST4expression body);

    IAST4unaryBlock newUnaryBlock(
            IAST4variable variable,
            IAST4expression initialization,
            IAST4expression body);

    IAST4while newWhile(
            IAST4expression condition,
            IAST4expression body);
}
