/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTexpression.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.cgen.NoDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;

/** La classe des expressions. Rappelons que les expressions sont des
 * instructions (comme en C). */

public abstract class CEASTexpression
  extends CEASTinstruction
  implements IAST2expression<CEASTparseException> {

  /** Compiler une instruction c'est ajouter un point-virgule a la
   * compilation de l'expression. */
  public void compileInstruction (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common,
                                  final IDestination destination)
    throws CgenerationException {
    this.compileExpression(buffer, lexenv, common, destination);
    buffer.append("; ");
  }

  /** Compiler une expression sans destination. */
  public void compileExpression (final StringBuffer buffer,
                                 final ICgenLexicalEnvironment lexenv,
                                 final ICgenEnvironment common)
    throws CgenerationException {
    this.compileExpression(buffer, lexenv, common, NoDestination.create());
  }

  /** Une constante utile pour les conversions entre liste et tableau. */
  @SuppressWarnings("unchecked")
  public static final
  IAST2expression<CEASTparseException>[] EMPTY_EXPRESSION_ARRAY =
          new IAST2expression[0];

  /** Renvoyer une expression vide qui ne fait rien mais, comme il faut
   * rendre une valeur, on renvoie FALSE.  */
  public static IAST2expression<CEASTparseException> voidExpression () {
    return new CEASTboolean("false");
  }
}

// end of CEASTExpression.java
