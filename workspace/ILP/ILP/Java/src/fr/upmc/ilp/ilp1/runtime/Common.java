/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: Common.java 921 2010-08-18 14:41:55Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;
import java.math.BigInteger;

/** Cette interface définit les caractéristiques globales d'un
 * interprète du langage ILP1. On y trouve, notamment, la définition
 * des opérateurs du langage.
 */

public class Common implements ICommon {

  public Common () {}

  /** Les opérateurs unaires.
   *
   * Comme il n'y a que deux tels opérateurs, leur définition est
   * intégrée dans cette méthode. */

  public Object applyOperator (String opName, Object operand)
    throws EvaluationException {
    checkNotNull(opName, 1, operand);

    if ( "-".equals(opName) ) {
      if ( operand instanceof BigInteger ) {
        BigInteger bi = (BigInteger) operand;
        return bi.negate();
      } else if ( operand instanceof Double ) {
        double bd = ((Double) operand).doubleValue();
        return new Double(-bd);
      } else {
        return signalWrongType(opName, 1, operand, "number");
      }

    } else if ( "!".equals(opName) ) {
      if ( operand instanceof Boolean ) {
        Boolean b = (Boolean) operand;
        return Boolean.valueOf( ! b.booleanValue() );
      } else {
        return signalWrongType(opName, 1, operand, "boolean");
      }

    } else {
      String msg = "Unknown unary operator: " + opName;
      throw new EvaluationException(msg);
    }
  }

  /** Les opérateurs binaires.
   *
   * Cette méthode n'est qu'un grand aiguillage. */

  public Object applyOperator (String opName,
                               Object leftOperand,
                               Object rightOperand)
    throws EvaluationException
  {
    if ( "+".equals(opName) ) {
      return operatorPlus(opName, leftOperand, rightOperand);
    } else if ( "-".equals(opName) ) {
      return operatorMinus(opName, leftOperand, rightOperand);

      // continuer                                            TEMP

    } else {
      String msg = "Unknown binary operator: " + opName;
      throw new EvaluationException(msg);
    }
  }

  // Vérifications diverses:

  private void checkNotNull (String opName, int rank, Object o)
    throws EvaluationException {
    if ( o == null ) {
      String msg = opName + ": Argument " + rank + " is null!\n"
        + "Value is: " + o;
      throw new EvaluationException(msg);
    }
  }

  private Object signalWrongType (String opName, int rank, Object o,
                                  String expectedType)
    throws EvaluationException {
   String msg = opName + ": Argument " + rank + " is not "
     + expectedType + "\n" + "Value is: " + o;
    throw new EvaluationException(msg);
  }

  // {{{ Les opérateurs binaires:

  /** Le traitement de l'addition. C'est compliqué car il y a de
   * nombreuses conversions possibles. */

  private Object operatorPlus (String opName, Object a, Object b)
    throws EvaluationException {
    checkNotNull(opName, 1, a);
    checkNotNull(opName, 2, b);
    if ( a instanceof BigInteger ) {
      BigInteger bi1 = (BigInteger) a;
      if ( b instanceof BigInteger ) {
        BigInteger bi2 = (BigInteger) b;
        return bi1.add(bi2);
      } else if ( b instanceof Double ) {
        double bd1 = bi1.doubleValue();
        double bd2 = ((Double) b).doubleValue();
        return new Double(bd1 + bd2);
      } else {
        return signalWrongType(opName, 2, b, "number");
      }
    } else if ( a instanceof Double ) {
      Double bd1 = (Double) a;
      if ( b instanceof Double ) {
        Double bd2 = (Double) b;
        return new Double(bd1.doubleValue() + bd2.doubleValue());
      } else if ( b instanceof BigInteger ) {
        BigInteger bi2 = (BigInteger) b;
        double bd2 = bi2.doubleValue();
        return new Double(bd1.doubleValue() + bd2);
      } else {
        return signalWrongType(opName, 2, b, "number");
      }
    } else {
      return signalWrongType(opName, 1, a, "number");
    }
  }

  /** Le traitement de la soustraction. Ca ressemble à l'addition. */

  private Object operatorMinus (String opName, Object a, Object b)
    throws EvaluationException {
    checkNotNull(opName, 1, a);
    checkNotNull(opName, 2, b);
    if ( a instanceof BigInteger ) {
      BigInteger bi1 = (BigInteger) a;
      if ( b instanceof BigInteger ) {
        BigInteger bi2 = (BigInteger) b;
        return bi1.subtract(bi2);
      } else if ( b instanceof Double ) {
        double bd1 = bi1.doubleValue();
        double bd2 = ((Double) b).doubleValue();
        return new Double(bd1 - bd2);
      } else {
        return signalWrongType(opName, 2, b, "number");
      }
    } else if ( a instanceof Double ) {
      Double bd1 = (Double) a;
      if ( b instanceof Double ) {
        Double bd2 = (Double) b;
        return new Double(bd1.doubleValue() - bd2.doubleValue());
      } else if ( b instanceof BigInteger ) {
        BigInteger bi2 = (BigInteger) b;
        double bd2 = bi2.doubleValue();
        return new Double(bd1.doubleValue() - bd2);
      } else {
        return signalWrongType(opName, 2, b, "number");
      }
    } else {
      return signalWrongType(opName, 1, a, "number");
    }
  }

  // }}}

}

// end of Common.java
