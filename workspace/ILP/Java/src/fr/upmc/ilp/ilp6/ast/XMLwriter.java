package fr.upmc.ilp.ilp6.ast;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6instantiation;
import fr.upmc.ilp.ilp6.interfaces.IAST6methodDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6program;
import fr.upmc.ilp.ilp6.interfaces.IAST6readField;
import fr.upmc.ilp.ilp6.interfaces.IAST6self;
import fr.upmc.ilp.ilp6.interfaces.IAST6send;
import fr.upmc.ilp.ilp6.interfaces.IAST6super;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.IAST6writeField;

public class XMLwriter 
extends fr.upmc.ilp.ilp4.ast.XMLwriter 
implements IAST6visitor<Object, Element, RuntimeException> {
    
    public XMLwriter () 
    throws ParserConfigurationException {
        super();
    }

    public Element visit(IAST6classDefinition iast, Object data) {
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
            result.setAttribute("super", iast.getSuperClassName());
            StringBuffer sb = new StringBuffer();
            for (String fieldName : iast.getProperFieldNames()) {
                sb.append(fieldName).append(" ");
            }
            result.setAttribute("properFieldNames", sb.toString());
            final Element methods = this.document.createElement("properMethods");
            result.appendChild(methods);
            Element lastVisitedElement = null;
            for (IAST6methodDefinition md : iast.getProperMethodDefinitions()) {
                lastVisitedElement = md.accept(this, data);
                methods.appendChild(lastVisitedElement);
            }
       }
       return result;
    }
    public Element visit(IAST6instantiation iast, Object data) {
           final Element result = this.document.createElement(
                    iast.getClass().getName() );
       if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
       } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("className", iast.getClassName()); 
            Element lastVisitedElement;
            for ( IAST4expression e : iast.getArguments()) {
                lastVisitedElement = e.accept(this, null);
                result.appendChild(lastVisitedElement);
            }
       }
       return result;
    }
    public Element visit(IAST6methodDefinition iast, Object data) {
           final Element result = this.document.createElement(
                    iast.getClass().getName() );
       if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
       } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("methodName", iast.getMethodName());
            result.setAttribute("realArity", ""+iast.getRealArity());
            final Element definition = this.document.createElement("function");
            result.appendChild(definition);

            definition.setAttribute("name", iast.getFunctionName());
            definition.setAttribute("recursive",
                    Boolean.toString(iast.isRecursive()) );

            Element lastVisitedElement = iast.getDefinedVariable().accept(this, null);
            definition.appendChild(lastVisitedElement);

            final Element funs =
                this.document.createElement("invokedFunctions");
            definition.appendChild(funs);
            for ( IAST4globalFunctionVariable gfv : iast.getInvokedFunctions() ) {
                lastVisitedElement = gfv.accept(this, null);
                funs.appendChild(lastVisitedElement);
            }

            final Element vars = this.document.createElement("variables");
            definition.appendChild(vars);
            for ( IAST4variable lv : iast.getVariables() ) {
                lastVisitedElement = lv.accept(this, null);
                vars.appendChild(lastVisitedElement);
            }

            final Element body = this.document.createElement("body");
            definition.appendChild(body);
            lastVisitedElement = iast.getBody().accept(this, null);
            body.appendChild(lastVisitedElement);
       }
       return result;
    }
    public Element visit(IAST6program iast, Object data) {
           final Element result = this.document.createElement(
                    iast.getClass().getName() );
       if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
       } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            Element lastVisitedElement;
            final Element classes = this.document.createElement("classes");
            result.appendChild(classes);
            for ( IAST6classDefinition cd : iast.getClassDefinitions() ) {
                lastVisitedElement = cd.accept(this, null);
                classes.appendChild(lastVisitedElement);
            }
            final Element functions = this.document.createElement("functions");
            result.appendChild(functions);
            for ( IAST4functionDefinition fd : iast.getFunctionDefinitions() ) {
                lastVisitedElement = fd.accept(this, null);
                functions.appendChild(lastVisitedElement);
            }
       }
       return result;
    }

    public Element visit(IAST6readField iast, Object data) {
       final Element result = this.document.createElement(
                    iast.getClass().getName() );
       if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
       } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("fieldname", iast.getFieldName()); 
            Element lastVisitedElement = iast.getObject().accept(this, null);
            result.appendChild(lastVisitedElement);
       }
       return result;
    }

    public Element visit(IAST6self iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
        }
        return result;
    }

    public Element visit(IAST6send iast, Object data) {
           final Element result = this.document.createElement(
                   iast.getClass().getName() );
           if ( this.memory.containsKey(iast) ) {
                final Element old = this.memory.get(iast);
                result.setAttribute("idref", old.getAttribute("id"));
           } else {
                result.setAttribute("id", "" + getCounter());
                this.memory.put(iast, result);
                // Serialisation:
                result.setAttribute("methodName", iast.getMethodName()); 
                Element lastVisitedElement = iast.getReceiver().accept(this, null);
                result.appendChild(lastVisitedElement);
                for ( IAST4expression arg : iast.getArguments() ) {
                    lastVisitedElement = arg.accept(this, null);
                    result.appendChild(lastVisitedElement);
                }
           }
           return result;
    }   

    public Element visit(IAST6super iast, Object data) {
        final Element result = this.document.createElement(
                iast.getClass().getName() );
        if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
        } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
        }
        return result;
    }

    public Element visit(IAST6writeField iast, Object data) {
       final Element result = this.document.createElement(
               iast.getClass().getName() );
       if ( this.memory.containsKey(iast) ) {
            final Element old = this.memory.get(iast);
            result.setAttribute("idref", old.getAttribute("id"));
       } else {
            result.setAttribute("id", "" + getCounter());
            this.memory.put(iast, result);
            // Serialisation:
            result.setAttribute("fieldname", iast.getFieldName()); 
            Element lastVisitedElement = iast.getObject().accept(this, null);
            result.appendChild(lastVisitedElement);
            lastVisitedElement = iast.getValue().accept(this, null);
            result.appendChild(lastVisitedElement);
       }
       return result;
    }
}
