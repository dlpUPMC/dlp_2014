package fr.upmc.ilp.ilp5.ast;

import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp5.interfaces.IAST5codefinedLocalFunctions;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionDefinition;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionVariable;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitable;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitor;

public class XMLwriter
extends fr.upmc.ilp.ilp4.ast.XMLwriter
implements IAST5visitor<Object, Element, RuntimeException> {

    public XMLwriter()
    throws ParserConfigurationException {
        super();
    }

    public synchronized String process (IAST5visitable iast) {
        this.result = null;
        this.document = this.documentBuilder.newDocument();
        this.memory = new HashMap<>();
        Element lastVisitedElement = (Element) iast.accept(this, null);
        this.document.appendChild(lastVisitedElement);
        try {
            return getXML();
        } catch (TransformerConfigurationException e) {
            return null;
        } catch (TransformerException e) {
            return null;
        }
    }

    public Element visit(IAST5codefinedLocalFunctions iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            final Element funs = this.document.createElement("fonctions");
            result.appendChild(funs);
            for ( IAST5localFunctionDefinition fundef : iast.getFunctions() ) {
                Element lastVisitedElement = (Element)
                    fundef.accept(this, data);
                funs.appendChild(lastVisitedElement);
            }

            final Element body = this.document.createElement("body");
            result.appendChild(body);
            Element lastVisitedElement = (Element)
                iast.getBody().accept(this, data);
            body.appendChild(lastVisitedElement);
        }
        return result;
    }

    public Element visit(IAST5localFunctionDefinition iast, Object data) {
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

            Element lastVisitedElement = (Element)
                iast.getDefinedVariable().accept(this, data);
            result.appendChild(lastVisitedElement);

            final Element funs =
                this.document.createElement("invokedFunctions");
            result.appendChild(funs);
            for ( IAST4globalFunctionVariable gfv : iast.getInvokedFunctions() ) {
                lastVisitedElement = (Element) gfv.accept(this, data);
                funs.appendChild(lastVisitedElement);
            }

            final Element vars = this.document.createElement("variables");
            result.appendChild(vars);
            for ( IAST4variable lv : iast.getVariables() ) {
                lastVisitedElement = (Element) lv.accept(this, data);
                vars.appendChild(lastVisitedElement);
            }

            final Element body = this.document.createElement("body");
            result.appendChild(body);
            lastVisitedElement = (Element)
                iast.getBody().accept(this, data);
            body.appendChild(lastVisitedElement);
        }
        return result;
    }

    public Element visit (IAST5localFunctionVariable iast, Object data) {
        return visit((IAST4variable) iast, data);
    }

    public Element visit(IAST4globalVariable v, Object data) {
        return visit((IAST4variable) v, data);
    }

    public Element visit(IAST4globalFunctionVariable v, Object data) {
        return visit((IAST4variable) v, data);
    }

    public Element visit(IAST4localVariable v, Object data) {
        return visit((IAST4variable) v, data);
    }
}
