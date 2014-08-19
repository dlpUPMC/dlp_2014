/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTwhile.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.cgen.VoidDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2while;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.IAST4while;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** La boucle tant-que: son interprétation et sa compilation.
 *
 * La principale question est: « Quelle est la valeur d'une telle
 * boucle ? »
 */

public class CEASTwhile
  extends CEASTdelegableInstruction
  implements IAST4while {

  public CEASTwhile (final IAST4expression condition,
                     final IAST4expression body)
  {
      this.delegate =
          new fr.upmc.ilp.ilp2.ast.CEASTwhile(condition, body);
  }
  private IAST2while<CEASTparseException> delegate;

  @Override
  public IAST2while<CEASTparseException> getDelegate () {
      return this.delegate;
  }

  @ILPexpression
  public IAST4expression getCondition () {
    return CEAST.narrowToIAST4expression(getDelegate().getCondition());
  }
  @ILPexpression
  public IAST4expression getBody() {
    return CEAST.narrowToIAST4expression(getDelegate().getBody());
  }

  public static IAST2while<CEASTparseException> parse (
          final Element e, final IParser parser)
  throws CEASTparseException {
    return fr.upmc.ilp.ilp2.ast.CEASTwhile.parse(e, parser);
  }

  @Override
  public IAST4expression normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
      return factory.newWhile(
          getCondition().normalize(lexenv, common, factory),
          getBody().normalize(lexenv, common, factory) );
  }

  @Override
  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common,
                       final IDestination destination)
  throws CgenerationException {
      IAST4localVariable tmp = CEASTlocalVariable.generateVariable();
      buffer.append(" while ( 1 ) {\n");
      tmp.compileDeclaration(buffer, lexenv, common);
      ICgenLexicalEnvironment bodyLexenv = lexenv;
      bodyLexenv = bodyLexenv.extend(tmp);
      getCondition().compile(buffer, bodyLexenv, common,
              new AssignDestination(tmp) );
      IDestination garbage = VoidDestination.create();
      buffer.append(" if ( ILP_isEquivalentToTrue(");
      buffer.append(tmp.getMangledName());
      buffer.append(") ) {\n");
      getBody().compile(buffer, bodyLexenv, common, garbage);
      buffer.append("}\n else { break; }\n}\n");
      CEASTinstruction.voidExpression()
          .compile(buffer, lexenv, common, destination);
      buffer.append(";\n");
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
  @Override
  public void findInvokedFunctions () {
      findAndAdjoinToInvokedFunctions(getCondition());
      findAndAdjoinToInvokedFunctions(getBody());
  }

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
    getCondition().inline();
    getBody().inline();
  }
  */

  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTwhile.java
