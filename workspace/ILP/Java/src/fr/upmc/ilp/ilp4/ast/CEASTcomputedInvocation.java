/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTcomputedInvocation.java 942 2010-08-24 17:09:58Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4computedInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

public class CEASTcomputedInvocation
extends CEASTinvocation implements IAST4computedInvocation {

    protected CEASTcomputedInvocation (final IAST4expression function,
                                       final IAST4expression[] argument) {
        super(function, argument);
    }

    @Override
    public IAST4expression normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
      throws NormalizeException {
      final IAST4expression function_ =
          getFunction().normalize(lexenv, common, factory);
      final IAST4expression[] arguments = getArguments();
      IAST4expression[] argument_ = new IAST4expression[arguments.length];
      for ( int i = 0 ; i<arguments.length ; i++ ) {
          argument_[i] = arguments[i].normalize(lexenv, common, factory);
      }
      return factory.newComputedInvocation(function_, argument_);
    }

    /** On ne peut rien dire avant l'execution. */
    @Override
    public void checkArity () {
        return;
    }

    @Override
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common,
                         final IDestination destination )
    throws CgenerationException {
        IAST4localVariable tmpfun = CEASTlocalVariable.generateVariable();
        IAST4expression[] args = getArguments();
        IAST4localVariable[] tmps = new IAST4localVariable[args.length];
        ICgenLexicalEnvironment bodyLexenv = lexenv;
        bodyLexenv = bodyLexenv.extend(tmpfun);
        buffer.append("{\n ILP_Primitive ");
        buffer.append(tmpfun.getMangledName());
        buffer.append(";\n");
        for ( int i=0; i<args.length ; i++ ) {
            tmps[i] = CEASTlocalVariable.generateVariable();
            tmps[i].compileDeclaration(buffer, lexenv, common);
            bodyLexenv = bodyLexenv.extend(tmps[i]);
        }
        getFunction().compile(buffer, bodyLexenv, common,
                new AssignDestination(tmpfun) );
        buffer.append(";\n");
        for ( int i=0 ; i<args.length ; i++ ) {
            args[i].compile(buffer, bodyLexenv, common,
                    new AssignDestination(tmps[i]) );
            buffer.append(";\n");
        }
        destination.compile(buffer, bodyLexenv, common);
        buffer.append(bodyLexenv.compile(tmpfun));
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
}
