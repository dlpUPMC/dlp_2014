/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTprimitiveInvocation.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.Invokable;
import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2invocation;
import fr.upmc.ilp.ilp2.interfaces.IAST2primitiveInvocation;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** Les invocations de primitives. Elles sont implantées comme des
 * invocations normales à des primitives obtenues à l'aide d'un espace
 * de nom un peu particulier afin d'être toujours disponibles.
 */

public class CEASTprimitiveInvocation
extends CEASTinvocation
implements IAST2primitiveInvocation<CEASTparseException> {

  public CEASTprimitiveInvocation (
          final String primitiveName,
          final IAST2expression<CEASTparseException>[] arguments ) {
      super(new CEASTreference(new CEASTvariable(primitiveName)),
            arguments);
      this.primitiveName = primitiveName;
  }
  private final String primitiveName;

  public String getPrimitiveName() {
      return this.primitiveName;
  }

  public static IAST2invocation<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
    throws CEASTparseException {
      String primitiveName = e.getAttribute("fonction");
      List<IAST2<CEASTparseException>> li = parser.parseList(e.getChildNodes());
      IAST2expression<CEASTparseException>[] arguments =
          li.toArray(CEASTexpression.EMPTY_EXPRESSION_ARRAY);
      return parser.getFactory()
          .newPrimitiveInvocation(primitiveName, arguments);
  }

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  @Override
  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
    final Object fn = common.primitiveLookup(getPrimitiveName());
    if ( fn instanceof Invokable ) {
      Invokable invokable = (Invokable) fn;
      final IAST2expression<CEASTparseException>[] arguments = getArguments();
      final Object[] args = new Object[arguments.length];
      for ( int i = 0 ; i<arguments.length ; i++ ) {
        args[i] = arguments[i].eval(lexenv, common);
      }
      return invokable.invoke(args);
    } else {
      final String msg = "Not a function: " + fn;
      throw new EvaluationException(msg);
    }
  }

  @Override
  public void compileExpression (final StringBuffer buffer,
                                 final ICgenLexicalEnvironment lexenv,
                                 final ICgenEnvironment common,
                                 final IDestination destination)
  throws CgenerationException {
      destination.compile(buffer, lexenv, common);
      //getFunction().compileExpression(buffer, lexenv, common);
      buffer.append(common.compilePrimitive(primitiveName));
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

  @Override
  public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                 final ICgenLexicalEnvironment lexenv ) {
      for ( IAST2expression<CEASTparseException> arg : getArguments() ) {
          arg.findGlobalVariables(globalvars, lexenv);
      }
  }
}

// end of CEASTprimitiveInvocation.java
