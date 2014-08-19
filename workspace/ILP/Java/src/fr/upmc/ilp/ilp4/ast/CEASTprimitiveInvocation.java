/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTprimitiveInvocation.java 1242 2012-09-12 17:51:41Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2invocation;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4invocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4primitiveInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Les invocations de primitives. */

public class CEASTprimitiveInvocation
  extends CEASTinvocation
  implements IAST4primitiveInvocation {

    public CEASTprimitiveInvocation (final IAST4globalVariable function,
                                     final IAST4expression[] argument) {
        super(new CEASTreference(function), argument);
        this.primitive = function;
        this.delegate = new fr.upmc.ilp.ilp2.ast.CEASTprimitiveInvocation(
                             getPrimitiveName(), argument );
    }
    private final IAST4globalVariable primitive;

    public String getPrimitiveName() {
        return getFunctionGlobalVariable().getName();
    }

    @ILPvariable
    public IAST4globalVariable getFunctionGlobalVariable () {
        return this.primitive;
    }

    @Override
    public IAST4expression getFunction() {
        final String msg = "Internal problem! Must never be invoked!";
        throw new RuntimeException(msg);
    }

    public static IAST2invocation<CEASTparseException> parse (
              final Element e, final IParser parser)
    throws CEASTparseException {
      return fr.upmc.ilp.ilp2.ast.CEASTprimitiveInvocation.parse(e, parser);
    }

  @Override
  public IAST4invocation normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
    final IAST4globalVariable function_ =
        CEAST.narrowToIAST4globalVariable(
                getFunctionGlobalVariable().normalize(lexenv, common, factory) );
    final IAST4expression[] arguments = getArguments();
    IAST4expression[] argument_ = new IAST4expression[arguments.length];
    for ( int i = 0 ; i<arguments.length ; i++ ) {
      argument_[i] = 
        arguments[i].normalize(lexenv, common, factory);
    }
    // On vérifie au passage l'arité:
    checkArity();
    return factory.newPrimitiveInvocation(function_, argument_);
  }

  /** Verifier que l'invocation a la bonne arité vis-à-vis de la définition
   * de la fonction globale.  */
  @Override
  public void checkArity ()
  throws NormalizeException {
      // TODO
  }

  @Override
  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common,
                       final IDestination destination )
  throws CgenerationException {
      IAST4expression[] args = getArguments();
      IAST4localVariable[] tmps = new IAST4localVariable[args.length];
      ICgenLexicalEnvironment bodyLexenv = lexenv;
      buffer.append("{\n");
      for ( int i=0; i<args.length ; i++ ) {
          tmps[i] = CEASTlocalVariable.generateVariable();
          tmps[i].compileDeclaration(buffer, lexenv, common);
          bodyLexenv = bodyLexenv.extend(tmps[i]);
      }
      for ( int i=0 ; i<args.length ; i++ ) {
          args[i].compile(buffer, bodyLexenv, common,
                  new AssignDestination(tmps[i]) );
          buffer.append(";\n");
      }
      destination.compile(buffer, bodyLexenv, common);
      buffer.append(common.compilePrimitive(getPrimitiveName()));
      buffer.append("(");
      for ( int i=0 ; i<(args.length-1) ; i++ ) {
          buffer.append(tmps[i].getMangledName());
          buffer.append(", ");
      }
      if ( args.length > 0 ) {
          buffer.append(tmps[args.length-1].getMangledName());
      }
      buffer.append(");\n}\n");
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
      for ( IAST4expression arg : getArguments() ) {
          arg.inline();
      }
  }
  */

  @Override
  public <Data, Result, Exc extends Throwable> Result 
  accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
    return visitor.visit(this, data);
  }
}

// end of CEASTprimitiveInvocation.java
