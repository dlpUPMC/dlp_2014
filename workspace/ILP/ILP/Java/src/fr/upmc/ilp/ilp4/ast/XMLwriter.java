package fr.upmc.ilp.ilp4.ast;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp4.interfaces.IAST4alternative;
import fr.upmc.ilp.ilp4.interfaces.IAST4assignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4binaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4computedInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4constant;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4invocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4localAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4localBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4primitiveInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4reference;
import fr.upmc.ilp.ilp4.interfaces.IAST4sequence;
import fr.upmc.ilp.ilp4.interfaces.IAST4try;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.IAST4while;

/** Un arpenteur d'AST le transformant en XML. Une fois instancié, il est
 * possible de l'utiliser plusieurs fois.
 */

public class XMLwriter
implements IAST4visitor<Object, Element, RuntimeException> {

    public XMLwriter ()
    throws ParserConfigurationException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        this.documentBuilder = dbf.newDocumentBuilder();
    }
    protected final DocumentBuilder documentBuilder;
    protected Document document;
    protected Map<Object,Element> memory;

    protected synchronized int getCounter () {
        return counter++;
    }
    private int counter = 1000;

    /** Obtenir l'XML correspondant a l'AST. Cette methode ne peut etre
     * utilisee qu'apres process(). */

    public String getXML ()
    throws TransformerConfigurationException, TransformerException {
        if ( null == this.result ) {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", 2);
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // FUTURE devrait figurer en propriete de compilation:
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            // NOTE: xmllint -format f.xml > g.xml est aussi possible.
            // Voir aussi XmlStarlet en xmlstar.sourceforge.net
            DOMSource source = new DOMSource(this.document);
            Writer writer = new StringWriter();
            StreamResult sr = new StreamResult(writer);
            transformer.transform(source, sr);
            this.result = writer.toString();
        }
        return this.result;
    }
    protected @OrNull String result;

  /** La methode initiale pour lancer le traitement sur un programme
   * ILP entier. La methode getXML() permet de recuperer le resultat
   * de la traduction vers DOM. */

    public synchronized String process (IAST4visitable iast) {
        this.result = null;
        this.document = this.documentBuilder.newDocument();
        this.memory = new HashMap<>();
        Element lastVisitedElement = iast.accept(this, null);
        this.document.appendChild(lastVisitedElement);
        try {
            return getXML();
        } catch (TransformerConfigurationException e) {
            return null;
        } catch (TransformerException e) {
            return null;
        }
    }

    // Tous les visiteurs specialises. Ils convertissent un noeud IAST4
    // en un element DOM qu'ils retournent en valeur. La variable data
    // ne sert a rien ici.

    // Tous les visiteurs ont la meme structure:
    // Pour un iast donné, créer le noeud XML demandé,
    // chercher dans la memoire si l'iast avait déjà un noeud XML
    // associé et si oui, indiquer le partage avec un idref. Si non,
    // arpenter les composants de l'iast et les integrer au noeud XML
    // courant. Finalement, retourner le noeud XML créé.

    public Element visit (IAST4program iast, Object data) {
        final Element program = this.document.createElement(
                iast.getClass().getName() );
        final Element definitions =
            this.document.createElement("functionDefinitions");
        program.appendChild(definitions);
        for ( IAST4functionDefinition fun : iast.getFunctionDefinitions() ) {
            Element lastVisitedElement = fun.accept(this, null);
            definitions.appendChild(lastVisitedElement);
        }

        final Element globals = this.document.createElement("globalVariables");
        program.appendChild(globals);
        for ( IAST4globalVariable gv : iast.getGlobalVariables() ) {
            Element lastVisitedElement = gv.accept(this, null);
            globals.appendChild(lastVisitedElement);
        }

        final Element body = this.document.createElement("programBody");
        program.appendChild(body);
        Element lastVisitedElement = iast.getBody().accept(this, null);
        body.appendChild(lastVisitedElement);
        return program;
    }

    public Element visit (IAST4alternative iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("ternary", (iast.isTernary())?"true":"false");
            Element lastVisitedElement = iast.getCondition().accept(this, null);
            result.appendChild(lastVisitedElement);
            lastVisitedElement = iast.getConsequent().accept(this, null);
            result.appendChild(lastVisitedElement);
            lastVisitedElement = iast.getAlternant().accept(this, null);
            result.appendChild(lastVisitedElement);
        }
        return result;
    }

    public Element visit (IAST4assignment iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            Element lastVisitedElement = iast.getVariable().accept(this, null);
            result.appendChild(lastVisitedElement);
            lastVisitedElement = iast.getValue().accept(this, null);
            result.appendChild(lastVisitedElement);
        }
        return result;
    }
    public Element visit (IAST4localAssignment iast, Object data) {
        return visit((IAST4assignment) iast, data);
    }
    public Element visit (IAST4globalAssignment iast, Object data) {
        return visit((IAST4assignment) iast, data);
    }

    public Element visit (IAST4constant iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("value", iast.getValue().toString());
        }
        return result;
    }

    public Element visit (IAST4invocation iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            final Element fun = this.document.createElement("function");
            result.appendChild(fun);
            Element lastVisitedElement = iast.getFunction().accept(this, null);
            fun.appendChild(lastVisitedElement);

            final Element args = this.document.createElement("arguments");
            result.appendChild(args);
            for ( IAST4expression arg : iast.getArguments() ) {
                lastVisitedElement = arg.accept(this, null);
                args.appendChild(lastVisitedElement);
            }
        }
        return result;
    }

    public Element visit(IAST4computedInvocation iast, Object data) {
        return visit((IAST4invocation) iast, data);
    }

    public Element visit (IAST4globalInvocation iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            IAST4expression inlined = iast.getInlined();
            result.setAttribute("id", "" + getCounter());
            result.setAttribute("inlined",
                    (inlined != null) ? "true" : "false" );
            this.memory.put(iast, result);
            // Serialisation:
            if ( inlined != null ) {
                final Element inl = this.document.createElement("inlined");
                result.appendChild(inl);
                Element lastVisitedElement = inlined.accept(this, null);
                inl.appendChild(lastVisitedElement);
            }

            final Element fun = this.document.createElement("globalFunction");
            result.appendChild(fun);
            Element lastVisitedElement = iast.getFunctionGlobalVariable().accept(this, null);
            fun.appendChild(lastVisitedElement);

            final Element args = this.document.createElement("arguments");
            result.appendChild(args);
            for ( IAST4expression arg : iast.getArguments() ) {
                lastVisitedElement = arg.accept(this, null);
                args.appendChild(lastVisitedElement);
            }
        }
        return result;
    }

    public Element visit (IAST4primitiveInvocation iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            final Element fun = this.document.createElement("primitivefunction");
            result.appendChild(fun);
            Element lastVisitedElement = iast.getFunctionGlobalVariable().accept(this, null);
            fun.appendChild(lastVisitedElement);

            final Element args = this.document.createElement("arguments");
            result.appendChild(args);
            for ( IAST4expression arg : iast.getArguments() ) {
                lastVisitedElement = arg.accept(this, null);
                args.appendChild(lastVisitedElement);
            }
        }
        return result;
    }

    public Element visit (IAST4functionDefinition iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("name", iast.getFunctionName());
            result.setAttribute("recursive",
                    Boolean.toString(iast.isRecursive()) );

            Element lastVisitedElement = iast.getDefinedVariable().accept(this, null);
            result.appendChild(lastVisitedElement);

            final Element funs =
                this.document.createElement("invokedFunctions");
            result.appendChild(funs);
            for ( IAST4globalFunctionVariable gfv : iast.getInvokedFunctions() ) {
                lastVisitedElement = gfv.accept(this, null);
                funs.appendChild(lastVisitedElement);
            }

            final Element vars = this.document.createElement("variables");
            result.appendChild(vars);
            for ( IAST4variable lv : iast.getVariables() ) {
                lastVisitedElement = lv.accept(this, null);
                vars.appendChild(lastVisitedElement);
            }

            final Element body = this.document.createElement("body");
            result.appendChild(body);
            lastVisitedElement = iast.getBody().accept(this, null);
            body.appendChild(lastVisitedElement);
        }
        return result;
    }

    public Element visit (IAST4reference iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            final Element var = this.document.createElement("variable");
            result.appendChild(var);
            Element lastVisitedElement = iast.getVariable().accept(this, null);
            var.appendChild(lastVisitedElement);
        }
        return result;
    }

    public Element visit (IAST4variable iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("name", iast.getName());
            result.setAttribute("mangledName", iast.getMangledName());
        }
        return result;
    }

    public Element visit (IAST4localBlock iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            final Element vars = this.document.createElement("variables");
            result.appendChild(vars);
            for ( IAST4variable lv : iast.getVariables() ) {
                Element lastVisitedElement = lv.accept(this, null);
                vars.appendChild(lastVisitedElement);
            }

            final Element inits = this.document.createElement("initialisations");
            result.appendChild(inits);
            for ( IAST4expression lvinit : iast.getInitializations() ) {
                Element lastVisitedElement = lvinit.accept(this, null);
                inits.appendChild(lastVisitedElement);
            }

            final Element body = this.document.createElement("body");
            result.appendChild(body);
            Element lastVisitedElement = iast.getBody().accept(this, null);
            body.appendChild(lastVisitedElement);
        }
        return result;
    }

    public Element visit (IAST4binaryOperation iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("operation", iast.getOperatorName());
            Element lastVisitedElement = iast.getLeftOperand().accept(this, null);
            result.appendChild(lastVisitedElement);
            lastVisitedElement = iast.getRightOperand().accept(this, null);
            result.appendChild(lastVisitedElement);
        }
        return result;
    }

    public Element visit (IAST4unaryOperation iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("operation", iast.getOperatorName());
            Element lastVisitedElement = iast.getOperand().accept(this, null);
            result.appendChild(lastVisitedElement);
        }
        return result;
    }

    public Element visit (IAST4sequence iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            for ( IAST4expression instr : iast.getInstructions() ) {
                Element lastVisitedElement = instr.accept(this, null);
                result.appendChild(lastVisitedElement);
            }
        }
        return result;
    }

    public Element visit (IAST4try iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            final Element body = this.document.createElement("body");
            result.appendChild(body);
            Element lastVisitedElement = iast.getBody().accept(this, null);
            body.appendChild(lastVisitedElement);

            final Element catcher = this.document.createElement("catcher");
            if ( null != iast.getCaughtExceptionVariable() ) {
                result.appendChild(catcher);
                final Element var =
                    this.document.createElement("caughtVariable");
                catcher.appendChild(var);
                lastVisitedElement = iast.getCaughtExceptionVariable().accept(this, null);
                var.appendChild(lastVisitedElement);

                final Element catcherBody =
                    this.document.createElement("catcherBody");
                catcher.appendChild(catcherBody);
                lastVisitedElement = iast.getCatcher().accept(this, null);
                catcher.appendChild(lastVisitedElement);
            }

            final Element finallyer = this.document.createElement("finallyer");
            if ( null != iast.getFinallyer() ) {
                result.appendChild(finallyer);
                lastVisitedElement = iast.getFinallyer().accept(this, null);
                finallyer.appendChild(lastVisitedElement);
            }
        }
        return result;
    }

    public Element visit (IAST4unaryBlock iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            final Element var = this.document.createElement("variable");
            result.appendChild(var);
            Element lastVisitedElement = iast.getVariable().accept(this, null);
            var.appendChild(lastVisitedElement);

            final Element init = this.document.createElement("initialisation");
            result.appendChild(init);
            lastVisitedElement = iast.getInitialization().accept(this, null);
            init.appendChild(lastVisitedElement);

            final Element body = this.document.createElement("body");
            result.appendChild(body);
            lastVisitedElement = iast.getBody().accept(this, null);
            body.appendChild(lastVisitedElement);
        }
        return result;
    }

    public Element visit (IAST4while iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            final Element cond = this.document.createElement("condition");
            result.appendChild(cond);
            Element lastVisitedElement = iast.getCondition().accept(this, null);
            cond.appendChild(lastVisitedElement);

            final Element body = this.document.createElement("body");
            result.appendChild(body);
            lastVisitedElement = iast.getBody().accept(this, null);
            body.appendChild(lastVisitedElement);
        }
        return result;
    }
}
