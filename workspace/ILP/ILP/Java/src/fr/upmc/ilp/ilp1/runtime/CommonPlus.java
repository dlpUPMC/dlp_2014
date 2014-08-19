// 
// ATTENTION: NE PAS MODIFIER! CETTE CLASSE A ÉTÉ ENGENDRÉE AUTOMATIQUEMENT
// A PARTIR DE BinOp.php (voir build.xml pour les détails).

/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: CommonPlus.java 1224 2012-08-27 20:07:18Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;

import java.math.BigInteger;

/** Cette classe (engendrée par PHP) implante les caractéristiques
 * générales d'un interprète du langage ILP1. On y trouve, notamment,
 * la définition des opérateurs du langage.
 */

public class CommonPlus implements ICommon {

     public CommonPlus () {}

     // Vérifications diverses:

     private void checkNotNull (final String opName, 
                                final int rank, 
                                final Object o)
          throws EvaluationException {
          if ( o == null ) {
               final String msg = opName + ": Argument " + rank + " is null!\n"
                    + "Value is: " + o;
               throw new EvaluationException(msg);
          }
     }

     private Object signalWrongType (final String opName, 
                                     final int rank, 
                                     final Object o,
                                     final String expectedType)
          throws EvaluationException {
          final String msg = opName + ": Argument " + rank + " is not "
               + expectedType + "\n" + "Value is: " + o;
          throw new EvaluationException(msg);
     }
  
     /** Les opérateurs unaires.
      *
      * Comme il n'y a que deux tels opérateurs, leur définition est
      * intégrée directement dans cette méthode plutôt que d'être
      * macro-générée. */

     public Object applyOperator (final String opName, final Object operand)
          throws EvaluationException {
          checkNotNull(opName, 1, operand);

          if ( "-".equals(opName) ) {
               if ( operand instanceof BigInteger ) {
                    final BigInteger bi = (BigInteger) operand;
                    return bi.negate();
               } else if ( operand instanceof Double ) {
                    // Profitons du déballage (unboxing) automatique:
                    // final double bd = ((Double) operand).doubleValue();
                    final double bd = (Double) operand;
                    return new Double(-bd);
               } else {
                    return signalWrongType(opName, 1, operand, "number");
               }

          } else if ( "!".equals(opName) ) {
               if ( operand instanceof Boolean ) {
                    final Boolean b = (Boolean) operand;
                    return Boolean.valueOf( ! b.booleanValue() );
               } else {
                    return signalWrongType(opName, 1, operand, "boolean");
               }

          } else {
               final String msg = "Unknown unary operator: " + opName;
               throw new EvaluationException(msg);
          }
     }

     /** Les opérateurs binaires.
      *
      * Cette méthode n'est qu'un grand aiguillage. */

     public Object applyOperator (final String opName,
                                  final Object leftOperand,
                                  final Object rightOperand)
          throws EvaluationException
          {
               // Les opérateurs:

          if ( "+".equals(opName) ) {
               return operatorPlus(opName, leftOperand, rightOperand);
          } else 
          if ( "-".equals(opName) ) {
               return operatorMinus(opName, leftOperand, rightOperand);
          } else 
          if ( "*".equals(opName) ) {
               return operatorMultiply(opName, leftOperand, rightOperand);
          } else 
          if ( "/".equals(opName) ) {
               return operatorQuotient(opName, leftOperand, rightOperand);
          } else 
          if ( "%".equals(opName) ) {
               return operatorModulo(opName, leftOperand, rightOperand);
          } else 

            // Les comparateurs:
    
          if ( "<".equals(opName) ) {
               return operatorLT(opName, leftOperand, rightOperand);
          } else 
          if ( "<=".equals(opName) ) {
               return operatorLE(opName, leftOperand, rightOperand);
          } else 
          if ( "==".equals(opName) ) {
               return operatorEQ(opName, leftOperand, rightOperand);
          } else 
          if ( ">=".equals(opName) ) {
               return operatorGE(opName, leftOperand, rightOperand);
          } else 
          if ( ">".equals(opName) ) {
               return operatorGT(opName, leftOperand, rightOperand);
          } else 
          if ( "!=".equals(opName) ) {
               return operatorNEQ(opName, leftOperand, rightOperand);
          } else 
          // L'operateur != est aussi connu sous l'alias <>
          // Cet oubli a été signalé par Cristian.Loiza_Soto@etu.upmc.fr:
          if ( "<>".equals(opName) ) {
               return operatorNEQ(opName, leftOperand, rightOperand);
          } else 

       // Les opérateurs booléens:
       if ( "&".equals(opName) ) {
            if ( leftOperand == Boolean.FALSE ) {
                 return Boolean.FALSE;
            } else {
                 return rightOperand;
            }
       } else
       if ( "|".equals(opName) ) {
            if ( leftOperand != Boolean.FALSE ) {
                 return leftOperand;
            } else {
                 return rightOperand;
            }
       } else
       if ( "^".equals(opName) ) {
            boolean left = (leftOperand != Boolean.FALSE);
            boolean right = (rightOperand != Boolean.FALSE);
            return (Boolean) (left != right);
       } else

           // NOTA: il serait plus astucieux de ranger les branches de 
           // ces alternatives par ordre d'usage décroissant!
     {
          final String msg = "Unknown binary operator: " + opName;
          throw new EvaluationException(msg);
     }
  }

