/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id:CgenEnvironment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.cgen;

import java.util.HashMap;
import java.util.Map;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.tool.CStuff;

/** La representation de l'environnement des operateurs predefinis. Il
 * definit comment les compiler. C'est un peu l'analogue de
 * runtime/Common pour le paquetage cgen.
 *
 *  NOTE: on abandonne l'usage de donnees statiques.
 */

public class CgenEnvironment
  implements ICgenEnvironment {

  protected final Map<String, String> mapOp1 = new HashMap<>();
  protected final Map<String, String> mapOp2 = new HashMap<>();
  protected final Map<String, String> mapPrimitives = new HashMap<>();
  protected final Map<String, String> mapGlobals = new HashMap<>();

  public CgenEnvironment () {
      // Binaires:
    mapOp2.put("+", "Plus");
    mapOp2.put("-", "Minus");
    mapOp2.put("*", "Times");
    mapOp2.put("/", "Divide");
    mapOp2.put("%", "Modulo");
    mapOp2.put("<", "LessThan");
    mapOp2.put("<=", "LessThanOrEqual");
    mapOp2.put("==", "Equal");
    mapOp2.put(">=", "GreaterThanOrEqual");
    mapOp2.put(">", "GreaterThan");
    mapOp2.put("!=", "NotEqual");
    // Unaires:
    mapOp1.put("-", "Opposite");
    mapOp1.put("!", "Not");
    // Primitives
    mapPrimitives.put("print", "print");
    mapPrimitives.put("newline", "newline");
    // Predefined globals
    mapGlobals.put("pi", "ILP_PI");
  }

  /** Comment convertir un operateur unaire en C. */

  public String compileOperator1 (final String opName)
    throws CgenerationException {
    return compileOperator(opName, mapOp1);
  }

  /** Comment convertir un operateur binaire en C. */

  public String compileOperator2 (final String opName)
    throws CgenerationException {
    return compileOperator(opName, mapOp2);
  }

  /** Comment convertir une primitive en C. */

  public String compilePrimitive (final String opName)
    throws CgenerationException {
    return compileOperator(opName, mapPrimitives);
  }

  /** Methode interne pour trouver le nom en C d'un operateur.
   *
   * @throws CgenerationException si le nom est inconnu.
   */

  private String compileOperator (final String opName, Map<String, String> map)
    throws CgenerationException {
    final String cName = map.get(opName);
    if ( cName != null ) {
      return "ILP_" + cName;
    } else {
      final String msg = "No such entity: " + opName;
      throw new CgenerationException(msg);
    }
  }

  /** Compiler une variable globale. */

  public String compileGlobal (final IAST2variable variable)
    throws CgenerationException {
    final String globalName = variable.getName();
    final String cName = mapGlobals.get(globalName);
    if ( cName != null ) {
        return cName;
    } else {
        return variable.getMangledName();
    }
  }

  /** Enregistrer une nouvelle variable globale. */

  public void bindGlobal (final IAST2variable var) {
      mapGlobals.put(var.getName(), var.getMangledName());
  }

  @Deprecated
  public void bindGlobal (final String variableName) {
      mapGlobals.put(variableName, CStuff.mangle(variableName));
  }
  
  /** Enregistrer une nouvelle primitive. */
  
  public void bindPrimitive (final String primitiveName) {
      this.mapPrimitives.put(primitiveName, primitiveName);
  }
  public void bindPrimitive (final String primitiveName, final String cName) {
      this.mapPrimitives.put(primitiveName, cName);
  }

  /** La variable est-elle presente dans l'environnement global ? */

  public boolean isPresent (final String variableName) {
      return mapGlobals.containsKey(variableName);
  }

}

// end of CgenEnvironment.java
