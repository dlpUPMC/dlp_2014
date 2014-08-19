package fr.upmc.ilp.ilp6.ast;

import java.util.Set;

import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.cgen.ReturnDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.CEAST;
import fr.upmc.ilp.ilp4.ast.CEASTfunctionDefinition;
import fr.upmc.ilp.ilp4.ast.CEASTglobalFunctionVariable;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp6.cgen.CgenMethodLexicalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6methodDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IClassEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IParser6;
import fr.upmc.ilp.ilp6.runtime.ICommon;
import fr.upmc.ilp.ilp6.runtime.ILPmethod;
import fr.upmc.ilp.tool.CStuff;

public class CEASTmethodDefinition
extends CEAST6
implements IAST6methodDefinition {

    // Ce constructeur est invoqué par parse():
    public CEASTmethodDefinition (IAST4functionDefinition delegate) {
        this.methodName = delegate.getFunctionName();
        IAST4variable[] vars = delegate.getVariables();
        // self n'est pas encore en tete des variables:
        this.selfVariable = null;
        // toutes les fonctions sous-jacentes aux methodes doivent avoir 
        // des noms differents:
        CEASTglobalFunctionVariable gfv =
            new CEASTglobalFunctionVariable("ilpMETHOD_" + getCounter());
        this.delegate = new CEASTfunctionDefinition(
                gfv,
                vars,
                delegate.getBody() );
    }
    private final CEASTfunctionDefinition delegate;
    private final IAST4variable selfVariable;
    private final String methodName;

    protected synchronized int getCounter () {
        return counter++;
    }
    private static int counter = 1;

    // Ce constructeur est invoqué par CEASTclassDefinition.adjoinSelfToMethod
    // et par normalize().
    public CEASTmethodDefinition (
            String methodName,
            IAST4globalFunctionVariable gfv,
            IAST4variable[] variables,
            IAST4expression body ) {
        this.methodName = methodName;
        // On n'ajoute pas self en tete, c'est deja fait!
        this.selfVariable = variables[0];
        this.delegate = new CEASTfunctionDefinition(gfv, variables, body);
    }

    public String getFunctionName () {
        return this.methodName;
    }
    public String getMethodName () {
        return this.methodName;
    }
    public IAST4globalFunctionVariable getDefinedVariable () {
        return this.delegate.getDefinedVariable();
    }
    public IAST4variable[] getVariables() {
        return this.delegate.getVariables();
    }
    @ILPexpression
    public IAST4expression getBody () {
        return this.delegate.getBody();
    }
    // On n'elimine aucune methode, on les considere toutes comme recursives.
    public boolean isRecursive () {
        return true;
    }

    /** Rendre l'arite de la fonction sous-jacente (self est compte comme
     * une variable). */
    public int getRealArity () {
        return this.delegate.getVariables().length;
    }

    public static IAST6methodDefinition parse (
            final Element e, final IParser6 parser)
    throws CEASTparseException {
        IAST4functionDefinition fun = CEASTfunctionDefinition.parse(e, parser);
        return parser.getFactory().newMethodDefinition(fun);
    }

    // Etablir un lien depuis la methode vers la classe la definissant.
    // Attention, ce lien n'est, pour l'heure, etabli qu'a la compilation.

    public void setDefiningClass (final IAST6classDefinition clazz) {
        this.definingClassDefinition = clazz;
    }
    private IAST6classDefinition definingClassDefinition;

    public int getMethodOffset (final IClassEnvironment common)
    throws CgenerationException {
        return this.definingClassDefinition.getMethodOffset(
                this.methodName, common );
    }

    public @OrNull IAST6methodDefinition findSuperMethod (
            final IClassEnvironment common )
    throws CgenerationException {
        IAST6classDefinition clazz = this.definingClassDefinition;
        String superClassName = clazz.getSuperClassName();
        while ( true ) {
            if ( "Object".equals(superClassName) ) {
                // Il n'y a que print et classOf comme methodes sur Object:
                if ( "print".equals(getMethodName()) ) {
                    return CEASTclassDefinition.printMethod;
                } else if ( "classOf".equals(getMethodName()) ) {
                    return CEASTclassDefinition.classOfMethod;
                } else {
                    // Pas de super-methode:
                    return null;
                }
            }
            clazz = common.findClassDefinition(superClassName);
            final IAST6methodDefinition[] methods =
                clazz.getProperMethodDefinitions();
            for ( IAST6methodDefinition m : methods ) {
                if ( getMethodName().equals(m.getMethodName()) ) {
                    return m;
                }
            }
            superClassName = clazz.getSuperClassName();
        }
    }

    @Override
    public IAST4functionDefinition normalize (
            final INormalizeLexicalEnvironment lexenv,
            final fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment common,
            final IAST4Factory factory)
      throws NormalizeException {
        return normalize6(
                lexenv,
                CEAST6.narrowToINormalizeGlobalEnvironment(common),
                CEAST6.narrowToIAST6Factory(factory) );
    }
    
    public IAST4functionDefinition normalize6 (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST6Factory factory )
      throws NormalizeException {
        final IAST4globalFunctionVariable gfv =
            CEAST.narrowToIAST4globalFunctionVariable(
                    getDefinedVariable().normalize(lexenv, common, factory));
        INormalizeLexicalEnvironment bodyLexenv = lexenv;
        final IAST4variable[] variables = getVariables();
        final IAST4variable[] variables_ = new IAST4variable[variables.length];
        variables_[0] = this.selfVariable;
        bodyLexenv = bodyLexenv.extend(this.selfVariable);
        for ( int i = 1 ; i<variables.length ; i++ ) {
            variables_[i] = factory.newLocalVariable(variables[i].getName());
            bodyLexenv = bodyLexenv.extend(variables_[i]);
        }
        final IAST4expression body_ = 
            getBody().normalize(bodyLexenv, common, factory);
        return factory.newMethodDefinition(
                getMethodName(),
                gfv,
                variables_,
                body_ );
    }

    @Override
    public void compile6 (
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv,
            final ICgenEnvironment common )
    throws CgenerationException {
        ICgenLexicalEnvironment methodLexenv =
            new CgenMethodLexicalEnvironment(this, lexenv);
        // Émettre en commentaire les fonctions appelées:
        if ( getInvokedFunctions().size() > 0 ) {
            buffer.append("/* Fonctions globales invoquées: ");
            for ( IAST4globalFunctionVariable gv : getInvokedFunctions() ) {
                buffer.append(gv.getMangledName());
                buffer.append(" ");
            }
            buffer.append(" */\n");
        }
        // Émettre la définition de la fonction:
        buffer.append("\nILP_Object\n");
        buffer.append(getDefinedVariable().getMangledName());
        this.delegate.compileVariableList(buffer);
        buffer.append("\n{\n");
        // Lien vers l'objet ILP_Method:
        buffer.append("static ILP_Method ilp_CurrentMethod = &ILP_object_");
        buffer.append(getFunctionName());
        buffer.append("_method;\n");
        // Calcul de la super methode:
        IAST6methodDefinition superMethod = findSuperMethod(common);
        buffer.append("static ILP_general_function ilp_SuperMethod = ");
        if ( superMethod == null ){
            buffer.append("NULL");
        } else {
            buffer.append(superMethod.getDefinedVariable().getMangledName());
        }
        buffer.append(";\n");
        // Sauvegarder des arguments pour super():
        buffer.append("ILP_Object ilp_CurrentArguments[");
        buffer.append(getVariables().length);
        // Pas de tableau de taille nulle en ISO C
        buffer.append("];\n");
        for ( int i=1 ; i<getVariables().length ; i++ ) {
            buffer.append(" ilp_CurrentArguments[");
            buffer.append(i);
            buffer.append("] = ");
            buffer.append(getVariables()[i].getMangledName());
            buffer.append(";\n");
        }
        final ICgenLexicalEnvironment bodyLexenv =
            CEAST6.narrowToICgenLexicalEnvironment(
                    this.delegate.extendWithFunctionVariables(methodLexenv) );
        getBody().compile(buffer, bodyLexenv, common, ReturnDestination.create());
        buffer.append(";\n}");
    }

    @Override
    public void compileHeader6 (
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv,
            final ICgenEnvironment common )
    throws CgenerationException {
        // Pas genant d'engendrer une directive de trop:
        buffer.append("extern struct ILP_Method ILP_object_");
        buffer.append(getFunctionName());
        buffer.append("_method;\n");
    }

    @Override
    public Object eval6 (ILexicalEnvironment lexenv, ICommon common)
            throws EvaluationException {
        final Object function =
            new ILPmethod(getFunctionName(),
                          getVariables(),
                          getBody() );
        common.updateGlobal(getFunctionName(), function);
        return function;
    }

    @Override
    public void findGlobalVariables (
            final Set<IAST2variable> globalvars,
            final fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment lexenv) {
        this.delegate.findGlobalVariables(globalvars, lexenv);
    }

    public <Data, Result, Exc extends Throwable> Result 
    accept (IAST6visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
    public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return CEAST6.narrowToIAST6visitor(visitor).visit(this, data);
    }
    // NOTE: double methode surchargee.

    @Override
    public IAST4localVariable[] getLocalVariables() {
        throw new RuntimeException("not yet implemented!");
    }

    @Override
    public String getMangledFunctionName() {
        return CStuff.mangle(this.methodName);
    }
}
