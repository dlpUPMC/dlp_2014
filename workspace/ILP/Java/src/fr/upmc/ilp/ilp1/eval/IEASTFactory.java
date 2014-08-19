/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IEASTFactory.java 1299 2013-08-27 07:09:39Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;
import java.util.List;

/** Cette interface décrit une fabrique d'AST conforme aux IAST. Elle
 * est notamment utilisée par le convertisseur générique de DOM en
 * IAST (eval/EASTParser). Le resultat appartient a la famille des EAST.
 */

public interface IEASTFactory<Exc extends Exception> {

  /** créer un nouveau programme. */
  EASTprogram newProgram(List<EAST> asts);

  /** Créer une séquence d'AST. */
  EASTsequence newSequence (List<EAST> asts);

  /** Créer une alternative binaire. */
  EASTalternative newAlternative (EAST condition,
                                  EAST consequent);

  /** Créer une alternative ternaire. */
  EASTalternative newAlternative (EAST condition,
                                  EAST consequent,
                                  EAST alternant);

  /** Créer un bloc local unaire (avec une seule variable locale). */
  EASTunaryBlock newUnaryBlock (EASTvariable variable,
                                EAST initialisation,
                                EASTsequence body);

  /** Créer une variable. */
  EASTvariable newVariable (String name);

  /** Créer une invocation (un appel à une fonction). */
  EASTinvocation newInvocation (String name, List<EAST> asts);

  /** Créer une opération unaire. */
  EASTunaryOperation newUnaryOperation (String operatorName,
                                        EAST operand);
  /** Créer une opération binaire. */
  EASTbinaryOperation newBinaryOperation (String operatorName,
                                          EAST leftOperand,
                                          EAST rightOperand);

  /** Créer une constante littérale entière. */
  EASTinteger newIntegerConstant (String value);

  /** Créer une constante littérale flottante. */
  EASTfloat newFloatConstant (String value);

  /** Créer une constante littérale chaîne de caractères. */
  EASTstring newStringConstant (String value);

  /** Créer une constante littérale booléenne. */
  EASTboolean newBooleanConstant (String value);

  /** Signaler un problème avec un message. */
  EAST throwParseException (String message) throws Exc;

  /** Signaler un problème avec une exception. */
  EAST throwParseException (Throwable cause) throws Exc;


  /* NOTA: on ne peut exprimer en Java que ces deux dernières méthodes
   * signalent toujours une exception et ne renvoient donc jamais une
   * valeur. Comme on ne peut écrire ni:
   *          factory.throwParseException("blabla");
   *  [car alors la méthode englobante manque d'un return]
   *                                 ni:
   *          return factory.throwParseException("blabla");
   *  [car on ne peut renvoyer void]
   * je change le type de retour pour EAST. */
}

// end of IEASTFactory.java
