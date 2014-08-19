/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ICgenEnvironment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;

/** L'interface decrivant l'environnement des operateurs pr�d�finis du
 * langage a compiler vers C. Il est l'analogue de runtime/ICommon
 * pour le paquetage cgen. */

public interface ICgenEnvironment {

  /** Comment convertir un operateur unaire en C. */

  String compileOperator1 (String opName)
    throws CgenerationException ;

  /** Comment convertir un operateur binaire en C. */

  String compileOperator2 (String opName)
    throws CgenerationException ;

  /** Comment convertir le nom d'une primitive en C. */

  String compilePrimitive (String primitiveName)
    throws CgenerationException;

  /** Comment compiler une variable globale en C. */

  String compileGlobal (IAST2variable variable)
    throws CgenerationException;

  /** Enregistrer une nouvelle variable globale. */

  void bindGlobal (IAST2variable var);
  
  /** Enregister une nouvelle primitive. */
  
  void bindPrimitive (String primitiveName);
  void bindPrimitive (String primitiveName, String cName);

  //@Deprecated
  //void bindGlobal (String varName);

  /** Verifier si une variable globale est presente ? */

  boolean isPresent (String variableName);
}

// end of ICgenEnvironment.java
