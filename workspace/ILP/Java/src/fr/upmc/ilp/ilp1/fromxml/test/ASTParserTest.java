/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ASTParserTest.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml.test;

import org.custommonkey.xmlunit.XMLTestCase;

import fr.upmc.ilp.ilp1.fromxml.AST;
import fr.upmc.ilp.ilp1.fromxml.ASTException;
import fr.upmc.ilp.ilp1.fromxml.ASTalternative;
import fr.upmc.ilp.ilp1.fromxml.ASTinvocation;
import fr.upmc.ilp.ilp1.fromxml.ASTsequence;
import fr.upmc.ilp.ilp1.fromxml.ASTunaryBlock;
import fr.upmc.ilp.ilp1.fromxml.ASTvariable;
import fr.upmc.ilp.ilp1.interfaces.IAST;
import fr.upmc.ilp.ilp1.interfaces.IASTbinaryOperation;
import fr.upmc.ilp.ilp1.interfaces.IASTboolean;
import fr.upmc.ilp.ilp1.interfaces.IASTfloat;
import fr.upmc.ilp.ilp1.interfaces.IASTinteger;
import fr.upmc.ilp.ilp1.interfaces.IASTinvocation;
import fr.upmc.ilp.ilp1.interfaces.IASTprogram;
import fr.upmc.ilp.ilp1.interfaces.IASTsequence;
import fr.upmc.ilp.ilp1.interfaces.IASTstring;
import fr.upmc.ilp.ilp1.interfaces.IASTunaryBlock;
import fr.upmc.ilp.ilp1.interfaces.IASTunaryOperation;

/** Tests (JUnit) simples d'equivalence depuis XML -> AST -> XML. */

public class ASTParserTest extends XMLTestCase {

  public void testEntier () throws ASTException {
    String program = "<entier valeur='34'/>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTinteger);
    IASTinteger iast = (IASTinteger) a;
    assertEquals(34, iast.getValue().intValue());
  }

  public void testEntierNeg () throws ASTException {
    String program = "<entier valeur='-34'/>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTinteger);
    IASTinteger iast = (IASTinteger) a;
    assertEquals(-34, iast.getValue().intValue());
  }

  public void testFlottant () throws ASTException {
    String program = "<flottant valeur='3.14'/>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTfloat);
    IASTfloat iast = (IASTfloat) a;
    assertEquals(3.14, iast.getValue().doubleValue(), 0.01);
  }

