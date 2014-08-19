/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ICgenEnvironment.java 825 2009-10-07 12:19:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.cgen;

import fr.upmc.ilp.ilp1.interfaces.*;

/** L'interface décrivant l'environnement des opérateurs prédéfinis du
 * langage à compiler vers C. Il est l'analogue de runtime/ICommon
 * pour le paquetage cgen. */

public interface ICgenEnvironment {

  /** Comment convertir un opérateur unaire en C. */

  String compileOperator1 (String opName)
    throws CgenerationException ;

  /** Comment convertir un opérateur binaire en C. */

  String compileOperator2 (String opName)
    throws CgenerationException ;

  /** un générateur de variables temporaires. */

  IASTvariable generateVariable ();

  /** L'enrichisseur d'environnement lexical avec les primitives. */

  ICgenLexicalEnvironment
    extendWithPrintPrimitives (ICgenLexicalEnvironment lexenv);

  /** L'enrichisseur d'environnement lexical avec les constantes. */

  ICgenLexicalEnvironment
    extendWithPredefinedConstants (ICgenLexicalEnvironment lexenv);

}

// end of ICgenEnvironment.java
