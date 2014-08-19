/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CgenEnvironment.java 1233 2012-09-02 16:24:43Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.cgen;

import java.util.*;

import fr.upmc.ilp.ilp1.interfaces.*;

/** La représentation de l'environnement des opérateurs prédéfinis. Il
 * définit comment les compiler. C'est un peu l'analogue de
 * runtime/Common pour le paquetage cgen. */

public class CgenEnvironment
  implements ICgenEnvironment {

  private static final Map<String,String> MAP_OP1 = new HashMap<>();
  private static final Map<String,String> MAP_OP2 = new HashMap<>();
  static {
    // Binaires:
    MAP_OP2.put("+", "Plus");
    MAP_OP2.put("-", "Minus");
    MAP_OP2.put("*", "Times");
    MAP_OP2.put("/", "Divide");
    MAP_OP2.put("%", "Modulo");
    MAP_OP2.put("<", "LessThan");
    MAP_OP2.put("<=", "LessThanOrEqual");
    MAP_OP2.put("==", "Equal");
    MAP_OP2.put(">=", "GreaterThanOrEqual");
    MAP_OP2.put(">", "GreaterThan");
    MAP_OP2.put("!=", "NotEqual");
    // Unaires:
    MAP_OP1.put("-", "Opposite");
    MAP_OP1.put("!", "Not");
  }

  /** Comment convertir un opérateur unaire en C. */

  @Override
public String  compileOperator1 (final String opName)
    throws CgenerationException {
    return compileOperator(opName, MAP_OP1);
  }

  /** Comment convertir un opérateur binaire en C. */

  @Override
public String compileOperator2 (final String opName)
    throws CgenerationException {
    return compileOperator(opName, MAP_OP2);
  }

  /** Méthode interne pour trouver le nom en C d'un opérateur. */

  private String compileOperator (final String opName, Map<String,String> map)
    throws CgenerationException {
    final String cName = map.get(opName);
    if ( cName != null ) {
      return "ILP_" + cName;
    } else {
      final String msg = "No such operator: " + opName;
      throw new CgenerationException(msg);
    }
  }

  /** Compiler une référence à une variable. */

  @Override
public IASTvariable generateVariable () {
    CgenEnvironment.counter++;
    return CgenEnvironment.createVariable("TEMP" + CgenEnvironment.counter);
  }
  private transient static int counter = 0;

  private static IASTvariable createVariable (final String name) {
    return new IASTvariable () {
        @Override
        public String getName () {
          return name;
        }
      };
  }

  /** Enrichir un environnement lexical avec les primitives
   * d'impression (print et newline). */

  @Override
public ICgenLexicalEnvironment extendWithPrintPrimitives (
          final ICgenLexicalEnvironment lexenv) {
    final ICgenLexicalEnvironment lexenv2 =
      lexenv.extend(CgenEnvironment.createVariable("print"),
                    "ILP_print");
    final ICgenLexicalEnvironment lexenv3 =
      lexenv2.extend(CgenEnvironment.createVariable("newline"),
                     "ILP_newline");
    return lexenv3;
  }
  
  @Override
public ICgenLexicalEnvironment extendWithPredefinedConstants (
          final ICgenLexicalEnvironment lexenv) {
        final ICgenLexicalEnvironment lexenv2 =
          lexenv.extend(CgenEnvironment.createVariable("pi"),
                        "ILP_PI");
        return lexenv2;
      }

}

// end of CgenEnvironment.java
