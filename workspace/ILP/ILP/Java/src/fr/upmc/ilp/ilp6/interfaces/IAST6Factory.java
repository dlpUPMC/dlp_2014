package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;

public interface IAST6Factory
extends IAST4Factory {
    IAST6classDefinition newClassDefinition(
            String className,
            String superClassName,
            String[] fieldNames,
            IAST6methodDefinition[] methodDefinitions );
    IAST6methodDefinition newMethodDefinition(
            IAST4functionDefinition function);
    IAST6methodDefinition newMethodDefinition(
            String methodName,
            IAST4globalFunctionVariable gfv,
            IAST4variable[] variables,
            IAST4expression body  );
    IAST6instantiation newInstantiation(
            String className,
            IAST4expression[] arguments );
    IAST6readField newReadField(
            String fieldName,
            IAST4expression object );
    IAST6writeField newWriteField(
            String fieldName,
            IAST4expression object,
            IAST4expression value );
    IAST6self newSelf(IAST4variable variable);
    IAST6send newSend(
            String message,
            IAST4expression receiver,
            IAST4expression[] arguments );
    IAST6super newSuper();
    IAST6program newProgram(IAST4functionDefinition[] definitions,
                            IAST6classDefinition[] clazzes,
                            IAST4expression body);
}
