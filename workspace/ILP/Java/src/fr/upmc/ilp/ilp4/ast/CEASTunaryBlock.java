/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTunaryBlock.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryBlock;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Le bloc unaire: interprétation et compilation.
 *
 * Il n'hérite pas du bloc local car il n'y a pas de code commun.
 */

public class CEASTunaryBlock
  extends CEASTdelegableInstruction
  implements IAST4unaryBlock {

  public CEASTunaryBlock (final IAST4variable variable,
                          final IAST4expression initialization,
                          final IAST4expression body)
  {
      this.delegate =
          new fr.upmc.ilp.ilp2.ast.CEASTunaryBlock(
                  variable, initialization, body );
  }
  private fr.upmc.ilp.ilp2.ast.CEASTunaryBlock delegate;

  @Override
  public fr.upmc.ilp.ilp2.ast.CEASTunaryBlock getDelegate () {
      return this.delegate;
  }

  @ILPvariable
  public IAST4variable getVariable () {
    return CEAST.narrowToIAST4variable(this.delegate.getVariable());
  }
  public IAST4localVariable getLocalVariable () {
        return CEAST.narrowToIAST4localVariable(this.delegate.getVariable());
      }
  @ILPexpression
  public IAST4expression getInitialization () {
    return CEAST.narrowToIAST4expression(this.delegate.getInitialization());
  }
  @ILPexpression
  public IAST4expression getBody() {
    return CEAST.narrowToIAST4expression(this.delegate.getBody());
  }

  public static IAST2unaryBlock<CEASTparseException> parse (
          final Element e, final IParser parser)
  throws CEASTparseException {
    return fr.upmc.ilp.ilp2.ast.CEASTunaryBlock.parse(e, parser);
  }

  @Override
  public IAST4expression normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
      IAST4variable var = factory.newLocalVariable(getVariable().getName());
      final INormalizeLexicalEnvironment bodyLexenv = lexenv.extend(var);
      return factory.newUnaryBlock(
              var,
              getInitialization().normalize(lexenv, common, factory),
              getBody().normalize(bodyLexenv, common, factory) );
  }

  @Override
  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common,
                       final IDestination destination)
    throws CgenerationException {
    final IAST4variable tmp = CEASTlocalVariable.generateVariable();
    final ICgenLexicalEnvironment lexenv2 = lexenv.extend(tmp);
    final ICgenLexicalEnvironment lexenv3 =
        lexenv2.extend(getVariable());

    buffer.append("{\n");
    tmp.compileDeclaration(buffer, lexenv2, common);
    getInitialization().compile(buffer, lexenv, common,
            new AssignDestination(tmp) );
    buffer.append(";\n");

    buffer.append("{\n");
    getVariable().compileDeclaration(buffer, lexenv2, common);
    buffer.append(getLocalVariable().getMangledName());
    buffer.append(" = ");
    buffer.append(tmp.getMangledName());
    buffer.append(";\n");

    getBody().compile(buffer, lexenv3, common, destination);
    buffer.append(";}\n}\n");
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
  @Override
  public void findInvokedFunctions () {
      findAndAdjoinToInvokedFunctions(getInitialization());
      findAndAdjoinToInvokedFunctions(getBody());
  }

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
    getInitialization().inline();
    getBody().inline();
  }
  */

  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTunaryBlock.java
