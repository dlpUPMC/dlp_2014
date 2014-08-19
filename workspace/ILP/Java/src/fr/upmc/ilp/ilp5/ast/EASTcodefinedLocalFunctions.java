package fr.upmc.ilp.ilp5.ast;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IUserFunction;
import fr.upmc.ilp.ilp2.runtime.UserFunction;
import fr.upmc.ilp.ilp4.ast.FindingInvokedFunctionsException;
import fr.upmc.ilp.ilp4.ast.InliningException;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp5.interfaces.IAST5Factory;
import fr.upmc.ilp.ilp5.interfaces.IAST5codefinedLocalFunctions;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionDefinition;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitor;
import fr.upmc.ilp.ilp5.interfaces.IParser5;

public class EASTcodefinedLocalFunctions
extends CEAST5expression
implements IAST5codefinedLocalFunctions {

    public EASTcodefinedLocalFunctions (IAST5localFunctionDefinition[] fns,
                                        IAST4expression body ) {
        this.functions = fns;
        this.body = body;
    }
    private final IAST5localFunctionDefinition[] functions;
    private final IAST4expression body;

    @ILPexpression
    public IAST4expression getBody() {
        return this.body;
    }
    @ILPexpression(isArray = true)
    public IAST5localFunctionDefinition[] getFunctions() {
        return this.functions;
    }

    public static IAST5codefinedLocalFunctions parse (
            final Element e, final IParser5<CEASTparseException> parser)
    throws CEASTparseException {
        EASTlocalFunctionDefinition[] fns;
        try {
            final XPathExpression localFunctionsPath =
                xPath.compile("./fonctions/*");
            final NodeList nlFunctions = (NodeList)
                localFunctionsPath.evaluate(e, XPathConstants.NODESET);
            fns = new EASTlocalFunctionDefinition[nlFunctions.getLength()];
            for ( int i=0 ; i<nlFunctions.getLength() ; i++ ) {
                IAST4functionDefinition fdef = (IAST4functionDefinition)
                    parser.parse(nlFunctions.item(i));
                fns[i] = new EASTlocalFunctionDefinition(fdef);
        }
        IAST4expression body = (IAST4expression)
            parser.findThenParseChildAsSequence(e, "corps");
        return parser.getFactory().newCodefinedLocalFunctions(fns, body);
        } catch (XPathExpressionException e1) {
            throw new CEASTparseException(e1);
        }
    }
    private static final XPath xPath = XPathFactory.newInstance().newXPath();
    // NOTE: factoriser xPath plus globalement ?
    
    @Override
    public IAST5codefinedLocalFunctions normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            IAST4Factory factory )
      throws NormalizeException {
        return this.normalize(lexenv, common, CEAST5.narrowToIAST5Factory(factory));
    }
    public IAST5codefinedLocalFunctions normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            IAST5Factory factory )
      throws NormalizeException {
        INormalizeLexicalEnvironment newlexenv = lexenv;
        for ( IAST5localFunctionDefinition fundef : getFunctions() ) {
            newlexenv = newlexenv.extend(fundef.getDefinedVariable());
        }
        List<IAST5localFunctionDefinition> newfundefs = new Vector<>();
        for ( IAST5localFunctionDefinition fundef : getFunctions() ) {
            IAST5localFunctionDefinition newfundef =
                CEAST5expression.narrowToIAST5localFunctionDefinition(
                        fundef.normalize(newlexenv, common, factory));
            newfundefs.add(newfundef);
        }
        IAST4expression newBody = 
            getBody().normalize(newlexenv, common, factory);
        return factory.newCodefinedLocalFunctions(
                newfundefs.toArray(EMPTY_IAST5_LOCAL_FUNCTION_DEFINITIONS),
                newBody);
    }
    public static IAST5localFunctionDefinition[]
      EMPTY_IAST5_LOCAL_FUNCTION_DEFINITIONS =
          new IAST5localFunctionDefinition[0];

    // Cette methode est necessaire car la meta-methode generique ne fonctionne
    // pas. En effet un IAST5localFunctionDefinition n'est pas une IAST4expression.
    @Override
    public void computeInvokedFunctions ()
    throws FindingInvokedFunctionsException {
        getBody().computeInvokedFunctions();
        for (IAST5localFunctionDefinition fundef : getFunctions() ) {
            fundef.computeInvokedFunctions();
        }
    }
    
    @Override
    public void findGlobalVariables (final Set<IAST2variable> globalvars,
                                   final ICgenLexicalEnvironment lexenv ) {
        ICgenLexicalEnvironment newlexenv = lexenv;
        for ( IAST5localFunctionDefinition fundef : getFunctions() ) {
            newlexenv = newlexenv.extend(fundef.getDefinedVariable());
        }
        for ( IAST5localFunctionDefinition fundef : getFunctions() ) {
            fundef.findGlobalVariables(globalvars, newlexenv);
        }
        getBody().findGlobalVariables(globalvars, newlexenv);
    }

    // Cette methode est necessaire car la meta-methode generique ne fonctionne
    // pas. En effet un IAST5localFunctionDefinition n'est pas une IAST4expression.
    @Override
    public void inline (IAST4Factory factory) throws InliningException {
        for ( IAST5localFunctionDefinition fd : getFunctions() ) {
            fd.inline(factory);
        }
        getBody().inline(factory);
    }
    
    @Override
    public Object eval (final ILexicalEnvironment lexenv,
                        final ICommon common)
    throws EvaluationException {
        ILexicalEnvironment newlexenv = lexenv;
        for ( IAST5localFunctionDefinition fundef : getFunctions() ) {
            newlexenv = newlexenv.extend(fundef.getDefinedVariable(),
                                         Boolean.FALSE);
        }
        for ( IAST5localFunctionDefinition fundef : getFunctions() ) {
            IUserFunction fn = new UserFunction(
                    fundef.getVariables(),
                    fundef.getBody(),
                    newlexenv);
            newlexenv.update(fundef.getDefinedVariable(), fn);
        }
        return getBody().eval(newlexenv, common);
    }

    @Override
    public <Data, Result, Exc extends Throwable> Result accept (
            IAST5visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
    public <Data, Result, Exc extends Throwable> Result accept (
            IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return narrowToIAST5visitor(visitor).visit(this, data);
    }
    // NOTE: double methode surchargee.
}
