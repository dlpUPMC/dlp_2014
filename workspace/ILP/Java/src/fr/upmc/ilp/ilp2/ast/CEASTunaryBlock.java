/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTunaryBlock.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.Set;

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
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryBlock;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** Le bloc unaire: interpretation et compilation.
 *
 * Il n'herite pas du bloc local car il n'y a aucun code en commun. On peut
 * toujours remplacer les blocs unaires par des blocs locaux n-aires: c'est
 * a l'analyseur syntaxique de le faire. J'ai maintenu les blocs unaires
 * pour pouvoir continuer a utiliser les tests qui en contiennent.
 *
 * On pourrait faire une nouvelle implantation transformant les blocs unaires
 * en blocs n-aires.
 */

public class CEASTunaryBlock
extends CEASTinstruction
implements IAST2unaryBlock<CEASTparseException> {

  public CEASTunaryBlock (final IAST2variable variable,
                          final IAST2expression<CEASTparseException> initialization,
                          final IAST2instruction<CEASTparseException> body)
  {
    this.variable = variable;
    this.initialization = initialization;
    this.body = body;
  }
  private final IAST2variable variable;
  private final IAST2expression<CEASTparseException> initialization;
  private final IAST2instruction<CEASTparseException> body;

  public IAST2variable getVariable () {
    return this.variable;
  }
  public IAST2expression<CEASTparseException> getInitialization () {
    return this.initialization;
  }
  public IAST2instruction<CEASTparseException> getBody () {
    return this.body;
  }

  public static IAST2unaryBlock<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      final NodeList nl = e.getChildNodes();
      IAST2variable variable;
      try {
          final XPathExpression varPath = xPath.compile("./variable");
          final Element nVar = (Element) varPath.evaluate(e, XPathConstants.NODE);
          variable = parser.getFactory().newVariable(nVar.getAttribute("nom"));
      } catch (XPathExpressionException e1) {
          throw new CEASTparseException(e1);
      }
      IAST2expression<CEASTparseException> initialization = (IAST2expression<CEASTparseException>)
          parser.findThenParseChildAsUnique(nl, "valeur");
      IAST2instruction<CEASTparseException> body = (IAST2instruction<CEASTparseException>)
          parser.findThenParseChildAsSequence(nl, "corps");
      return parser.getFactory()
          .newUnaryBlock(variable, initialization, body);
  }
  private static final XPath xPath = XPathFactory.newInstance().newXPath();

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
    ILexicalEnvironment newlexenv =
      lexenv.extend(getVariable(), getInitialization().eval(lexenv, common));
    return getBody().eval(newlexenv, common);
  }

  public void compileInstruction (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common,
                                  final IDestination destination)
    throws CgenerationException {
    final IAST2variable tmp = CEASTvariable.generateVariable();
    final ICgenLexicalEnvironment lexenv2 = lexenv.extend(tmp);
    final ICgenLexicalEnvironment lexenv3 = lexenv2.extend(getVariable());

    buffer.append("{\n");
    tmp.compileDeclaration(buffer, lexenv2, common);
    getInitialization().compileInstruction(
            buffer, lexenv2, common, new AssignDestination(tmp) );

    buffer.append("{\n");
    getVariable().compileDeclaration(buffer, lexenv3, common);
    buffer.append(lexenv3.compile(getVariable()));
    buffer.append(" = ");
    buffer.append(lexenv2.compile(tmp));
    buffer.append(";\n");
    getBody().compileInstruction(buffer, lexenv3, common, destination);
    buffer.append("}\n}\n");
  }

  @Override
  public void
    findGlobalVariables (final Set<IAST2variable> globalvars,
                       final ICgenLexicalEnvironment lexenv ) {
    getInitialization().findGlobalVariables(globalvars, lexenv);
    final ICgenLexicalEnvironment bodylexenv = lexenv.extend(getVariable());
    getBody().findGlobalVariables(globalvars, bodylexenv);
  }

}

// end of CEASTunaryBlock.java
