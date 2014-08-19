package fr.upmc.ilp.ilp4.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.xml.sax.SAXException;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp4.ast.CEASTFactory;
import fr.upmc.ilp.ilp4.ast.CEASTfunctionDefinition;
import fr.upmc.ilp.ilp4.ast.CEASTglobalFunctionVariable;
import fr.upmc.ilp.ilp4.ast.CEASTglobalVariable;
import fr.upmc.ilp.ilp4.ast.CEASTinteger;
import fr.upmc.ilp.ilp4.ast.CEASTinvocation;
import fr.upmc.ilp.ilp4.ast.CEASTlocalVariable;
import fr.upmc.ilp.ilp4.ast.CEASTprogram;
import fr.upmc.ilp.ilp4.ast.CEASTreference;
import fr.upmc.ilp.ilp4.ast.CEASTvariable;
import fr.upmc.ilp.ilp4.ast.InliningException;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.ast.NormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.ast.NormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.XMLwriter;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

public class XMLwriterTest extends XMLTestCase {

    @Override
    public void setUp () throws EvaluationException {
        // pour normaliser:
        normlexenv = new NormalizeLexicalEnvironment.EmptyNormalizeLexicalEnvironment();
        normcommon = new NormalizeGlobalEnvironment();
        normcommon.addPrimitive(new CEASTglobalVariable("print"));
        normcommon.addPrimitive(new CEASTglobalVariable("newline"));
        normcommon.addPrimitive(new CEASTglobalVariable("throw"));
    }
    // pour normaliser:
    protected INormalizeLexicalEnvironment normlexenv;
    protected INormalizeGlobalEnvironment normcommon;

    // test sur un programme entier avant normalisation
    public void testBeforeAndAfterNormalization ()
    throws ParserConfigurationException,
           TransformerConfigurationException,
           TransformerException,
           SAXException, IOException, XpathException,
           NormalizeException, InliningException {
        // fonction unaire
        final CEASTfunctionDefinition function_definition =
            new CEASTfunctionDefinition(
                    new CEASTglobalFunctionVariable("foobar"),  // <--------
                    new IAST4variable[] {
                            new CEASTlocalVariable("x"),
                    },
                    new CEASTinteger("1") );
        // invocation unaire
        CEASTinvocation invocation =
            new CEASTinvocation(
                    new CEASTreference(new CEASTvariable("foobar")),  // <--------
                    new IAST4expression[] {
                        new CEASTinteger("2"),
                    } );
        // construire un programme entier
        CEASTprogram program = new CEASTprogram(
                new IAST4functionDefinition[] {
                        function_definition,
                },
                invocation );
        XMLwriter visitor1 = new XMLwriter();
        String xml = visitor1.process(program);
        System.out.println(xml);
        assertNotNull(xml);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram", xml);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram/functionDefinitions", xml);
        assertXpathEvaluatesTo("1", "count(/fr.upmc.ilp.ilp4.ast.CEASTprogram/functionDefinitions/*)", xml);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram/globalVariables", xml);
        assertXpathEvaluatesTo("0", "count(/fr.upmc.ilp.ilp4.ast.CEASTprogram/globalVariables/*)", xml);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram/programBody", xml);
        assertXpathEvaluatesTo("1", "count(/fr.upmc.ilp.ilp4.ast.CEASTprogram/programBody/*)", xml);

        // Normalisation
        IAST4Factory factory = new CEASTFactory();
        IAST4 nceast = program.normalize(normlexenv, normcommon, factory);
        assertTrue(nceast instanceof CEASTprogram);
        CEASTprogram prog = (CEASTprogram) nceast;
        XMLwriter visitor2 = new XMLwriter();
        String xml2 = visitor2.process(prog);
        System.out.println(xml2);
        assertNotNull(xml2);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram", xml2);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram/functionDefinitions", xml2);
        assertXpathEvaluatesTo("2", "count(/fr.upmc.ilp.ilp4.ast.CEASTprogram/functionDefinitions/*)", xml2);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram/globalVariables", xml2);
        assertXpathEvaluatesTo("0", "count(/fr.upmc.ilp.ilp4.ast.CEASTprogram/globalVariables/*)", xml2);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram/programBody", xml2);
        assertXpathEvaluatesTo("1", "count(/fr.upmc.ilp.ilp4.ast.CEASTprogram/programBody/*)", xml2);

        // integration
        nceast.inline(factory);
        XMLwriter visitor3 = new XMLwriter();
        String xml3 = visitor3.process(prog);
        System.out.println(xml3);
        assertNotNull(xml3);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram", xml3);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram/functionDefinitions", xml3);
        // Je ne retire plus les definitions integrees!
        //assertXpathEvaluatesTo("0", "count(/fr.upmc.ilp.ilp4.ast.CEASTprogram/functionDefinitions/*)", xml3);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram/globalVariables", xml3);
        assertXpathEvaluatesTo("0", "count(/fr.upmc.ilp.ilp4.ast.CEASTprogram/globalVariables/*)", xml3);
        assertXpathExists("/fr.upmc.ilp.ilp4.ast.CEASTprogram/programBody", xml3);
        assertXpathEvaluatesTo("1", "count(/fr.upmc.ilp.ilp4.ast.CEASTprogram/programBody/*)", xml3);
        assertXpathEvaluatesTo("true", "/fr.upmc.ilp.ilp4.ast.CEASTprogram/programBody/*/@inlined", xml3);
    }
}
