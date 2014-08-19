package fr.upmc.ilp.ilp2.interfaces;

import java.util.List;

public interface IAST2Factory<Exc extends Exception> {

    /* Créer un programme */
    IAST2program<Exc> newProgram(
            IAST2functionDefinition<Exc>[] functions,
            IAST2instruction<Exc> instruction);
    
    /** Créer une séquence d'AST. */
    IAST2sequence<Exc> newSequence(List<IAST2instruction<Exc>> asts);

    /** Créer une alternative binaire. */
    IAST2alternative<Exc> newAlternative(
            IAST2expression<Exc> condition,
            IAST2instruction<Exc> consequent);

    /** Créer une alternative ternaire. */
    IAST2alternative<Exc> newAlternative(
            IAST2expression<Exc> condition,
            IAST2instruction<Exc> consequent,
            IAST2instruction<Exc> alternant);

    /** Créer une variable. */
    IAST2variable newVariable(String name);

    /** Créer une reference à une variable. */
    IAST2reference<Exc> newReference(IAST2variable variable);

    /** Créer une invocation (un appel à une fonction). */
    IAST2invocation<Exc> newInvocation(
            IAST2expression<Exc> function,
            IAST2expression<Exc>[] arguments);

    /** Créer une invocation (un appel à une fonction predefinie). */
    IAST2primitiveInvocation<Exc> newPrimitiveInvocation(
            String primitiveName,
            IAST2expression<Exc>[] arguments);

    /** Créer une opération unaire. */
    IAST2unaryOperation<Exc> newUnaryOperation(
            String operatorName,
            IAST2expression<Exc> operand);

    /** Créer une opération binaire. */
    IAST2binaryOperation<Exc> newBinaryOperation(
            String operatorName,
            IAST2expression<Exc> leftOperand,
            IAST2expression<Exc> rightOperand);

    /** Créer une constante littérale entière. */
    IAST2integer<Exc> newIntegerConstant(String value);

    /** Créer une constante littérale flottante. */
    IAST2float<Exc> newFloatConstant(String value);

    /** Créer une constante littérale chaîne de caractères. */
    IAST2string<Exc> newStringConstant(String value);

    /** Créer une constante littérale booléenne. */
    IAST2boolean<Exc> newBooleanConstant(String value);

    /** Créer une affectation. */
    IAST2assignment<Exc> newAssignment(
            IAST2variable variable,
            IAST2expression<Exc> value);

    /** Créer un bloc local n-aire. */
    IAST2localBlock<Exc> newLocalBlock(
            IAST2variable[] variables,
            IAST2expression<Exc>[] initializations,
            IAST2instruction<Exc> body);

    /** Par compatibilite, creer un bloc local unaire. */
    IAST2unaryBlock<Exc> newUnaryBlock(
            IAST2variable variable,
            IAST2expression<Exc> initialization,
            IAST2instruction<Exc> body);

    /** Créer une boucle tant-que. */
    IAST2while<Exc> newWhile(
            IAST2expression<Exc> condition,
            IAST2instruction<Exc> body);

    /** Créer une definition de fonctions. */
    IAST2functionDefinition<Exc> newFunctionDefinition(
            String functionName,
            IAST2variable[] variables,
            IAST2instruction<Exc> body);
}
