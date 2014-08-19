/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTfunctionDefinition.java 405 2006-09-13 17:21:53Z queinnec $
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
import fr.upmc.ilp.ilp2.cgen.ReturnDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;
import fr.upmc.ilp.ilp2.runtime.UserGlobalFunction;
import fr.upmc.ilp.tool.CStuff;

public class CEASTfunctionDefinition
  extends CEAST
  implements IAST2functionDefinition<CEASTparseException> {

  public CEASTfunctionDefinition (
          final String name,
          final IAST2variable[] variables,
          final IAST2instruction<CEASTparseException> body ) {
      this.name = name;
      this.variable = variables;
      this.body = body;
  }
  private final String name;
  private final IAST2variable[] variable;
  private final IAST2instruction<CEASTparseException> body;

  public String getFunctionName () {
      return this.name;
  }
  public String getMangledFunctionName () {
      return CStuff.mangle(this.name);
  }
  public IAST2variable[] getVariables () {
      return this.variable;
  }
  public IAST2instruction<CEASTparseException> getBody () {
      return this.body;
  }

  public static IAST2functionDefinition<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      final NodeList nl = e.getChildNodes();
      IAST2variable[] variables = new IAST2variable[0];
      String name = e.getAttribute("nom");
      try {
          final XPathExpression varsPath = xPath.compile("variables/*");
          final NodeList nlVars = (NodeList)
          varsPath.evaluate(e, XPathConstants.NODESET);
          final List<IAST2variable> vars = new Vector<>();
          for ( int i=0 ; i<nlVars.getLength() ; i++ ) {
              final Element varNode = (Element) nlVars.item(i);
              final IAST2variable var =
                  parser.getFactory().newVariable(varNode.getAttribute("nom"));
              vars.add(var);
          }
          variables = vars.toArray(new IAST2variable[]{});
      } catch (XPathExpressionException e1) {
          throw new CEASTparseException(e1);
      }
      IAST2instruction<CEASTparseException> body = 
          (IAST2instruction<CEASTparseException>)
              parser.findThenParseChildAsSequence(nl, "corps");
      return parser.getFactory().newFunctionDefinition(
              name, variables, body );
  }
  private static final XPath xPath = XPathFactory.newInstance().newXPath();

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
    final Object function =
        new UserGlobalFunction(
                getFunctionName(),
                getVariables(),
                getBody());
    common.updateGlobal(getFunctionName(), function);
    return function;
  }

  public void compileHeader (final StringBuffer buffer,
                             final ICgenLexicalEnvironment lexenv,
                             final ICgenEnvironment common)
    throws CgenerationException {
    buffer.append("static ILP_Object ");
    buffer.append(getMangledFunctionName());
    final ICgenLexicalEnvironment lexenv2 = this.extend(lexenv);
    this.compileVariableList(buffer, lexenv2, common);
    buffer.append(";\n");
  }

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

  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common )
    throws CgenerationException {
    buffer.append("\nILP_Object\n");
    buffer.append(getMangledFunctionName());
    final ICgenLexicalEnvironment lexenv2 = this.extend(lexenv);
    compileVariableList(buffer, lexenv2, common);
    buffer.append("\n");
    getBody().compileInstruction(
            buffer, lexenv2, common, ReturnDestination.create());
    buffer.append("\n");
  }

  public void compileVariableList (final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final ICgenEnvironment common)
    throws CgenerationException {
    buffer.append(" (");
    final IAST2variable[] vars = getVariables();
    for ( int i = 0 ; i<vars.length-1 ; i++ ) {
      buffer.append("    ILP_Object ");
      buffer.append(vars[i].getMangledName());
      buffer.append(",\n");
    }
    if ( vars.length > 0 ) {
      buffer.append("    ILP_Object ");
      buffer.append(vars[vars.length-1].getMangledName());
    }
    buffer.append(" ) ");
  }

}

// end of CEASTfunctionDefinition.java
