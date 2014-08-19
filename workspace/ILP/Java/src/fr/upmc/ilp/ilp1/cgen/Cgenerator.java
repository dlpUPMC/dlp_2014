/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:Cgenerator.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.cgen;

import java.math.BigInteger;

import fr.upmc.ilp.ilp1.interfaces.IAST;
import fr.upmc.ilp.ilp1.interfaces.IASTalternative;
import fr.upmc.ilp.ilp1.interfaces.IASTbinaryOperation;
import fr.upmc.ilp.ilp1.interfaces.IASTboolean;
import fr.upmc.ilp.ilp1.interfaces.IASTconstant;
import fr.upmc.ilp.ilp1.interfaces.IASTfloat;
import fr.upmc.ilp.ilp1.interfaces.IASTinteger;
import fr.upmc.ilp.ilp1.interfaces.IASTinvocation;
import fr.upmc.ilp.ilp1.interfaces.IASToperation;
import fr.upmc.ilp.ilp1.interfaces.IASTprogram;
import fr.upmc.ilp.ilp1.interfaces.IASTsequence;
import fr.upmc.ilp.ilp1.interfaces.IASTstring;
import fr.upmc.ilp.ilp1.interfaces.IASTunaryBlock;
import fr.upmc.ilp.ilp1.interfaces.IASTunaryOperation;
import fr.upmc.ilp.ilp1.interfaces.IASTvariable;

/** La génération vers C. */

public class Cgenerator {

  public Cgenerator (final ICgenEnvironment common) {
    this.common = common;
  }
  protected final ICgenEnvironment common;

  /** Convertir un AST en une chaîne de caractères C. */

  public String compile (final IAST iast,
                         final ICgenLexicalEnvironment lexenv,
                         final String destination)
    throws CgenerationException {
    this.buffer = new StringBuffer(1024);
    buffer.append("/* Fichier compilé vers C */\n");
    analyze(iast, lexenv, this.common, destination);
    final String result = this.buffer.toString();
    return result;
  }
  protected transient StringBuffer buffer;

  /** Convertir une expression en C. */

  protected void analyzeExpression (final IAST iast,
                                     final ICgenLexicalEnvironment lexenv,
                                     final ICgenEnvironment common)
    throws CgenerationException {
    analyze(iast, lexenv, common, "");
  }

  /** Convertir une instruction en C.
   *
   * La destination est soit "" (ou "(void)" pour indiquer que la
   * valeur obtenue est inutile, soit "return" pour indiquer que c'est
   * la valeur finale d'une fonction ou encore "variable =" pour
   * ranger la valeur obtenue dans une variable.
   */

  protected void analyzeInstruction (final IAST iast,
                                      final ICgenLexicalEnvironment lexenv,
                                      final ICgenEnvironment common,
                                      final String destination)
    throws CgenerationException {
    analyze(iast, lexenv, common, destination);
  }

  /** Cette méthode analyse la nature de l'AST à traiter et détermine
   * la bonne méthode à appliquer. C'est un envoi de message simulé à
   * la main, l'ordre de discrimination n'est pas le plus efficace,
   * mais utilise les marqueurs d'interface pour regrouper certains
   * tests. Le code spécifique aux divers cas se trouve dans les
   * méthodes generate() surchargées.
   */

