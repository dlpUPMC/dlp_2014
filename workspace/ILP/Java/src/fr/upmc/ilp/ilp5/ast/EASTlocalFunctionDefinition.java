package fr.upmc.ilp.ilp5.ast;

import java.util.Set;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp5.interfaces.IAST5Factory;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionDefinition;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionVariable;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitor;

/** Definition d'une fonction locale. Ce n'est pas une expression. */

public class EASTlocalFunctionDefinition
extends CEAST5
implements IAST5localFunctionDefinition {

    public EASTlocalFunctionDefinition (IAST5localFunctionVariable fnv,
                                        IAST4variable[] variables,
                                        IAST4expression body ) {
        this.functionVariable = fnv;
        this.variables = variables;
        this.body = body;
    }
    private final IAST5localFunctionVariable functionVariable;
    private final IAST4variable[] variables;
    private final IAST4expression body;

    public EASTlocalFunctionDefinition (IAST4functionDefinition fdef) {
        this(new EASTlocalFunctionVariable(fdef.getDefinedVariable().getName()),
             fdef.getVariables(),
             fdef.getBody());
    }

    public IAST5localFunctionVariable getDefinedVariable() {
        return this.functionVariable;
    }
    public String getFunctionName (){
        return getDefinedVariable().getName();
    }

    @ILPexpression
    public IAST4expression getBody() {
        return this.body;
    }
    @ILPvariable(isArray=true)
    public IAST4variable[] getVariables() {
        return this.variables;
    }

    // Quasiment le meme code que CEASTfunctionDefinition!
    @Override
    public IAST5localFunctionDefinition normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
      throws NormalizeException {
        return this.normalize(lexenv, common, CEAST5.narrowToIAST5Factory(factory));
    }
    public IAST5localFunctionDefinition normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST5Factory factory )
      throws NormalizeException {
        INormalizeLexicalEnvironment bodyLexenv = lexenv;
        final IAST4variable[] variables = getVariables();
        final IAST4variable[] variables_ = new IAST4variable[variables.length];
        for ( int i = 0 ; i<variables.length ; i++ ) {
            variables_[i] = factory.newLocalVariable(variables[i].getName());
            bodyLexenv = bodyLexenv.extend(variables_[i]);
        }
        final IAST4expression body_ = 
            getBody().normalize(bodyLexenv, common, factory);
        return factory.newLocalFunctionDefinition(
                getDefinedVariable(), variables_, body_ );
    }

    // Fortement inspiré par CEASTfunctionDefinition d'ILP2.
    @Override
    public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                   final ICgenLexicalEnvironment lexenv ) {

        final ICgenLexicalEnvironment newlexenv = this.extend(lexenv);
        getBody().findGlobalVariables(globalvars, newlexenv);
    }

    public ICgenLexicalEnvironment extend (final ICgenLexicalEnvironment lexenv)
    {
      ICgenLexicalEnvironment newlexenv = lexenv;
      final IAST2variable[] vars = getVariables();
      for ( int i = 0 ; i<vars.length ; i++ ) {
        newlexenv = newlexenv.extend(vars[i]);
      }
      return newlexenv;
    }

    @Override
    public <Data, Result, Exc extends Throwable> Result accept (
            IAST5visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
    public <Data, Result, Exc extends Throwable> Result accept (
            IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return CEAST5expression.narrowToIAST5visitor(visitor).visit(this, data);
    }
    // NOTE: double methode surchargee.
}
