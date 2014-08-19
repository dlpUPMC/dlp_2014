/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTinvocation.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.Invokable;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2invocation;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;
import fr.upmc.ilp.ilp2.interfaces.IUserFunction;

public class CEASTinvocation
  extends CEASTexpression
  implements IAST2invocation<CEASTparseException> {

  // NOTE: les deux methodes qui suivent ne peuvent etre simultanement
  // presentes car la genericite (type erasure) de Java ne le permet pas.
  // Je retire la seconde qui pose plus de problemes de typage.
  public CEASTinvocation (final IAST2expression<CEASTparseException> function,
                          final List<IAST2expression<CEASTparseException>> arguments) {
    this(function, arguments.toArray(CEASTexpression.EMPTY_EXPRESSION_ARRAY));
  }
  //public CEASTinvocation (final CEASTexpression function,
  //                        final List<CEASTexpression> arguments) {
  //    this(function, arguments.toArray(CEASTexpression.EMPTY_EXPRESSION_ARRAY);
  //}
  public CEASTinvocation (final IAST2expression<CEASTparseException> function,
                          final IAST2expression<CEASTparseException>[] arguments) {
    this.function = function;
    this.argument = arguments;
  }
  private final IAST2expression<CEASTparseException>   function;
  private final IAST2expression<CEASTparseException>[] argument;

  public IAST2expression<CEASTparseException> getFunction () {
    return this.function;
  }
  public IAST2expression<CEASTparseException>[] getArguments () {
      return this.argument;
  }
  public IAST2expression<CEASTparseException> getArgument (int i) {
      return this.argument[i];
  }
  public int getArgumentsLength () {
      return this.argument.length;
  }

  public static IAST2invocation<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      final NodeList nl = e.getChildNodes();
      IAST2expression<CEASTparseException> function =
          (IAST2expression<CEASTparseException>)
          parser.findThenParseChildAsUnique(nl, "fonction");
      IAST2expression<CEASTparseException>[] arguments =
          parser.findThenParseChildAsList(nl, "arguments")
          .toArray(CEASTexpression.EMPTY_EXPRESSION_ARRAY);
      return parser.getFactory().newInvocation(function, arguments);
  }

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
    final Object fn = getFunction().eval(lexenv, common);
    if ( fn instanceof IUserFunction ) {
      final IUserFunction function = (IUserFunction) fn;
      final IAST2expression<CEASTparseException>[] arguments = getArguments();
      final Object[] args = new Object[arguments.length];
      for ( int i = 0 ; i<arguments.length; i++ ) {
        args[i] = arguments[i].eval(lexenv, common);
      }
      return function.invoke(args, common);
    } else if ( fn instanceof Invokable ) {
        final Invokable function = (Invokable) fn;
        final IAST2expression<CEASTparseException>[] arguments = getArguments();
        final Object[] args = new Object[arguments.length];
        for ( int i = 0 ; i<arguments.length; i++ ) {
          args[i] = arguments[i].eval(lexenv, common);
        }
        return function.invoke(args);
    } else {
      final String msg = "Not a function: " + fn;
      throw new EvaluationException(msg);
    }
  }

  public void compileExpression (final StringBuffer buffer,
                                 final ICgenLexicalEnvironment lexenv,
                                 final ICgenEnvironment common,
                                 final IDestination destination)
    throws CgenerationException {
    destination.compile(buffer, lexenv, common);
    getFunction().compileExpression(buffer, lexenv, common);
    buffer.append("(");
    final IAST2expression<CEASTparseException>[] arguments = getArguments();
    for ( int i = 0 ; i<arguments.length-1 ; i++ ) {
      arguments[i].compileExpression(buffer, lexenv, common);
      buffer.append(", ");
    }
    if ( arguments.length> 0 ) {
      arguments[arguments.length-1].compileExpression(buffer, lexenv, common);
    }
    buffer.append(")");
  }
  // NOTE: les expressions sont compilees en expressions C,
  // les instructions en instructions C.

  @Override
  public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                 final ICgenLexicalEnvironment lexenv ) {
      getFunction().findGlobalVariables(globalvars, lexenv);
      for ( IAST2expression<CEASTparseException> arg : getArguments() ) {
          arg.findGlobalVariables(globalvars, lexenv);
      }
  }
}

// end of CEASTinvocation.java
