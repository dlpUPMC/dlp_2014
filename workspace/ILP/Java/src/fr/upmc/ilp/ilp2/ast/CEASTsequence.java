/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTsequence.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.cgen.VoidDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2sequence;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** Les sequences d'instructions. */

public class CEASTsequence
  extends CEASTinstruction
  implements IAST2sequence<CEASTparseException> {

  public CEASTsequence (final IAST2instruction<CEASTparseException>[] instruction) {
    this.instruction = instruction;
  }
  public CEASTsequence (final List<IAST2instruction<CEASTparseException>> instructions) {
    this(instructions.toArray(CEASTinstruction.EMPTY_INSTRUCTION_ARRAY));
  }
  private final IAST2instruction<CEASTparseException>[] instruction;

  public IAST2instruction<CEASTparseException>[] getInstructions () {
      return this.instruction;
  }
  public int getInstructionsLength () {
      return this.instruction.length;
  }
  public IAST2instruction<CEASTparseException> getInstruction (final int i) 
    throws CEASTparseException {
      try {
        return this.instruction[i];
      } catch (Exception exc) {
          throw new CEASTparseException(exc);
      }
  }

  public static IAST2sequence<CEASTparseException> parse(
          final Element e, final IParser<CEASTparseException> parser)
    throws CEASTparseException {
      return parser.parseChildrenAsSequence(e.getChildNodes());
  }

  //NOTE: Acces direct aux champs interdit a partir d'ici!

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
      IAST2instruction<CEASTparseException>[] instructions = getInstructions();
      Object last = Boolean.FALSE;
      for ( int i = 0 ; i < instructions.length ; i++ ) {
          last = instructions[i].eval(lexenv, common);
      }
      return last;
  }

  public void compileInstruction (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common,
                                  final IDestination destination)
    throws CgenerationException {
    buffer.append("{\n");
    IAST2instruction<CEASTparseException>[] instructions = getInstructions();
    for ( int i = 0 ; i<instructions.length-1 ; i++ ) {
      instructions[i].compileInstruction(
              buffer, lexenv, common, VoidDestination.create() );
    }
    final IAST2instruction<CEASTparseException> last = instructions[instructions.length-1];
    last.compileInstruction(buffer, lexenv, common, destination);
    buffer.append("\n}\n");
  }

  @Override
  public void findGlobalVariables (
          final Set<IAST2variable> globalvars,
          final ICgenLexicalEnvironment lexenv ) {
      for ( IAST2instruction<CEASTparseException> instr : getInstructions() ) {
          instr.findGlobalVariables(globalvars, lexenv);
      }
  }
}

// end of CEASTsequence.java