     // Le cas de l'opérateur binaire + est plus compliqué car il permet
     // aussi la concaténation de chaînes de caractères. Il sera donc
     // traité à part et donc écrit à la main. C'est tout comme pour
     // modulo qui ne s'applique qu'à des entiers et non à des flottants.
     // Tous les autres opérateurs binaires seront macro-générés.

     /** Définition de l'opérateur binaire + */

     private Object operatorPlus (final String opName, 
                                  final Object a, 
                                  final Object b)
          throws EvaluationException {
          checkNotNull(opName, 1, a);
          checkNotNull(opName, 2, b);
          if ( a instanceof BigInteger ) {
               final BigInteger bi1 = (BigInteger) a;
               if ( b instanceof BigInteger ) {
                    final BigInteger bi2 = (BigInteger) b;
                    return bi1.add(bi2);
               } else if ( b instanceof Double ) {
                    final double bd1 = bi1.doubleValue();
                    //final double bd2 = ((Double) b).doubleValue();
                    final double bd2 = (Double) b;
                    return new Double(bd1 + bd2);
               } else {
                    return signalWrongType(opName, 2, b, "number");
               }
          } else if ( a instanceof Double ) {
               final Double bd1 = (Double) a;
               if ( b instanceof Double ) {
                    final Double bd2 = (Double) b;
                    //return new Double(bd1.doubleValue() + bd2.doubleValue());
                    return new Double(bd1 + bd2);
               } else if ( b instanceof BigInteger ) {
                    final BigInteger bi2 = (BigInteger) b;
                    final double bd2 = bi2.doubleValue();
                    return new Double(bd1.doubleValue() + bd2);
               } else {
                    return signalWrongType(opName, 2, b, "number");
               }
          } else if ( a instanceof String ) {
               final String sa = (String) a;
               if ( b instanceof String ) {
                    final String sb = (String) b;
                    return sa + sb;
               } else {
                    return signalWrongType(opName, 2, b, "string");
               }
          } else {
               return signalWrongType(opName, 1, a, "number");
          }
     }

     /** Définition de l'opérateur binaire modulo % */

     private Object operatorModulo (final String opName, 
                                    final Object a, 
                                    final Object b)
          throws EvaluationException {
          checkNotNull(opName, 1, a);
          checkNotNull(opName, 2, b);
          if ( a instanceof BigInteger ) {
               final BigInteger bi1 = (BigInteger) a;
               if ( b instanceof BigInteger ) {
                    final BigInteger bi2 = (BigInteger) b;
                    return bi1.mod(bi2);
               } else {
                    return signalWrongType(opName, 2, b, "integer");
               }
          } else {
               return signalWrongType(opName, 1, a, "integer");
          }
     }

     // Les opérateurs binaires (engendrés par PHP)
  

          /** Définition de l'opérateur binaire -              à l'aide de BigInteger.subtract.  */

