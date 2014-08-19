/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTwriteField.java 542 2006-10-26 18:53:30Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.ast;

import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.CEAST;
import fr.upmc.ilp.ilp4.ast.CEASTlocalVariable;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.IAST6writeField;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IParser6;
import fr.upmc.ilp.ilp6.runtime.ICommon;
import fr.upmc.ilp.ilp6.runtime.ILPinstance;

/** Modifier un champ dans un objet. */

public class CEASTwriteField
extends CEAST6expression
implements IAST6writeField {

  public CEASTwriteField (final String fieldName,
                          final IAST4expression object,
                          final IAST4expression value) {
    this.fieldName = fieldName;
    this.object = object;
    this.value = value;
  }
  private final String fieldName;
  private final IAST4expression object;
  private final IAST4expression value;

  @ILPexpression
  public IAST4expression getObject () {
      return this.object;
  }
  @ILPexpression
  public IAST4expression getValue () {
      return this.value;
  }
  public String getFieldName () {
      return this.fieldName;
  }

  public static IAST6writeField parse (
          final Element e, final IParser6 parser)
  throws CEASTparseException {
      final String fieldName = e.getAttribute("champ");
      try {
          final XPathExpression targetPath = xPath.compile("./cible/*");
          final Node nTarget = (Node)
              targetPath.evaluate(e, XPathConstants.NODE);
          final IAST4expression target = CEAST.narrowToIAST4expression(
                  parser.parse(nTarget) );

          final XPathExpression valuePath = xPath.compile("./valeur/*");
          final Node nValue = (Node)
              valuePath.evaluate(e, XPathConstants.NODE);
          final IAST4expression value = CEAST.narrowToIAST4expression(
                  parser.parse(nValue) );

          return parser.getFactory().newWriteField(fieldName, target, value);
      } catch (XPathExpressionException e1) {
          final String msg = "Should never occur!";
          assert false : msg;
          throw new CEASTparseException(msg);
      }
  }
  private static final XPath xPath = XPathFactory.newInstance().newXPath();
  // NOTE: partager ce XPath.

  @Override
  public Object eval6 (final ILexicalEnvironment lexenv,
                       final ICommon common)
    throws EvaluationException {
    final Object target = getObject().eval(lexenv, common);
    final Object newValue = getValue().eval(lexenv, common);
    if ( target instanceof ILPinstance ) {
        ILPinstance o = (ILPinstance) target;
        return o.write(getFieldName(), newValue);
    } else {
        throw new EvaluationException("Wrong class");
    }
  }

  @Override
  public void compile6 (final StringBuffer buffer,
                        final ICgenLexicalEnvironment lexenv,
                        final ICgenEnvironment common,
                        final IDestination destination)
    throws CgenerationException {
    final IAST4variable tempvar = CEASTlocalVariable.generateVariable();
    final IAST4variable tempValue = CEASTlocalVariable.generateVariable();
    buffer.append("{ ");
    tempvar.compileDeclaration(buffer, lexenv, common);
    tempValue.compileDeclaration(buffer, lexenv, common);
    getObject().compile(buffer, lexenv, common, new AssignDestination(tempvar));
    buffer.append(";\n");
    getValue().compile(buffer, lexenv, common, new AssignDestination(tempValue));
    buffer.append(";\n");
    buffer.append("if ( ILP_IsA(");
    buffer.append(tempvar.getMangledName());
    buffer.append(", (ILP_Class) &ILP_object_");
    final IAST6classDefinition cd =
      common.findDefiningClassDefinition(getFieldName());
    buffer.append(cd.getName());
    buffer.append("_class) ) {\n");
    destination.compile(buffer, lexenv, common);
    buffer.append(" (");
    buffer.append(tempvar.getMangledName());
    buffer.append("->_content.asInstance.field[");
    buffer.append(cd.getFieldOffset(getFieldName(), common));
    buffer.append("] = ");
    buffer.append(tempValue.getMangledName());
    buffer.append(");\n} else {\n");
    destination.compile(buffer, lexenv, common);
    buffer.append(" ILP_UnknownFieldError(\"");
    buffer.append(getFieldName());
    buffer.append("\", ");
    buffer.append(tempvar.getMangledName());
    buffer.append(");\n}\n}\n");
  }

  @Override
  public void findGlobalVariables (
          final Set<IAST2variable> globalvars,
          final fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment lexenv ) {
      getObject().findGlobalVariables(globalvars, lexenv);
      getValue().findGlobalVariables(globalvars, lexenv);
  }

  @Override
  public IAST6writeField normalize6 (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          IAST6Factory factory )
    throws NormalizeException {
    IAST4expression object_ = getObject().normalize(lexenv, common, factory);
    IAST4expression value_ =  getValue().normalize(lexenv, common, factory);
    return factory.newWriteField(getFieldName(), object_, value_);
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
}

// end of CEASTwriteField.java
