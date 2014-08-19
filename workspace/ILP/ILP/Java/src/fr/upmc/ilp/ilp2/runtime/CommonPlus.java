/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id:CommonPlus.php 461 2006-09-25 08:19:12Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.runtime;

import java.util.HashMap;
import java.util.Map;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICommon;

/** Environnement global d'interpretation d'ILP2. */

public class CommonPlus
extends fr.upmc.ilp.ilp1.runtime.CommonPlus
implements ICommon {

     public CommonPlus () {
         this(new HashMap<String,Object>(),
              new HashMap<String,Object>());
     }
     public CommonPlus (Map<String, Object> primitiveMap,
                        Map<String, Object> globalMap ) {
         this.primitiveMap = primitiveMap;
         this.globalMap = globalMap;
     }

     /** Ces tables associent des chaines et des objets. */
     protected final Map<String, Object> primitiveMap;
     protected final Map<String, Object> globalMap;

     /** Renvoyer la valeur d'une primitive. */

     public Object primitiveLookup (final String primitiveName)
          throws EvaluationException {
          final Object value = primitiveMap.get(primitiveName);
          if ( value != null ) {
               return value;
          } else {
               final String msg = "No such entity: " + primitiveName;
               throw new EvaluationException(msg);
          }
     }

     /** Etablir la valeur d'une primitive. */

     public void bindPrimitive (final String primitiveName,
                                final Object primitive)
          throws EvaluationException {
          final Object value = primitiveMap.get(primitiveName);
          if ( value == null ) {
               primitiveMap.put(primitiveName, primitive);
          } else {
               final String msg = "Already defined primitive: " + primitiveName;
               throw new EvaluationException(msg);
          }
     }

     /** Renvoyer la valeur d'une variable globale. */

     public Object globalLookup (final IAST2variable variable)
          throws EvaluationException {
          final String variableName = variable.getName();
          final Object value = globalMap.get(variableName);
          if ( value != null ) {
               return value;
          } else if ( globalMap.containsKey(variableName) ) {
               final String msg = "uninitialized global: " + variable.getName();
               throw new EvaluationException(msg);
          } else {
               final String msg = "No such global: " + variable.getName();
               throw new EvaluationException(msg);
          }
     }

     /** Determiner si une variable globale est presente. */

     public boolean isPresent (final String variableName) {
          return globalMap.containsKey(variableName);
     }

     /** Etablir ou modifier la valeur d'une variable globale. Et si
      * c'était une fonction globale ? */

     public void updateGlobal (final String globalVariableName,
                               final Object value) {
          globalMap.put(globalVariableName, value);
     }
}

// end of CommonPlus.java


