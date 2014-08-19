/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTprogram.java 1299 2013-08-27 07:09:39Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp3;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTfunctionDefinition;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.ast.CEASTvariable;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2program;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IParser;

/** La classe d'un programme composé de fonctions globales et
 * d'instructions. */

public class CEASTprogram
extends fr.upmc.ilp.ilp2.ast.CEASTprogram {

    public CEASTprogram (
            final IAST2functionDefinition<CEASTparseException>[] definitions,
            final IAST2instruction<CEASTparseException> body ) {
        super(definitions, body);
    }

    /** Le constructeur analysant syntaxiquement un DOM. */

    public static IAST2program<CEASTparseException> parse (
            final Element e, final IParser<CEASTparseException> parser)
    throws CEASTparseException {
        return fr.upmc.ilp.ilp2.ast.CEASTprogram.parse(e, parser);
    }

    //NOTE: Accès direct aux champs interdit à partir d'ici!

  @Override
  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
      IAST2functionDefinition<CEASTparseException>[] definitions =
          getFunctionDefinitions();
      for ( int i = 0 ; i<definitions.length ; i++ ) {
          definitions[i].eval(lexenv, common);
      }
      Object result = null;
      try {
          result = getBody().eval(lexenv, common);
      } catch ( ThrownException exc ) {
          // Deballer la valeur si c'est une valeur obtenue par
          // echappement. Cela revient a admettre qu'un programme P est
          // equivalent a try { P } catch (e) { e };
          result = exc.getThrownValue();
      }
      return result;
  }
  
  @Override
  public void compile (final StringBuffer buffer,
                        final ICgenLexicalEnvironment lexenv,
                        final ICgenEnvironment common )
  throws CgenerationException {
      buffer.append("#include <stdio.h>\n");
      buffer.append("#include <stdlib.h>\n");
      buffer.append("\n");
      buffer.append("#include \"ilp.h\"\n");
      buffer.append("#include \"ilpException.h\"\n");
      buffer.append("\n");
      final IAST2functionDefinition<CEASTparseException>[] definitions =
              getFunctionDefinitions();
      // Declarer les variables globales:
      buffer.append("/* Variables globales: */\n");
      for ( IAST2variable var : getGlobalVariables() ) {
          buffer.append("static ILP_Object ");
          buffer.append(var.getMangledName());
          buffer.append(" = NULL;\n");
          if ( ! common.isPresent(var.getName()) ) {
              common.bindGlobal(var);
          }
      }
      // Emettre le code des fonctions:
      buffer.append("/* Prototypes: */\n");
      for ( IAST2functionDefinition<CEASTparseException> fun : definitions ) {
          fun.compileHeader(buffer, lexenv, common);
      }
      buffer.append("/* Fonctions globales: */\n");
      for ( IAST2functionDefinition<CEASTparseException> fun : definitions ) {
          common.bindGlobal(new CEASTvariable(fun.getFunctionName()));
      }
      for ( IAST2functionDefinition<CEASTparseException> fun : definitions ) {
          fun.compile(buffer, lexenv, common);
      }
      // Emettre les instructions regroupees dans une fonction:
      buffer.append("/* Code hors fonction: */\n");
      IAST2functionDefinition<CEASTparseException> bodyAsFunction =
              new CEASTfunctionDefinition(
                      "ilp_program",
                      CEASTvariable.EMPTY_VARIABLE_ARRAY,
                      getBody() );
      //INUTILE: bodyAsFunction.compile_header(buffer, lexenv, common);
      bodyAsFunction.compile(buffer, lexenv, common);
      buffer.append("\n");
      buffer.append("static ILP_Object ilp_caught_program () {\n");
      buffer.append("  struct ILP_catcher* current_catcher = ILP_current_catcher;\n");
      buffer.append("  struct ILP_catcher new_catcher;\n");
      buffer.append("\n");
      buffer.append("  if ( 0 == setjmp(new_catcher._jmp_buf) ) {\n");
      buffer.append("    ILP_establish_catcher(&new_catcher);\n");
      buffer.append("    return ilp_program();\n");
      buffer.append("  };\n");
      buffer.append("  /* Une exception est survenue. */\n");
      buffer.append("  return ILP_current_exception;\n");
      buffer.append("}\n");
      buffer.append("\n");
      buffer.append("int main (int argc, char *argv[]) {\n");
      buffer.append("  ILP_print(ilp_caught_program());\n");
      buffer.append("  ILP_newline();\n");
      buffer.append("  return EXIT_SUCCESS;\n");
      buffer.append("}\n\n");
      buffer.append("/* fin */\n");
  }
}

// end of CEASTprogram.java
