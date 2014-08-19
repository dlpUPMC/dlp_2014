/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTtry.java 1247 2012-09-19 14:24:59Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp3;

import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTinstruction;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.cgen.VoidDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp3.IParser;

public class CEASTtry extends CEASTinstruction
implements IAST3try<CEASTparseException> {

  public CEASTtry (final IAST2instruction<CEASTparseException> body,
                   final IAST2variable caughtExceptionVariable,
                   final IAST2instruction<CEASTparseException> catcher,
                   final IAST2instruction<CEASTparseException> finallyer) {
    this.body = body;
    // S'il n'y a pas de rattrapeur, il n'y a pas de variable caughtException:
    this.caughtExceptionVariable = caughtExceptionVariable;
    // et le rattrapeur sera null:
    this.catcher = catcher;
    // Il y a toujours un finaliseur (eventuellement vide):
    this.finallyer = finallyer;
  }
  private IAST2instruction<CEASTparseException> body;
  private @OrNull IAST2variable    caughtExceptionVariable;
  private @OrNull IAST2instruction<CEASTparseException> catcher;
  private @OrNull IAST2instruction<CEASTparseException> finallyer;

  // Les accesseurs

  public IAST2instruction<CEASTparseException> getBody () {
      return this.body;
  }
  public @OrNull IAST2variable getCaughtExceptionVariable () {
      return this.caughtExceptionVariable;
  }
  public @OrNull IAST2instruction<CEASTparseException> getCatcher () {
      return this.catcher;
  }
  public @OrNull IAST2instruction<CEASTparseException> getFinallyer () {
      return this.finallyer;
  }

  public static IAST3try<CEASTparseException> parse (
          final Element e, final IParser<CEASTparseException> parser)
  throws CEASTparseException {
      try {
          final XPathExpression bodyPath = xPath.compile("./corps/*");
          final NodeList nlBody = (NodeList)
              bodyPath.evaluate(e, XPathConstants.NODESET);
          IAST2instruction<CEASTparseException> body =
              (IAST2instruction<CEASTparseException>)
              parser.parseChildrenAsSequence(nlBody);
          IAST2variable caughtExceptionVariable = null;
          IAST2instruction<CEASTparseException> catcher = null;
          final XPathExpression catchPath = xPath.compile("./catch");
          final Element nCatch = (Element)
              catchPath.evaluate(e, XPathConstants.NODE);
          if ( null != nCatch ) {
              caughtExceptionVariable =
                  parser.getFactory()
                  .newVariable(nCatch.getAttribute("exception"));
              catcher = (IAST2instruction<CEASTparseException>)
                  parser.parseChildrenAsSequence(nCatch.getChildNodes());
          }
          final XPathExpression finallyPath = xPath.compile("./finally");
          final Element nlFinally = (Element)
              finallyPath.evaluate(e, XPathConstants.NODE);
          IAST2instruction<CEASTparseException> finallyer = null;
          if ( null != nlFinally ) {
              finallyer = (IAST2instruction<CEASTparseException>)
                  parser.parseChildrenAsSequence(nlFinally.getChildNodes());
          }
          IAST3Factory<CEASTparseException> factory = parser.getFactory();
          return factory.newTry(body, caughtExceptionVariable,
                                catcher, finallyer);
      } catch (Exception e1) {
          throw new CEASTparseException(e1);
      }
  }
  private static final XPath xPath = XPathFactory.newInstance().newXPath();

  //NOTE: Accès direct aux champs interdit à partir d'ici!

  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
        throws EvaluationException {
      Object result = Boolean.FALSE;
      try {
          // Bizarrement, je n'ai pu écrire return ci-dessous et ai dû
          // passer par la variable result !?
          result = getBody().eval(lexenv, common);
      } catch (ThrownException e) {
          if ( getCatcher() != null ) {
              final ILexicalEnvironment catcherLexenv =
                  lexenv.extend(getCaughtExceptionVariable(), e.getThrownValue());
              getCatcher().eval(catcherLexenv, common);
          } else {
              throw e;
          }
      } catch (EvaluationException e) {
          if ( getCatcher() != null ) {
              final ILexicalEnvironment catcherLexenv =
                  lexenv.extend(getCaughtExceptionVariable(), e);
              // VARIANTE: On pourrait vouloir prefixer par "result = "
              getCatcher().eval(catcherLexenv, common);
          } else {
              throw e;
          }
      } catch (RuntimeException e) {
          if ( getCatcher() != null ) {
              final ILexicalEnvironment catcherLexenv =
                  lexenv.extend(getCaughtExceptionVariable(), e);
              getCatcher().eval(catcherLexenv, common);
          } else {
              throw e;
          }
      } finally {
          if ( getFinallyer() != null ) {
              getFinallyer().eval(lexenv, common);
          }
      }
      return result;
  }

  public void compileInstruction (final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final ICgenEnvironment common,
                                   final IDestination destination)
    throws CgenerationException {
    buffer.append("{ struct ILP_catcher* current_catcher = ILP_current_catcher; \n");
    buffer.append("  struct ILP_catcher new_catcher;  \n");
    buffer.append("  if ( 0 == setjmp(new_catcher._jmp_buf) ) { \n");
    buffer.append("      ILP_establish_catcher(&new_catcher); \n");
    getBody().compileInstruction(buffer, lexenv, common, destination);
    buffer.append("      ILP_current_exception = NULL; \n");
    buffer.append("  }; \n");

    if ( getCatcher() != null ) {
      final ICgenLexicalEnvironment catcherLexenv =
        lexenv.extend(getCaughtExceptionVariable());
      buffer.append("  ILP_reset_catcher(current_catcher); \n");
      buffer.append("  if ( NULL != ILP_current_exception ) { \n");
      buffer.append("      if ( 0 == setjmp(new_catcher._jmp_buf) ) { \n");
      buffer.append("          ILP_establish_catcher(&new_catcher); \n");
      buffer.append("          { ILP_Object ");
      buffer.append(getCaughtExceptionVariable().getMangledName());
      buffer.append(" = ILP_current_exception; \n");
      buffer.append("            ILP_current_exception = NULL; \n");
      getCatcher().compileInstruction(
              buffer, catcherLexenv, common, VoidDestination.create());
      buffer.append("          } \n");
      buffer.append("      }; \n");
      buffer.append("  }; \n");
    }

    buffer.append("  ILP_reset_catcher(current_catcher); \n");
    if ( getFinallyer() != null ) {
        getFinallyer().compileInstruction(
                buffer, lexenv, common, VoidDestination.create());
    }
    buffer.append("  if ( NULL != ILP_current_exception ) { \n");
    buffer.append("      ILP_throw(ILP_current_exception); \n");
    buffer.append("  }; \n");
    CEASTinstruction.voidInstruction().compileInstruction(
            buffer, lexenv, common, destination);
    buffer.append("}\n");
  }

  @Override
  public void findGlobalVariables (
              final Set<IAST2variable> globalvars,
              final ICgenLexicalEnvironment lexenv ) {
    getBody().findGlobalVariables(globalvars, lexenv);
    if ( getCatcher() != null ) {
      final ICgenLexicalEnvironment catcherLexenv =
        lexenv.extend(getCaughtExceptionVariable());
      getCatcher().findGlobalVariables(globalvars, catcherLexenv);
    }
    if ( getFinallyer() != null ) {
        getFinallyer().findGlobalVariables(globalvars, lexenv);
    }
  }
}

// end of CEASTtry.java
