/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: EAST.java 1213 2012-08-27 15:09:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;
import fr.upmc.ilp.ilp1.interfaces.*;
import fr.upmc.ilp.ilp1.runtime.*;

public abstract class EAST
implements IAST, IASTEvaluable {

  /** La méthode qui évalue un EAST et retourne sa valeur. Attention:
   * les valeurs sont des objets JAVA (POJO comme l'on dit). */

  public abstract Object eval (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException;

  /** Un programme qui calcule n'importe quoi. C'est, par exemple,
   * utilisé pour les alternatives binaires.
   *
   * NOTA: une fois que ce n'importe-quoi est déterminé, ici, il ne
   * change plus!
   */

  public static EAST voidConstant () {
    return THE_VOID_CONSTANT;
  }
  private static final EAST THE_VOID_CONSTANT;
  static {
    THE_VOID_CONSTANT = new EASTboolean("false");
  }

  /** Rendre une valeur quelconque. Ici c'est la valeur de
   * voidConstant(lexenv, common) qui a été choisie.
   */
  public static Object voidConstantValue () {
      return Boolean.FALSE;
  }

}

// fin de EAST.java
