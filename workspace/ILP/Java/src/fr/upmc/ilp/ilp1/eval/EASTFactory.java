/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:EASTFactory.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;
import java.util.List;

/** Une fabrique pour fabriquer des EAST. */

public class EASTFactory implements IEASTFactory<EASTException> {

  public EASTprogram newProgram (List<EAST> asts) {
      return new EASTprogram(asts);
  }
  
  /** Créer une séquence d'AST. */
  public EASTsequence newSequence (List<EAST> asts) {
    return new EASTsequence(asts);
  }

  /** Créer une alternative binaire. */
  public EASTalternative newAlternative (
          EAST condition,
          EAST consequent) {
    return new EASTalternative(condition, consequent);
  }

  /** Créer une alternative ternaire. */
  public EASTalternative newAlternative (
          EAST condition,
          EAST consequent,
          EAST alternant) {
    return new EASTalternative(condition, consequent, alternant);
  }

  /** Créer un bloc local unaire (avec une seule variable locale). */
  public EASTunaryBlock newUnaryBlock (
          EASTvariable variable,
          EAST initialisation,
          EASTsequence body) {
    return new EASTunaryBlock(variable, initialisation, body);
  }

  /** Créer une variable. */
  public EASTvariable newVariable (String name) {
    return new EASTvariable(name);
  }

  /** Créer une invocation (un appel à une fonction). */
  public EASTinvocation newInvocation (String name, List<EAST> asts) {
    return new EASTinvocationPrimitive(name, asts);
  }

  /** Créer une opération unaire. */
  public EASTunaryOperation newUnaryOperation (String operatorName,
                                               EAST operand) {
    return new EASTunaryOperation(operatorName, operand);
  }

  /** Créer une opération binaire. */
  public EASTbinaryOperation newBinaryOperation (String operatorName,
                                                 EAST leftOperand,
                                                 EAST rightOperand) {
    return new EASTbinaryOperation(operatorName, leftOperand, rightOperand);
  }

  /** Créer une constante littérale entière. */
  public EASTinteger newIntegerConstant (String value) {
    return new EASTinteger(value);
  }

  /** Créer une constante littérale flottante. */
  public EASTfloat newFloatConstant (String value) {
    return new EASTfloat(value);
  }

  /** Créer une constante littérale chaîne de caractères. */
  public EASTstring newStringConstant (String value) {
    return new EASTstring(value);
  }

  /** Créer une constante littérale booléenne. */
  public EASTboolean newBooleanConstant (String value) {
    return new EASTboolean(value);
  }

  /** Signaler un problème avec un message. */
  public EAST throwParseException (String message)
    throws EASTException {
    throw new EASTException(message);
  }

  /** Signaler un problème avec une exception. */
  public EAST throwParseException (Throwable cause)
    throws EASTException {
    throw new EASTException(cause);
  }

}

// end of EASTFactory.java
