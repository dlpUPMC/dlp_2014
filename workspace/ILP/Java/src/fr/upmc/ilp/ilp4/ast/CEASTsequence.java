/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTsequence.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2sequence;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4sequence;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Les sequences d'instructions. */

public class CEASTsequence
extends CEASTdelegableInstruction
implements IAST4sequence {

  public CEASTsequence (final IAST4expression[] instructions) {
      this.delegate =
          new fr.upmc.ilp.ilp2.ast.CEASTsequence(instructions);
  }
  private final fr.upmc.ilp.ilp2.ast.CEASTsequence delegate;

  @Override
  public fr.upmc.ilp.ilp2.ast.CEASTsequence getDelegate () {
      return this.delegate;
  }

  @ILPexpression(isArray=true)
  public IAST4expression[] getInstructions () {
    return CEAST.narrowToIAST4expressionArray(
                      getDelegate().getInstructions() );
  }
  public int getInstructionsLength () {
      return getDelegate().getInstructions().length;
  }
  public IAST4expression getInstruction (int i) throws CEASTparseException {
      return CEAST.narrowToIAST4expression(
              getDelegate().getInstruction(i) );
  }

  public static IAST2sequence<CEASTparseException> parse (
          final Element e, final IParser parser)
    throws CEASTparseException {
    return fr.upmc.ilp.ilp2.ast.CEASTsequence.parse(e, parser);
  }

  /** Renvoyer une séquence d'instructions réduite à une seule
   * instruction ne faisant rien. */

  public static IAST4expression voidSequence () {
      return CEASTexpression.voidExpression();
  }

  /** Si la séquence ne comporte qu'une unique expression, la
   * remplacer par cette unique expression (normalisée elle-même bien
   * sûr). */

  @Override
  public IAST4expression normalize (final INormalizeLexicalEnvironment lexenv,
                                    final INormalizeGlobalEnvironment common,
                                    final IAST4Factory factory )
    throws NormalizeException {
      IAST4expression[] instructions = getInstructions();
      IAST4expression[] instructions_ = new IAST4expression[instructions.length];
      for ( int i = 0 ; i< instructions.length ; i++ ) {
          instructions_[i] = 
              instructions[i].normalize(lexenv, common, factory);
      }
      if ( instructions.length == 1 ) {
          return instructions_[0];
      } else {
          return factory.newSequence(instructions_);
      }
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
  @Override
  public void findInvokedFunctions () {
      for ( IAST4expression instr : getInstructions() ) {
          findAndAdjoinToInvokedFunctions(instr);
      }
  }

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
      for ( IAST4expression instr : getInstructions() ) {
          instr.inline();
      }
  }
  */

  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTsequence.java