  protected void analyze (final IAST iast,
                            final ICgenLexicalEnvironment lexenv,
                            final ICgenEnvironment common,
                            final String destination)
    throws CgenerationException {
    if ( iast instanceof IASTconstant ) {
      if ( iast instanceof IASTboolean ) {
        generate((IASTboolean) iast, lexenv, common, destination);
      } else if ( iast instanceof IASTfloat ) {
        generate((IASTfloat) iast, lexenv, common, destination);
      } else if ( iast instanceof IASTinteger ) {
        generate((IASTinteger) iast, lexenv, common, destination);
      } else if ( iast instanceof IASTstring ) {
        generate((IASTstring) iast, lexenv, common, destination);
      } else {
        final String msg = "Unknown type of constant: " + iast;
        throw new CgenerationException(msg);
      }
    } else if ( iast instanceof IASTalternative ) {
      generate((IASTalternative) iast, lexenv, common, destination);
    } else if ( iast instanceof IASTinvocation ) {
      generate((IASTinvocation) iast, lexenv, common, destination);
    } else if ( iast instanceof IASToperation ) {
      if ( iast instanceof IASTunaryOperation ) {
        generate((IASTunaryOperation) iast, lexenv, common, destination);
      } else if ( iast instanceof IASTbinaryOperation ) {
        generate((IASTbinaryOperation) iast, lexenv, common, destination);
      } else {
        final String msg = "Unknown type of operation: " + iast;
        throw new CgenerationException(msg);
    }
    } else if ( iast instanceof IASTsequence ) {
      generate((IASTsequence) iast, lexenv, common, destination);
    } else if ( iast instanceof IASTunaryBlock ) {
      generate((IASTunaryBlock) iast, lexenv, common, destination);
    } else if ( iast instanceof IASTvariable ) {
      generate((IASTvariable) iast, lexenv, common, destination);
    } else if ( iast instanceof IASTprogram ) {
        generate((IASTprogram) iast, lexenv, common);
    } else {
      final String msg = "Unknown type of AST: " + iast;
      throw new CgenerationException(msg);
    }
  }

  /** Toutes les méthodes qui suivent remplissent le tampon courant
   * (buffer). Il y a une méthode generate par catégorie
   * syntaxique. Attention, la methode pour un programme ne prend
   * pas de destination. */
  
  protected void generate (final IASTprogram iast,
                             final ICgenLexicalEnvironment lexenv,
                             final ICgenEnvironment common )
      throws CgenerationException {
      buffer.append("#include <stdio.h>\n");
      buffer.append("#include <stdlib.h>\n");
      buffer.append("\n");
      buffer.append("#include \"ilp.h\"\n");
      buffer.append("\n");
      buffer.append("ILP_Object ilp_program ()\n");
      analyzeInstruction(iast.getBody(), lexenv, common, "return");
      buffer.append("\n");
      buffer.append("int main (int argc, char *argv[]) {\n");
      buffer.append("  ILP_print(ilp_program());\n");
      buffer.append("  ILP_newline();\n");
      buffer.append("  return EXIT_SUCCESS;\n");
      buffer.append("}\n");
      buffer.append("/* fin */\n");
  }

  protected void generate (final IASTalternative iast,
                            final ICgenLexicalEnvironment lexenv,
                            final ICgenEnvironment common,
                            final String destination)
    throws CgenerationException {
    buffer.append(" if ( ILP_isEquivalentToTrue( ");
    analyzeExpression(iast.getCondition(), lexenv, common);
    buffer.append(" ) ) {\n");
    analyzeInstruction(iast.getConsequent(), lexenv, common, destination);
    buffer.append(";\n}");
    if ( iast.isTernary() ) {
      buffer.append(" else {\n");
      try {
        analyzeInstruction(iast.getAlternant(), lexenv, common, destination);
        buffer.append(";");
      } catch (Exception e) {
          final String msg = "Should never occur!";
          assert false : msg;
          throw new CgenerationException(msg);
      }
      buffer.append("\n}");
    } else {
      buffer.append(" else {\n");
      buffer.append(destination);
      buffer.append(" ILP_FALSE;");
      buffer.append("\n}");
    }
  }

  protected void generate (final IASTbinaryOperation iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    buffer.append(destination);
    buffer.append(" ");
    buffer.append(common.compileOperator2(iast.getOperatorName()));
    buffer.append("(");
    analyzeExpression(iast.getLeftOperand(), lexenv, common);
    buffer.append(", ");
    analyzeExpression(iast.getRightOperand(), lexenv, common);
    buffer.append(") ");
  }

  protected void generate (final IASTboolean iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    buffer.append(destination);
    if ( iast.getValue() ) {
      buffer.append(" ILP_TRUE ");
    } else {
      buffer.append(" ILP_FALSE ");
    }
  }