  public void testFlottantExp () throws ASTException {
    String program = "<flottant valeur='-3.14e+3'/>";
    AST a = ASTfromXML.toAST(program);
    //assertEquals(program, a.toXML()); // Pas la meme forme en sortie!
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTfloat);
    IASTfloat iast = (IASTfloat) a;
    assertEquals(-3140, iast.getValue().doubleValue(), 0.01);
  }

  public void testBoolTrue () throws ASTException {
    String program = "<booleen valeur='true'/>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTboolean);
    IASTboolean iast = (IASTboolean) a;
    assertEquals(true, iast.getValue());
  }

  public void testBoolFalse () throws ASTException {
    String program = "<booleen valeur='false'/>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTboolean);
    IASTboolean iast = (IASTboolean) a;
    assertEquals(false, iast.getValue());
  }

  public void testChaine () throws ASTException {
    String program = "<chaine>foobar</chaine>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTstring);
    IASTstring iast = (IASTstring) a;
    assertEquals("foobar", iast.getValue());
  }

  // {{{
  // Bogue signalée par Jacques Malenfant <jacques.malenfant@lip6.fr>:
  // les conditions et consequences étaient incapables de prendre
  // plusieurs instructions! On en profite pour utiliser XMLUnit
  // signalé par Jérémy Bobbio <jeremy.bobbio@etu.upmc.fr>

  public void testAlternativeTernaire () throws Exception {
    String program = "<alternative>"
      + "<condition><booleen valeur='true'/></condition>"
      + "<consequence><entier valeur='1'/></consequence>"
      + "<alternant><chaine>foobar</chaine></alternant>"
      + "</alternative>";
    AST a = ASTfromXML.toAST(program);
    String result = a.toXML();
    //System.err.println(result);
    //assertEquals(program, result);
    assertXpathExists("/alternative", result);
    assertXpathExists("/alternative/condition", result);
    assertXpathExists("/alternative/condition/booleen", result);
    assertXpathExists("/alternative/consequence/sequence/entier", result);
    assertXpathValuesEqual("1",
                           "/alternative/consequence/sequence/entier/@valeur",
                           result);
    assertXpathExists("/alternative/alternant/sequence/chaine", result);
    assertXpathValuesEqual("foobar",
                           "/alternative/consequence/sequence/chaine",
                           result);
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTalternative);
    ASTalternative iast = (ASTalternative) a;
    assertXMLEqual("<booleen valeur='true'/>",
                   ((AST)iast.getCondition()).toXML());
    assertXMLEqual("<sequence><entier valeur='1'/></sequence>",
                    ((AST)iast.getConsequent()).toXML());
    assertXMLEqual("<sequence><chaine>foobar</chaine></sequence>",
                    ((AST)iast.getAlternant()).toXML());
  }

  public void testAlternativeBinaire () throws Exception {
    String program = "<alternative>"
      + "<condition><booleen valeur='true'/></condition>"
      + "<consequence><entier valeur='1'/></consequence>"
      + "</alternative>";
    AST a = ASTfromXML.toAST(program);
    String result = a.toXML();
    //assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTalternative);
    ASTalternative iast = (ASTalternative) a;
    assertXMLEqual("<booleen valeur='true'/>",
                    ((AST)iast.getCondition()).toXML());
    assertXMLEqual("<sequence><entier valeur='1'/></sequence>",
                    ((AST)iast.getConsequent()).toXML());
    assertXpathExists("/alternative/consequence", result);
    assertXpathExists("/alternative/consequence/sequence", result);
    assertXpathExists("/alternative/consequence/sequence/entier", result);
    assertXpathNotExists("/alternative/alternant", result);
    assertNull(iast.getAlternant());
  }

  public void testAlternativeWithConsequences () throws ASTException {
    String program = "<alternative>"
      + "<condition><booleen valeur='true'/></condition>"
      + "<consequence><entier valeur='1'/><entier valeur='11'/></consequence>"
      + "</alternative>";
    AST a = ASTfromXML.toAST(program);
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTalternative);
    IAST c = ((ASTalternative) a).getConsequent();
    assertTrue(c instanceof fr.upmc.ilp.ilp1.fromxml.ASTsequence);
    assertEquals(2, ((ASTsequence) c).getInstructions().length);
  }

  public void testAlternativeWithAlternants () throws Exception {
    String program = "<alternative>"
      + "<condition><booleen valeur='true'/></condition>"
      + "<consequence><entier valeur='1'/><entier valeur='11'/></consequence>"
      + "<alternant><chaine>foo</chaine><chaine>bar</chaine></alternant>"
      + "</alternative>";
    AST a = ASTfromXML.toAST(program);
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTalternative);
    IAST c = ((ASTalternative) a).getConsequent();
    assertTrue(c instanceof fr.upmc.ilp.ilp1.fromxml.ASTsequence);
    assertEquals(2, ((ASTsequence) c).getInstructions().length);
    String result = a.toXML();
    assertXpathExists("/alternative", result);
    assertXpathExists("/alternative/consequence", result);
    assertXpathExists("/alternative/consequence/sequence", result);
    assertXpathExists("/alternative/consequence/sequence/entier[@valeur='1']",
                      result);
    assertXpathExists("/alternative/consequence/sequence/entier[@valeur='11']",
                      result);
    assertXpathExists("/alternative/alternant", result);
    assertXpathExists("/alternative/alternant/sequence", result);
    assertXpathEvaluatesTo("2",
                           "count(/alternative/alternant/sequence/chaine)",
                           result);
  }

  // }}}

  public void testSequence () throws Exception {
    String program = "<sequence>"
      + "<booleen valeur='true'/>"
      + "<entier valeur='1'/>"
      + "</sequence>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTsequence);
    IASTsequence iast = (IASTsequence) a;
    IAST[] iastlist = iast.getInstructions();
    assertNotNull(iastlist);
    assertTrue(iastlist.length == 2);
    IAST e1 = iastlist[0];
    assertXMLEqual("<booleen valeur='true'/>", ((AST)e1).toXML());
    IAST e2 = iastlist[1];
    assertXMLEqual("<entier valeur='1'/>", ((AST)e2).toXML());
  }

  public void testSequenceOne () throws Exception {
    String program = "<sequence>"
      + "<entier valeur='1'/>"
      + "</sequence>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTsequence);
    IASTsequence iast = (IASTsequence) a;
    IAST[] iastlist = iast.getInstructions();
    assertNotNull(iastlist);
    assertTrue(iastlist.length == 1);
    IAST e1 = iastlist[0];
    assertXMLEqual("<entier valeur='1'/>", ((AST)e1).toXML());
  }

  public void testSequenceEmpty () throws ASTException {
    String program = "<sequence>"
      + "</sequence>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTsequence);
    IASTsequence iast = (IASTsequence) a;
    IAST[] iastlist = iast.getInstructions();
    assertNotNull(iastlist);
    assertTrue(iastlist.length == 0);
  }

  public void testVariable () throws ASTException {
    String program = "<variable nom='foo'/>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTvariable);
    assertTrue(a instanceof fr.upmc.ilp.ilp1.interfaces.IASTvariable);
    ASTvariable var = (ASTvariable) a;
    assertEquals("foo", var.getName());
  }

  public void testBlocUnaire () throws Exception {
    String program = "<blocUnaire>"
      + "<variable nom='foo'/>"
      + "<valeur><entier valeur='11'/></valeur>"
      + "<corps><variable nom='bar'/></corps>"
      + "</blocUnaire>";
    AST a = ASTfromXML.toAST(program);
    //assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTunaryBlock);
    IASTunaryBlock iast = (ASTunaryBlock) a;
    assertXMLEqual("<variable nom='foo'/>",
                 ((AST)iast.getVariable()).toXML());
    assertXMLEqual("<entier valeur='11'/>",
                 ((AST)iast.getInitialization()).toXML());
    //System.err.println(iast.getBody().toXML());
    assertXMLEqual("<sequence><variable nom='bar'/></sequence>",
                 ((AST)iast.getBody()).toXML());
  }

  public void testInvocationPrimitive2 () throws Exception {
    String program = "<invocationPrimitive fonction='xy'>"
      + "<variable nom='foo'/>"
      + "<entier valeur='11'/>"
      + "</invocationPrimitive>";
    AST a = ASTfromXML.toAST(program);
    //assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTinvocationPrimitive);
    IASTinvocation iast = (ASTinvocation) a;
    assertEquals(2, iast.getArgumentsLength());
    assertEquals("<variable nom='xy'/>",
                 ((AST)iast.getFunction()).toXML());
    IAST[] iastlist = iast.getArguments();
    assertTrue(iastlist.length == 2);
    IAST e1 = iastlist[0];
    assertXMLEqual("<variable nom='foo'/>", ((AST)e1).toXML());
    assertXMLEqual("<variable nom='foo'/>",
                 ((AST)iast.getArgument(0)).toXML());
    IAST e2 = iastlist[1];
    assertXMLEqual("<entier valeur='11'/>", ((AST)e2).toXML());
    assertXMLEqual("<entier valeur='11'/>",
                 ((AST)iast.getArgument(1)).toXML());
  }

  public void testInvocationPrimitive1 () throws Exception {
    String program = "<invocationPrimitive fonction='xy'>"
      + "<entier valeur='11'/>"
      + "</invocationPrimitive>";
    AST a = ASTfromXML.toAST(program);
    //assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTinvocationPrimitive);
    IASTinvocation iast = (ASTinvocation) a;
    assertEquals(1, iast.getArgumentsLength());
    assertEquals("<variable nom='xy'/>",
                 ((AST)iast.getFunction()).toXML());
    IAST[] iastlist = iast.getArguments();
    assertTrue(iastlist.length == 1);
    IAST e1 = iastlist[0];
    assertXMLEqual("<entier valeur='11'/>", ((AST)e1).toXML());
    assertXMLEqual("<entier valeur='11'/>",
                 ((AST)iast.getArgument(0)).toXML());
  }

  public void testInvocationPrimitive0 () throws Exception {
    String program = "<invocationPrimitive fonction='xy'>"
      + "</invocationPrimitive>";
    AST a = ASTfromXML.toAST(program);
    //assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTinvocationPrimitive);
    IASTinvocation iast = (ASTinvocation) a;
    assertEquals(0, iast.getArgumentsLength());
    assertXMLEqual("<variable nom='xy'/>",
                 ((AST)iast.getFunction()).toXML());
    IAST[] iastlist = iast.getArguments();
    assertTrue(iastlist.length == 0);
  }

  public void testOperationUnaire () throws Exception {
    String program = "<operationUnaire operateur='-'>"
      + "<operande><entier valeur='123'/></operande>"
      + "</operationUnaire>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    assertTrue(a instanceof fr.upmc.ilp.ilp1.fromxml.ASTunaryOperation);
    IASTunaryOperation iast = (IASTunaryOperation) a;
    //System.err.println(iast.getOperator().toXML());
    assertEquals("-", iast.getOperatorName());
    assertEquals(1, iast.getArity());
    assertXMLEqual("<entier valeur='123'/>",
                 ((AST)iast.getOperand()).toXML());
    IAST[] iastlist = iast.getOperands();
    assertTrue(iastlist.length == 1);
    IAST e1 = iastlist[0];
    assertXMLEqual("<entier valeur='123'/>", ((AST)e1).toXML());
  }

  public void testOperationBinaire () throws Exception {
    String program = "<operationBinaire operateur='+'>"
      + "<operandeGauche><entier valeur='123'/></operandeGauche>"
      + "<operandeDroit><entier valeur='-34'/></operandeDroit>"
      + "</operationBinaire>";
    AST a = ASTfromXML.toAST(program);
    assertEquals(program, a.toXML());
    IASTbinaryOperation iast = (IASTbinaryOperation) a;
    //System.err.println(iast.getOperator().toXML());
    assertEquals("+", iast.getOperatorName());
    assertEquals(2, iast.getArity());
    assertXMLEqual("<entier valeur='123'/>",
                 ((AST)iast.getLeftOperand()).toXML());
    assertXMLEqual("<entier valeur='-34'/>",
                 ((AST)iast.getRightOperand()).toXML());
    IAST[] iastlist = iast.getOperands();
    assertTrue(iastlist.length == 2);
    AST e1 = (AST) iastlist[0];
    assertXMLEqual("<entier valeur='123'/>", e1.toXML());
    AST e2 = (AST) iastlist[1];
    assertXMLEqual("<entier valeur='-34'/>", e2.toXML());
  }
  
  public void testProgram () throws Exception {
      String program = "<programme1><operationBinaire operateur='+'>"
      + "<operandeGauche><entier valeur='123'/></operandeGauche>"
      + "<operandeDroit><entier valeur='-34'/></operandeDroit>"
      + "</operationBinaire></programme1>";
      AST a = ASTfromXML.toAST(program);
      assertEquals(program, a.toXML());
      IASTprogram prg = (IASTprogram) a;
      ASTsequence seq = (ASTsequence) prg.getBody();
      IASTbinaryOperation iast = (IASTbinaryOperation) seq.getInstruction(0);
      //System.err.println(iast.getOperator().toXML());
      assertEquals("+", iast.getOperatorName());
      assertEquals(2, iast.getArity());
      assertXMLEqual("<entier valeur='123'/>",
                   ((AST)iast.getLeftOperand()).toXML());
      assertXMLEqual("<entier valeur='-34'/>",
                   ((AST)iast.getRightOperand()).toXML());
      IAST[] iastlist = iast.getOperands();
      assertTrue(iastlist.length == 2);
      AST e1 = (AST) iastlist[0];
      assertXMLEqual("<entier valeur='123'/>", e1.toXML());
      AST e2 = (AST) iastlist[1];
      assertXMLEqual("<entier valeur='-34'/>", e2.toXML());
  }
}

// end of ASTParserTest.java
