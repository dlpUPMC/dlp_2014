/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTlocalBlock.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

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

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.cgen.AssignDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2localBlock;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

public class CEASTlocalBlock
extends CEASTinstruction
implements IAST2localBlock<CEASTparseException> {

    private final IAST2variable[] variable;
    private final IAST2expression<CEASTparseException>[] initialization;
    private final IAST2instruction<CEASTparseException> body;

    public CEASTlocalBlock (
            final IAST2variable[] variable,
            final IAST2expression<CEASTparseException>[] initialization,
            final IAST2instruction<CEASTparseException> body ) {
        this.variable = variable;
        this.initialization = initialization;
        this.body = body;
    }

    public IAST2variable[] getVariables () {
        return this.variable;
    }
    public IAST2expression<CEASTparseException>[] getInitializations () {
        return this.initialization;
    }
    public IAST2instruction<CEASTparseException> getBody () {
        return this.body;
    }

    public static IAST2localBlock<CEASTparseException> parse (
            final Element e, final IParser<CEASTparseException> parser)
    throws CEASTparseException {
        IAST2variable[] variables = new IAST2variable[0];
        IAST2expression<CEASTparseException>[] initializations;
        try {
            final XPathExpression bindingVarsPath =
                xPath.compile("./liaisons/liaison/variable");
            final NodeList nlVars = (NodeList)
                bindingVarsPath.evaluate(e, XPathConstants.NODESET);
            final List<IAST2variable> vars = new Vector<>();
            final List<IAST2expression<CEASTparseException>> inits =
                new Vector<>();
            for ( int i=0 ; i<nlVars.getLength() ; i++ ) {
                final Element varNode = (Element) nlVars.item(i);
                final IAST2variable var =
                    parser.getFactory().newVariable(varNode.getAttribute("nom"));
                vars.add(var);
                final IAST2expression<CEASTparseException> init =
                    (IAST2expression<CEASTparseException>)
                    parser.findThenParseChildAsUnique(
                        varNode.getParentNode(),
                        "initialisation");
                inits.add(init);
                
            }
            variables = vars.toArray(CEASTvariable.EMPTY_VARIABLE_ARRAY);
            initializations =
                inits.toArray(CEASTexpression.EMPTY_EXPRESSION_ARRAY);
        } catch (XPathExpressionException e1) {
            throw new CEASTparseException(e1);
        }
        IAST2instruction<CEASTparseException> body =
            (IAST2instruction<CEASTparseException>)
            parser.findThenParseChildAsSequence(e, "corps");
        return parser.getFactory()
            .newLocalBlock(variables, initializations, body);
    }
    private static final XPath xPath = XPathFactory.newInstance().newXPath();
    // NOTE: factoriser xPath plus globalement ?

    //NOTE: Accès direct aux champs interdit à partir d'ici!

    public Object eval (final ILexicalEnvironment lexenv,
                        final ICommon common)
    throws EvaluationException {
        ILexicalEnvironment newlexenv = lexenv;
        final IAST2variable[] vars = getVariables();
        final IAST2expression<CEASTparseException>[] inits = getInitializations();
        for (int i = 0; i < vars.length; i++) {
            newlexenv = newlexenv.extend(vars[i], inits[i].eval(
                    lexenv, common));
        }
        return getBody().eval(newlexenv, common);
    }

    public void compileInstruction (final StringBuffer buffer,
                                    final ICgenLexicalEnvironment lexenv,
                                    final ICgenEnvironment common,
                                    final IDestination destination )
    throws CgenerationException {
        final IAST2variable[] vars = getVariables();
        final IAST2expression<CEASTparseException>[] inits = getInitializations();
        IAST2variable[] temp = new IAST2variable[vars.length];
        ICgenLexicalEnvironment templexenv = lexenv;

        buffer.append("{\n");
        for (int i = 0; i < vars.length; i++) {
            temp[i] = CEASTvariable.generateVariable();
            templexenv = templexenv.extend(temp[i]);
            temp[i].compileDeclaration(buffer, templexenv, common);
        }

        for (int i = 0; i < vars.length; i++) {
            inits[i].compileInstruction(
                    buffer, templexenv, common,
                    new AssignDestination(temp[i]) );
        }

        buffer.append("{\n");
        ICgenLexicalEnvironment bodylexenv = templexenv;
        for (int i = 0; i < vars.length; i++) {
            bodylexenv = bodylexenv.extend(vars[i]);
            vars[i].compileDeclaration(buffer, bodylexenv, common);
        }

        for (int i = 0; i < vars.length; i++) {
            buffer.append(bodylexenv.compile(vars[i]));
            buffer.append(" = ");
            buffer.append(bodylexenv.compile(temp[i]));
            buffer.append(";\n");
        }

        getBody().compileInstruction(buffer, bodylexenv, common, destination);
        buffer.append("}\n}\n");
    }

    @Override
    public void findGlobalVariables (
            final Set<IAST2variable> globalvars,
            final ICgenLexicalEnvironment lexenv ) {
        ICgenLexicalEnvironment bodylexenv = lexenv;
        for ( IAST2variable var : getVariables() ) {
            bodylexenv = bodylexenv.extend(var);
        }
        for ( IAST2expression<CEASTparseException> expr : getInitializations() ) {
            expr.findGlobalVariables(globalvars, lexenv);
        }
        getBody().findGlobalVariables(globalvars, bodylexenv);
    }
}

// end of CEASTlocalBlock.java
