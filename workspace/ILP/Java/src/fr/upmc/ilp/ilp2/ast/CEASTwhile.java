/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTwhile.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.cgen.VoidDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.IAST2while;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** La boucle tant-que: son interprétation et sa compilation.
 *
 * La principale question est: Quelle est la valeur d'une telle
 * boucle ?
 */

public class CEASTwhile
  extends CEASTinstruction
  implements IAST2while<CEASTparseException> {

  public CEASTwhile (
          final IAST2expression<CEASTparseException> condition,
          final IAST2instruction<CEASTparseException> body) {
    this.condition = condition;
    this.body = body;
  }
  private final IAST2expression<CEASTparseException> condition;
  private final IAST2instruction<CEASTparseException> body;

  public IAST2expression<CEASTparseException> getCondition () {
      return condition;
  }
  public IAST2instruction<CEASTparseException> getBody () {
      return body;
  }

  public static IAST2while<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
    throws CEASTparseException {
    final NodeList nl = e.getChildNodes();
    IAST2expression<CEASTparseException> condition =
      (IAST2expression<CEASTparseException>)
        parser.findThenParseChildAsUnique(nl, "condition");
    IAST2instruction<CEASTparseException> body = 
      (IAST2instruction<CEASTparseException>)
        parser.findThenParseChildAsSequence(nl, "corps");
    return parser.getFactory().newWhile(condition, body);
  }

  //NOTE: Acces direct aux champs interdit a partir d'ici!

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
    while ( true ) {
      Object bool = getCondition().eval(lexenv, common);
      if ( Boolean.FALSE == bool ) {
        break;
      }
      getBody().eval(lexenv, common);
    }
    // Bogue ici précédemment: vue par Rafael.Cerioli@etu.upmc.fr
    return Boolean.FALSE;
  }

  public void compileInstruction (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common,
                                  final IDestination destination)
    throws CgenerationException {
    buffer.append(" while ( ILP_isEquivalentToTrue( ");
    getCondition().compileExpression(buffer, lexenv, common);
    buffer.append(") ) { ");
    getBody().compileInstruction(
            buffer, lexenv, common, VoidDestination.create() );
    buffer.append("}\n");
    CEASTinstruction.voidInstruction()
      .compileInstruction(buffer, lexenv, common, destination);
  }

  @Override
  public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                 final ICgenLexicalEnvironment lexenv ) {
      getCondition().findGlobalVariables(globalvars, lexenv);
      getBody().findGlobalVariables(globalvars, lexenv);
  }
}

// end of CEASTwhile.java
