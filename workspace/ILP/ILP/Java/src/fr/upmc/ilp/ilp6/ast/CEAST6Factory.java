package fr.upmc.ilp.ilp6.ast;

import fr.upmc.ilp.ilp4.ast.CEASTFactory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6instantiation;
import fr.upmc.ilp.ilp6.interfaces.IAST6methodDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6program;
import fr.upmc.ilp.ilp6.interfaces.IAST6readField;
import fr.upmc.ilp.ilp6.interfaces.IAST6self;
import fr.upmc.ilp.ilp6.interfaces.IAST6send;
import fr.upmc.ilp.ilp6.interfaces.IAST6super;
import fr.upmc.ilp.ilp6.interfaces.IAST6writeField;

public class CEAST6Factory extends CEASTFactory
implements IAST6Factory {

    public IAST6program newProgram(
            IAST4functionDefinition[] definitions,
            IAST6classDefinition[] clazzes,
            IAST4expression body ) {
        return new CEASTprogram(definitions, clazzes, body);
    }

    public IAST6classDefinition newClassDefinition(
            String className,
            String superClassName,
            String[] fieldNames,
            IAST6methodDefinition[] methodDefinitions) {
        return new CEASTclassDefinition(
                className, superClassName, fieldNames, methodDefinitions);
    }

    public IAST6instantiation newInstantiation(
            String className,
            IAST4expression[] arguments) {
        return new CEASTinstantiate(className, arguments);
    }

    public IAST6methodDefinition newMethodDefinition(
            IAST4functionDefinition function) {
        return new CEASTmethodDefinition(function);
    }
    public IAST6methodDefinition newMethodDefinition(
            String methodName,
            IAST4globalFunctionVariable gfv,
            IAST4variable[] variables,
            IAST4expression body  ) {
        return new CEASTmethodDefinition(methodName, gfv, variables, body);
    }

    public IAST6readField newReadField(
            String fieldName, IAST4expression object) {
        return new CEASTreadField(fieldName, object);
    }

    public IAST6self newSelf(IAST4variable variable) {
        return new CEASTself(variable);
    }

    public IAST6send newSend(
            String message,
            IAST4expression receiver,
            IAST4expression[] arguments) {
        return new CEASTsend(message, receiver, arguments);
        }

    public IAST6super newSuper() {
        return new CEASTsuper();
    }

    public IAST6writeField newWriteField(
            String fieldName,
            IAST4expression object,
            IAST4expression value) {
        return new CEASTwriteField(fieldName, object, value);
    }
}