          private Object operatorMinus (final String opName, final Object a, final Object b)
          throws EvaluationException {
               checkNotNull(opName, 1, a);
               checkNotNull(opName, 2, b);
               if ( a instanceof BigInteger ) {
                    final BigInteger bi1 = (BigInteger) a;
                    if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         return bi1.subtract(bi2);
                    } else if ( b instanceof Double ) {
                         final double bd1 = bi1.doubleValue();
                         //final double bd2 = ((Double) b).doubleValue();
                         final double bd2 = (Double) b;
                         return new Double(bd1 - bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else if ( a instanceof Double ) {
                    final Double bd1 = (Double) a;
                    if ( b instanceof Double ) {
                         final Double bd2 = (Double) b;
                         //return new Double(bd1.doubleValue() - bd2.doubleValue());
                         return new Double(bd1 - bd2);
                    } else if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         final double bd2 = bi2.doubleValue();
                         //return new Double(bd1.doubleValue() - bd2);
                         return new Double(bd1 - bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else {
                    return signalWrongType(opName, 1, a, "number");
               }
          }

          /** Définition de l'opérateur binaire *              à l'aide de BigInteger.multiply.  */

          private Object operatorMultiply (final String opName, final Object a, final Object b)
          throws EvaluationException {
               checkNotNull(opName, 1, a);
               checkNotNull(opName, 2, b);
               if ( a instanceof BigInteger ) {
                    final BigInteger bi1 = (BigInteger) a;
                    if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         return bi1.multiply(bi2);
                    } else if ( b instanceof Double ) {
                         final double bd1 = bi1.doubleValue();
                         //final double bd2 = ((Double) b).doubleValue();
                         final double bd2 = (Double) b;
                         return new Double(bd1 * bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else if ( a instanceof Double ) {
                    final Double bd1 = (Double) a;
                    if ( b instanceof Double ) {
                         final Double bd2 = (Double) b;
                         //return new Double(bd1.doubleValue() * bd2.doubleValue());
                         return new Double(bd1 * bd2);
                    } else if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         final double bd2 = bi2.doubleValue();
                         //return new Double(bd1.doubleValue() * bd2);
                         return new Double(bd1 * bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else {
                    return signalWrongType(opName, 1, a, "number");
               }
          }

          /** Définition de l'opérateur binaire /              à l'aide de BigInteger.divide.  */

          private Object operatorQuotient (final String opName, final Object a, final Object b)
          throws EvaluationException {
               checkNotNull(opName, 1, a);
               checkNotNull(opName, 2, b);
               if ( a instanceof BigInteger ) {
                    final BigInteger bi1 = (BigInteger) a;
                    if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         return bi1.divide(bi2);
                    } else if ( b instanceof Double ) {
                         final double bd1 = bi1.doubleValue();
                         //final double bd2 = ((Double) b).doubleValue();
                         final double bd2 = (Double) b;
                         return new Double(bd1 / bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else if ( a instanceof Double ) {
                    final Double bd1 = (Double) a;
                    if ( b instanceof Double ) {
                         final Double bd2 = (Double) b;
                         //return new Double(bd1.doubleValue() / bd2.doubleValue());
                         return new Double(bd1 / bd2);
                    } else if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         final double bd2 = bi2.doubleValue();
                         //return new Double(bd1.doubleValue() / bd2);
                         return new Double(bd1 / bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else {
                    return signalWrongType(opName, 1, a, "number");
               }
          }
 
       // Les comparateurs binaires (engendrés par PHP)
  

          /** Définition de l'opérateur binaire LT              à l'aide de BigInteger.compareTo().  */

          private Object operatorLT (final String opName, final Object a, final Object b)
               throws EvaluationException {
               checkNotNull(opName, 1, a);
               checkNotNull(opName, 2, b);
               if ( a instanceof BigInteger ) {
                    final BigInteger bi1 = (BigInteger) a;
                    if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         return Boolean.valueOf(bi1.compareTo(bi2) < 0);
                    } else if ( b instanceof Double ) {
                         final double bd1 = bi1.doubleValue();
                         //final double bd2 = ((Double) b).doubleValue();
                         final double bd2 = (Double) b;
                         return Boolean.valueOf(bd1 < bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else if ( a instanceof Double ) {
                    final Double bd1 = (Double) a;
                    if ( b instanceof Double ) {
                         final Double bd2 = (Double) b;
                         //return Boolean.valueOf(bd1.doubleValue() < bd2.doubleValue());
                         return Boolean.valueOf(bd1 < bd2);
                    } else if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         final double bd2 = bi2.doubleValue();
                         return Boolean.valueOf(bd1.doubleValue() < bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else {
                    return signalWrongType(opName, 1, a, "number");
               }
          }

          /** Définition de l'opérateur binaire LE              à l'aide de BigInteger.compareTo().  */

          private Object operatorLE (final String opName, final Object a, final Object b)
               throws EvaluationException {
               checkNotNull(opName, 1, a);
               checkNotNull(opName, 2, b);
               if ( a instanceof BigInteger ) {
                    final BigInteger bi1 = (BigInteger) a;
                    if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         return Boolean.valueOf(bi1.compareTo(bi2) <= 0);
                    } else if ( b instanceof Double ) {
                         final double bd1 = bi1.doubleValue();
                         //final double bd2 = ((Double) b).doubleValue();
                         final double bd2 = (Double) b;
                         return Boolean.valueOf(bd1 <= bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else if ( a instanceof Double ) {
                    final Double bd1 = (Double) a;
                    if ( b instanceof Double ) {
                         final Double bd2 = (Double) b;
                         //return Boolean.valueOf(bd1.doubleValue() <= bd2.doubleValue());
                         return Boolean.valueOf(bd1 <= bd2);
                    } else if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         final double bd2 = bi2.doubleValue();
                         return Boolean.valueOf(bd1.doubleValue() <= bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else {
                    return signalWrongType(opName, 1, a, "number");
               }
          }

          /** Définition de l'opérateur binaire EQ              à l'aide de BigInteger.compareTo().  */

          private Object operatorEQ (final String opName, final Object a, final Object b)
               throws EvaluationException {
               checkNotNull(opName, 1, a);
               checkNotNull(opName, 2, b);
               if ( a instanceof BigInteger ) {
                    final BigInteger bi1 = (BigInteger) a;
                    if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         return Boolean.valueOf(bi1.compareTo(bi2) == 0);
                    } else if ( b instanceof Double ) {
                         final double bd1 = bi1.doubleValue();
                         //final double bd2 = ((Double) b).doubleValue();
                         final double bd2 = (Double) b;
                         return Boolean.valueOf(bd1 == bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else if ( a instanceof Double ) {
                    final Double bd1 = (Double) a;
                    if ( b instanceof Double ) {
                         final Double bd2 = (Double) b;
                         return Boolean.valueOf(bd1.doubleValue() == bd2.doubleValue());
                    } else if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         final double bd2 = bi2.doubleValue();
                         return Boolean.valueOf(bd1.doubleValue() == bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else {
                    return signalWrongType(opName, 1, a, "number");
               }
          }

          /** Définition de l'opérateur binaire GE              à l'aide de BigInteger.compareTo().  */

          private Object operatorGE (final String opName, final Object a, final Object b)
               throws EvaluationException {
               checkNotNull(opName, 1, a);
               checkNotNull(opName, 2, b);
               if ( a instanceof BigInteger ) {
                    final BigInteger bi1 = (BigInteger) a;
                    if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         return Boolean.valueOf(bi1.compareTo(bi2) >= 0);
                    } else if ( b instanceof Double ) {
                         final double bd1 = bi1.doubleValue();
                         //final double bd2 = ((Double) b).doubleValue();
                         final double bd2 = (Double) b;
                         return Boolean.valueOf(bd1 >= bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else if ( a instanceof Double ) {
                    final Double bd1 = (Double) a;
                    if ( b instanceof Double ) {
                         final Double bd2 = (Double) b;
                         //return Boolean.valueOf(bd1.doubleValue() >= bd2.doubleValue());
                         return Boolean.valueOf(bd1 >= bd2);
                    } else if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         final double bd2 = bi2.doubleValue();
                         return Boolean.valueOf(bd1.doubleValue() >= bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else {
                    return signalWrongType(opName, 1, a, "number");
               }
          }

          /** Définition de l'opérateur binaire GT              à l'aide de BigInteger.compareTo().  */

          private Object operatorGT (final String opName, final Object a, final Object b)
               throws EvaluationException {
               checkNotNull(opName, 1, a);
               checkNotNull(opName, 2, b);
               if ( a instanceof BigInteger ) {
                    final BigInteger bi1 = (BigInteger) a;
                    if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         return Boolean.valueOf(bi1.compareTo(bi2) > 0);
                    } else if ( b instanceof Double ) {
                         final double bd1 = bi1.doubleValue();
                         //final double bd2 = ((Double) b).doubleValue();
                         final double bd2 = (Double) b;
                         return Boolean.valueOf(bd1 > bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else if ( a instanceof Double ) {
                    final Double bd1 = (Double) a;
                    if ( b instanceof Double ) {
                         final Double bd2 = (Double) b;
                         //return Boolean.valueOf(bd1.doubleValue() > bd2.doubleValue());
                         return Boolean.valueOf(bd1 > bd2);
                    } else if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         final double bd2 = bi2.doubleValue();
                         return Boolean.valueOf(bd1.doubleValue() > bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else {
                    return signalWrongType(opName, 1, a, "number");
               }
          }

          /** Définition de l'opérateur binaire NEQ              à l'aide de BigInteger.compareTo().  */

          private Object operatorNEQ (final String opName, final Object a, final Object b)
               throws EvaluationException {
               checkNotNull(opName, 1, a);
               checkNotNull(opName, 2, b);
               if ( a instanceof BigInteger ) {
                    final BigInteger bi1 = (BigInteger) a;
                    if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         return Boolean.valueOf(bi1.compareTo(bi2) != 0);
                    } else if ( b instanceof Double ) {
                         final double bd1 = bi1.doubleValue();
                         //final double bd2 = ((Double) b).doubleValue();
                         final double bd2 = (Double) b;
                         return Boolean.valueOf(bd1 != bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else if ( a instanceof Double ) {
                    final Double bd1 = (Double) a;
                    if ( b instanceof Double ) {
                         final Double bd2 = (Double) b;
                         return Boolean.valueOf(bd1.doubleValue() != bd2.doubleValue());
                    } else if ( b instanceof BigInteger ) {
                         final BigInteger bi2 = (BigInteger) b;
                         final double bd2 = bi2.doubleValue();
                         return Boolean.valueOf(bd1.doubleValue() != bd2);
                    } else {
                         return signalWrongType(opName, 2, b, "number");
                    }
               } else {
                    return signalWrongType(opName, 1, a, "number");
               }
          }
 
}
  
// fin de génération de CommonPlus.java
