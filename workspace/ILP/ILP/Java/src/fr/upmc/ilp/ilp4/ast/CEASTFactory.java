package fr.upmc.ilp.ilp4.ast;

import java.util.List;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4alternative;
import fr.upmc.ilp.ilp4.interfaces.IAST4assignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4binaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4boolean;
import fr.upmc.ilp.ilp4.interfaces.IAST4computedInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4float;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4integer;
import fr.upmc.ilp.ilp4.interfaces.IAST4invocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4localAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4localBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4primitiveInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4reference;
import fr.upmc.ilp.ilp4.interfaces.IAST4sequence;
import fr.upmc.ilp.ilp4.interfaces.IAST4string;
import fr.upmc.ilp.ilp4.interfaces.IAST4try;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4while;

/**
 * Une implantation de la fabrique pour ILP4. Comme l'on doit implanter
 * IAST4Factory, on doit aussi implanter des methodes prenant des IAST2*
 * que l'on redirige, apres verification, vers les methodes d'ilp4.
 */

public class CEASTFactory
implements IAST4Factory {
    
    public IAST4program newProgram(
            IAST2functionDefinition<CEASTparseException>[] defs, 
            IAST2instruction<CEASTparseException> body) {
        return this.newProgram(
                CEAST.narrowToIAST4functionDefinitionArray(defs), 
                CEAST.narrowToIAST4expression(body));
    }
    public IAST4program newProgram(
            IAST4functionDefinition[] defs, 
            IAST4expression body) {
        return new CEASTprogram(defs, body);
    }
    
    public IAST4variable newVariable(String name) {
        return new CEASTvariable(name);
    }
    public IAST4globalFunctionVariable newGlobalFunctionVariable(
            String name ) {
        return new CEASTglobalFunctionVariable(name);
    }
    public IAST4localVariable newLocalVariable(String name) {
        return new CEASTlocalVariable(name);
    }
    public IAST4globalVariable newGlobalVariable(String name) {
        return new CEASTglobalVariable(name);
    }

    public IAST4assignment newAssignment(IAST4variable variable,
                                         IAST4expression value) {
        return new CEASTassignment(variable, value);
    }
    public IAST4localAssignment newLocalAssignment(
            IAST4localVariable variable,
            IAST4expression value) {
        return new CEASTlocalAssignment(variable, value);
    }
    public IAST4globalAssignment newGlobalAssignment(
            IAST4globalVariable variable,
            IAST4expression value) {
        return new CEASTglobalAssignment(variable, value);
    }
    public IAST4assignment newAssignment(
            IAST2variable variable, IAST2expression<CEASTparseException> value) {
        return new CEASTassignment(
                CEAST.narrowToIAST4variable(variable),
                CEAST.narrowToIAST4expression(value) );
    }

    public IAST4expression newVoidExpression () {
        return new CEASTboolean("false");
    }

    public IAST4alternative newAlternative(IAST4expression condition,
                                           IAST4expression consequence) {
        IAST4expression something = newVoidExpression();
        return new CEASTalternative(condition, consequence, something);
    }
    public IAST4alternative newAlternative(
            IAST4expression condition,
            IAST4expression consequence,
            IAST4expression alternant) {
        return new CEASTalternative(condition, consequence, alternant);
    }
    public IAST4alternative newAlternative(
            IAST2expression<CEASTparseException> condition,
            IAST2instruction<CEASTparseException> consequent) {
        return new CEASTalternative(
                CEAST.narrowToIAST4expression(condition),
                CEAST.narrowToIAST4expression(consequent) );
    }
    public IAST4alternative newAlternative(
            IAST2expression<CEASTparseException> condition,
            IAST2instruction<CEASTparseException> consequent,
            IAST2instruction<CEASTparseException> alternant) {
        return new CEASTalternative(
                CEAST.narrowToIAST4expression(condition),
                CEAST.narrowToIAST4expression(consequent),
                CEAST.narrowToIAST4expression(alternant) );
    }

    public IAST4unaryOperation newUnaryOperation(
            String operatorName,
            IAST4expression operand) {
        return new CEASTunaryOperation(operatorName, operand);
    }
    public IAST4unaryOperation newUnaryOperation(
            String operatorName, IAST2expression<CEASTparseException> operand) {
        return new CEASTunaryOperation(
                operatorName,
                CEAST.narrowToIAST4expression(operand));
    }

    public IAST4binaryOperation newBinaryOperation(
            String operatorName,
            IAST4expression leftOperand,
            IAST4expression rightOperand) {
        return new CEASTbinaryOperation(operatorName, leftOperand, rightOperand);
    }
    public IAST4binaryOperation newBinaryOperation(
            String operatorName,
            IAST2expression<CEASTparseException> leftOperand,
            IAST2expression<CEASTparseException> rightOperand) {
        return new CEASTbinaryOperation(
                operatorName,
                CEAST.narrowToIAST4expression(leftOperand),
                CEAST.narrowToIAST4expression(rightOperand) );
    }

    public IAST4boolean newBooleanConstant(String value) {
        return new CEASTboolean(value);
    }

    public IAST4float newFloatConstant(String value) {
        return new CEASTfloat(value);
    }

    public IAST4integer newIntegerConstant(String value) {
        return new CEASTinteger(value);
    }

    public IAST4string newStringConstant(String value) {
        return new CEASTstring(value);
    }

    public IAST4functionDefinition newFunctionDefinition(
            IAST4globalFunctionVariable global,
            IAST4variable[] variables,
            IAST4expression body) {
        return new CEASTfunctionDefinition(global, variables, body);
    }
    public IAST4functionDefinition newFunctionDefinition(
            String functionName, 
            IAST2variable[] variables,
            IAST2instruction<CEASTparseException> body) {
        return new CEASTfunctionDefinition(
                this.newGlobalFunctionVariable(functionName),
                CEAST.narrowToIAST4variableArray(variables),
                CEAST.narrowToIAST4expression(body) );
    }

    public IAST4invocation newInvocation(IAST4expression function,
                                         IAST4expression[] arguments) {
        return new CEASTinvocation(function, arguments);
    }
    public IAST4computedInvocation newComputedInvocation(
            IAST4expression function,
            IAST4expression[] arguments) {
        return new CEASTcomputedInvocation(function, arguments);
    }
    public IAST4globalInvocation newGlobalInvocation(
            IAST4globalFunctionVariable function,
            IAST4expression[] arguments) {
        return new CEASTglobalInvocation(function, arguments);
    }
    public IAST4invocation newInvocation(
            IAST2expression<CEASTparseException> function,
            IAST2expression<CEASTparseException>[] arguments) {
        return new CEASTinvocation(
                CEAST.narrowToIAST4expression(function),
                CEAST.narrowToIAST4expressionArray(arguments) );
    }

    public IAST4localBlock newLocalBlock(IAST4variable[] variables,
                                         IAST4expression[] initializations,
                                         IAST4expression body ) {
        return new CEASTlocalBlock(variables, initializations, body);
    }
    public IAST4localBlock newLocalBlock(
            IAST2variable[] variables,
            IAST2expression<CEASTparseException>[] initializations,
            IAST2instruction<CEASTparseException> body) {
        return new CEASTlocalBlock(
                CEAST.narrowToIAST4variableArray(variables),
                CEAST.narrowToIAST4expressionArray(initializations),
                CEAST.narrowToIAST4expression(body) );
    }

    public IAST4unaryBlock newUnaryBlock(
            IAST4variable variable,
            IAST4expression initialization,
            IAST4expression body) {
        return new CEASTunaryBlock(variable, initialization, body);
    }
    public IAST4unaryBlock newUnaryBlock(
            IAST2variable variable,
            IAST2expression<CEASTparseException> initialization,
            IAST2instruction<CEASTparseException> body) {
        return new CEASTunaryBlock(
                CEAST.narrowToIAST4variable(variable),
                CEAST.narrowToIAST4expression(initialization),
                CEAST.narrowToIAST4expression(body) );
    }

    public IAST4reference newReference(IAST4variable variable) {
        return new CEASTreference(variable);
    }
    public IAST4reference newReference(
            IAST2variable variable) {
        return new CEASTreference(
                CEAST.narrowToIAST4variable(variable) );
    }

    public IAST4sequence newSequence(IAST4expression[] asts) {
        return new CEASTsequence(asts);
    }
    public IAST4sequence newSequence(
            List<IAST2instruction<CEASTparseException>> asts) {
        return new CEASTsequence(
                asts.toArray(new IAST4expression[0]) );
    }

    public IAST4try newTry(IAST4expression body,
                           IAST4variable caughtExceptionVariable,
                           IAST4expression catcher,
                           IAST4expression finallyer) {
        return new CEASTtry(body, caughtExceptionVariable, catcher, finallyer);
    }
    public IAST4try newTry(
                IAST2instruction<CEASTparseException> body,
                IAST2variable caughtExceptionVariable,
                IAST2instruction<CEASTparseException> catcher,
                IAST2instruction<CEASTparseException> finallyer) {
        return new CEASTtry(
                CEAST.narrowToIAST4expression(body),
                (caughtExceptionVariable == null) ? null
                 : CEAST.narrowToIAST4variable(caughtExceptionVariable),
                (catcher == null) ? null
                 : CEAST.narrowToIAST4expression(catcher),
                (finallyer == null) ? null
                 : CEAST.narrowToIAST4expression(finallyer) );
    }

    public IAST4while newWhile(IAST4expression condition,
                               IAST4expression body) {
        return new CEASTwhile(condition, body);
    }
    public IAST4while newWhile(
            IAST2expression<CEASTparseException> condition,
            IAST2instruction<CEASTparseException> body) {
        return new CEASTwhile(
                CEAST.narrowToIAST4expression(condition),
                CEAST.narrowToIAST4expression(body) );
    }

    public IAST4primitiveInvocation newPrimitiveInvocation(
            IAST4globalVariable gv,
            IAST4expression[] arguments) {
        return new CEASTprimitiveInvocation(gv, arguments);
    } 
    public IAST4primitiveInvocation newPrimitiveInvocation(
            String primitiveName,
            IAST2expression<CEASTparseException>[] arguments) {
        return new CEASTprimitiveInvocation(
                this.newGlobalVariable(primitiveName),
                CEAST.narrowToIAST4expressionArray(arguments));
    }
}