  protected void generate (final IASTfloat iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    buffer.append(destination);
    buffer.append(" ILP_Float2ILP(");
    buffer.append(iast.getValue());
    buffer.append(") ");
  }

  protected void generate (final IASTinteger iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    final BigInteger bi = iast.getValue();
    // && et non || comme l'a remarqué Nicolas.Bros@gmail.com
    if (    bi.compareTo(BIMIN) > 0
         && bi.compareTo(BIMAX) < 0 ) {
      buffer.append(destination);
      buffer.append(" ILP_Integer2ILP(");
      buffer.append(bi.intValue());
      buffer.append(") ");
    } else {
      final String msg = "Too large integer " + bi;
      throw new CgenerationException(msg);
    }
  }
  public static final BigInteger BIMIN;
  public static final BigInteger BIMAX;
  static {
    final Integer i = Integer.MIN_VALUE;
    BIMIN = new BigInteger(i.toString());
    final Integer j = Integer.MAX_VALUE;
    BIMAX = new BigInteger(j.toString());
  }

  protected void generate (final IASTinvocation iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    buffer.append(destination);
    buffer.append(" ");
    analyzeExpression(iast.getFunction(), lexenv, common);
    buffer.append("(");
    final int numberOfArguments = iast.getArgumentsLength();
    for ( int i=0 ; i<numberOfArguments ; i++ ) {
        IAST iast2 = iast.getArgument(i);
        analyzeExpression(iast2, lexenv, common);
        if ( i<numberOfArguments-1 ) {
            buffer.append(", ");
        }
    }
    buffer.append(")");
  }

  protected void generate (final IASTsequence iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    buffer.append("{\n");
    final IAST[] instrs = iast.getInstructions();
    for ( int i = 0 ; i < instrs.length-1 ; i ++) {
        final IAST iast2 = instrs[i];
        analyzeInstruction(iast2, lexenv, common, "(void)");
        buffer.append(";\n");
    }
    analyzeInstruction(instrs[instrs.length-1], lexenv, common, destination);
    buffer.append(";\n}\n");
  }

  protected void generate (final IASTstring iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    buffer.append(destination);
    buffer.append(" ILP_String2ILP(\"");
    final String s = iast.getValue();
    final int n = s.length();
    for ( int i = 0 ; i<n ; i++ ) {
      char c = s.charAt(i);
      switch ( c ) {
      case '\\':
      case '"': {
        buffer.append("\\");
      }
    //$FALL-THROUGH$
    default: {
        buffer.append(c);
      }
      }
    }
    buffer.append("\") ");
  }

  protected void generate (final IASTunaryBlock iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    final IASTvariable tmp = common.generateVariable();
    final ICgenLexicalEnvironment lexenv2 =
      lexenv.extend(tmp, tmp.getName());
    final ICgenLexicalEnvironment lexenv3 =
      lexenv2.extend(iast.getVariable());

    buffer.append("{\n");
    buffer.append(" ILP_Object ");
    analyzeExpression(tmp, lexenv2, common);
    buffer.append(" = ");
    analyzeExpression(iast.getInitialization(), lexenv, common);
    buffer.append(";\n");

    buffer.append(" ILP_Object ");
    analyzeExpression(iast.getVariable(), lexenv3, common);
    buffer.append(" = ");
    analyzeExpression(tmp, lexenv2, common);
    buffer.append(";\n");

    analyzeInstruction(iast.getBody(), lexenv3, common, destination);
    buffer.append("}\n");
  }

  protected void generate (final IASTunaryOperation iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    buffer.append(destination);
    buffer.append(" ");
    buffer.append(common.compileOperator1(iast.getOperatorName()));
    buffer.append("(");
    analyzeExpression(iast.getOperand(), lexenv, common);
    buffer.append(") ");
  }

  protected void generate (final IASTvariable iast,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common,
                           final String destination)
    throws CgenerationException {
    buffer.append(destination);
    buffer.append(" ");
    buffer.append(lexenv.compile(iast));
    buffer.append(" ");
  }

}

// end of Cgenerator.java
