/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTalternative.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2alternative;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4alternative;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** L'alternative: son interprétation et sa compilation. */

public class CEASTalternative
extends CEASTdelegableInstruction
implements IAST4alternative {

    public CEASTalternative (final IAST4expression condition,
                             final IAST4expression consequence,
                             final IAST4expression alternant ) {
        this.delegate =
            new fr.upmc.ilp.ilp2.ast.CEASTalternative(
                condition, consequence, alternant );
    }
    public CEASTalternative (final IAST4expression condition,
                             final IAST4expression consequence ) {
        this(condition,
             consequence,
             CEASTexpression.voidExpression() );
    }
    private fr.upmc.ilp.ilp2.ast.CEASTalternative delegate;

    @Override
    public IAST2alternative<CEASTparseException> getDelegate () {
        return this.delegate;
    }

    public static IAST2alternative<CEASTparseException> parse (
            final Element e, final IParser parser)
    throws CEASTparseException {
        return fr.upmc.ilp.ilp2.ast.CEASTalternative.parse(e, parser);
    }

    @ILPexpression
    public IAST4expression getCondition () {
      return CEAST.narrowToIAST4expression(getDelegate().getCondition());
    }
    @ILPexpression
    public IAST4expression getConsequent () {
      return CEAST.narrowToIAST4expression(getDelegate().getConsequent());
    }
    @ILPexpression
    public IAST4expression getAlternant () {
        try {
            return CEAST.narrowToIAST4expression(getDelegate().getAlternant());
        } catch (CEASTparseException e) {
            assert false : "Should not occur!";
            throw new RuntimeException(e);
        }
    }
    public boolean isTernary () {
        return true;
    }

    @Override
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common,
                         final IDestination destination)
      throws CgenerationException {
        final IAST4variable tmp = CEASTlocalVariable.generateVariable();
        buffer.append("{ ");
        tmp.compileDeclaration(buffer, lexenv, common);
        getCondition().compile(buffer, lexenv, common, new AssignDestination(tmp));
        buffer.append(";\n if ( ILP_isEquivalentToTrue( ");
        buffer.append(tmp.getMangledName());
        buffer.append(" ) ) {\n");
        getConsequent().compile(buffer, lexenv, common, destination);
        buffer.append(";\n } else {\n");
        getAlternant().compile(buffer, lexenv, common, destination);
        buffer.append(";\n }\n}");
    }

  @Override
  public IAST4expression normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
    throws NormalizeException {
      return factory.newAlternative(
              getCondition().normalize(lexenv, common, factory),
              getConsequent().normalize(lexenv, common, factory),
              getAlternant().normalize(lexenv, common, factory) );
  }

  /* Obsolete de par CEAST.findInvokedFunctions() qui use d'annotations
  @Override
  public void findInvokedFunctions () {
      findAndAdjoinToInvokedFunctions(getCondition());
      findAndAdjoinToInvokedFunctions(getConsequent());
      findAndAdjoinToInvokedFunctions(getAlternant());
  }

  /* Obsolète de par CEAST.inline() qui use de réflexivité.
  @Override
  public void inline () {
      getCondition().inline();
      getConsequent().inline();
      getAlternant().inline();
  }
  */

  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTalternative.java
