/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTParser.java 1243 2012-09-16 08:01:36Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.ilp2.ast.AbstractParser;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4sequence;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** Transformer un document XML en un CEAST. */

public class CEASTParser extends AbstractParser
implements IParser {

    @Override
    public IAST4Factory getFactory () {
        return (IAST4Factory) super.getFactory();
    }

    public CEASTParser (IAST4Factory factory) {
        super(factory);
        this.parsers = new HashMap<>();
        addParser("alternative",         CEASTalternative.class);
        addParser("sequence",            CEASTsequence.class);
        addParser("boucle",              CEASTwhile.class);
        addParser("affectation",         CEASTassignment.class);
        addParser("definitionFonction",  CEASTfunctionDefinition.class);
        addParser("blocUnaire",          CEASTunaryBlock.class);
        addParser("blocLocal",           CEASTlocalBlock.class);
        addParser("variable",            CEASTreference.class);
        addParser("invocationPrimitive", CEASTprimitiveInvocation.class);
        addParser("invocation",          CEASTinvocation.class);
        addParser("operationUnaire",     CEASTunaryOperation.class);
        addParser("operationBinaire",    CEASTbinaryOperation.class);
        addParser("entier",              CEASTinteger.class);
        addParser("flottant",            CEASTfloat.class);
        addParser("booleen",             CEASTboolean.class);
        addParser("chaine",              CEASTstring.class);
        addParser("try",                 CEASTtry.class);
    }
    private final HashMap<String, Method> parsers;

    /** Ajout d'une caracteristique a ILP. Lorsque l'element XML nommé
     * name est lu, method(e, parser) sera invoquee. */

    public void addParser (String name, Method method) {
        this.parsers.put(name, method);
    }

    /** Ajout d'une caracteristique a ILP. Lorsque l'element XML nommé
     * name est lu, la methode clazz.parse(e, parser) sera invoquee. */

    public void addParser (String name, Class<?> clazz) {
      try {
        // Ne fonctionne plus avec ILP6: faut ruser!
        //final Method method = clazz.getMethod("parse",
        //   new Class[]{ Element.class, IParser.class } );
        for ( Method m : clazz.getMethods() ) {
            if ( ! "parse".equals(m.getName()) ) {
                continue;
            }
            if ( ! Modifier.isStatic(m.getModifiers()) ) {
                continue;
            }
            //if ( ! IAST2.class.isAssignableFrom(m.getReturnType())) {
            //    continue;
            //}
            Class<?>[] parameterTypes = m.getParameterTypes();
            if ( parameterTypes.length != 2 ) {
                continue;
            }
            if ( ! Element.class.isAssignableFrom(parameterTypes[0]) ) {
                continue;
            }
            if ( ! IParser.class.isAssignableFrom(parameterTypes[1]) ) {
                continue;
            }
            addParser(name, m);
            return;
        }
        if ( Object.class == clazz ) {
            final String msg = "Cannot find suitable parse() method!";
            throw new RuntimeException(msg);
        } else {
            addParser(name, clazz.getSuperclass());
        }
      } catch (SecurityException e1) {
        final String msg = "Cannot access parse() method!";
        throw new RuntimeException(msg);
      }
    }

  /** Convertir un noeud DOM en un noeud AST. */

    public IAST4program parse (final Document d)
    throws CEASTparseException {
        final Element e = d.getDocumentElement();
        return CEASTprogram.parse(e, this);
    }

    public IAST4 parse (final Node n)
      throws CEASTparseException {
      switch ( n.getNodeType() ) {
      case Node.ELEMENT_NODE: {
        final Element e = (Element) n;
        final String name = e.getTagName();

        if ( parsers.containsKey(name) ) {
            final Method method = parsers.get(name);
            try {
              Object result = method.invoke(null, new Object[]{e, this});
              return CEAST.narrowToIAST4(result);
            } catch (IllegalArgumentException exc) {
              throw new CEASTparseException(exc);
            } catch (IllegalAccessException exc) {
              throw new CEASTparseException(exc);
            } catch (InvocationTargetException exc) {
                Throwable t = exc.getTargetException();
                if ( t instanceof CEASTparseException ) {
                    throw (CEASTparseException) t;
                } else {
                    throw new CEASTparseException(exc);
                }
            }

        } else {
          final String msg = "Unknown element name: " + name;
          throw new CEASTparseException(msg);
        }
      }

      default: {
        final String msg = "Unknown node type: " + n.getNodeName();
        throw new CEASTparseException(msg);
      }
      }

    }

    // NOTE: meme code qu'en ILP2 sauf que les CEASTsequence sont celles d'ILP4.

    /** Trouver un sous-noeud donné et convertir ses fils en une séquence
     * d'instructions. */
    @Override
    public IAST4sequence findThenParseChildAsSequence (
             final NodeList nl, final String childName)
    throws CEASTparseException {
        return getFactory().newSequence(
                findThenParseChildAsList(nl, childName)
                    .toArray(CEASTexpression.EMPTY_EXPRESSION_ARRAY) );
    }

    @Override
    public IAST4sequence findThenParseChildAsSequence (
             final Node n, final String childName)
    throws CEASTparseException {
        return getFactory().newSequence(
                findThenParseChildAsList(n.getChildNodes(), childName)
                    .toArray(CEASTexpression.EMPTY_EXPRESSION_ARRAY) );
    }

    @Override
    public IAST4sequence parseChildrenAsSequence (final NodeList nl)
    throws CEASTparseException {
        return getFactory().newSequence(
                parseList(nl)
                .toArray(CEASTinstruction.EMPTY_EXPRESSION_ARRAY) );
    }

}

// end of CEASTParser.java
