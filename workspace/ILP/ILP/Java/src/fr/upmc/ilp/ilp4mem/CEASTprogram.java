/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTprogram.java 1299 2013-08-27 07:09:39Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4mem;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IParser;

/** La classe d'un programme composé de fonctions globales et
 * d'instructions. */

public class CEASTprogram extends fr.upmc.ilp.ilp4.ast.CEASTprogram {

    public CEASTprogram (final IAST4functionDefinition[] definitions,
                          final IAST4expression body ) {
        super(definitions, body);
    }

    /** Le constructeur analysant syntaxiquement un DOM. */

    public static IAST4program parse (final Element e, final IParser parser)
    throws CEASTparseException {
        return fr.upmc.ilp.ilp4.ast.CEASTprogram.parse(e, parser);
    }

    //NOTE: Accès direct aux champs interdit à partir d'ici!

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
      
      // Ajouts pour memoryGet et memoryReset:
      buffer.append("struct ILP_Object ILP_object_42 = {\n");
      buffer.append("  ILP_INTEGER_KIND, { 42 } };\n");
      buffer.append("#define ILP_42 (&ILP_object_42)\n");
      
      buffer.append("struct ILP_Object ILP_object_43 = {\n");
      buffer.append("  ILP_INTEGER_KIND, { 43 } };\n");
      buffer.append("#define ILP_43 (&ILP_object_43)\n");
      
      buffer.append("long ILP_bytes_count = 0;\n");
      
      buffer.append("ILP_Object ILP_malloc (int size, enum ILP_Kind kind) {\n");
      buffer.append("  ILP_Object result = malloc(size);\n");
      buffer.append("  if ( result == NULL ) { \n");
      buffer.append("    return ILP_die(\"Memory exhaustion\");\n");
      buffer.append("  };\n");
      buffer.append("  ILP_bytes_count += size;\n");
      buffer.append("  result->_kind = kind;\n");
      buffer.append("  return result;\n");
      buffer.append("}\n");
      
      buffer.append("ILP_Object ILP_memoryGet () {\n");
      buffer.append("  return ILP_Integer2ILP(ILP_bytes_count);\n");
      buffer.append("}\n");
      
      buffer.append("ILP_Object ILP_memoryReset () {\n");
      buffer.append("  ILP_bytes_count = 0;\n");
      buffer.append("  return ILP_FALSE;\n");
      buffer.append("}\n");
      
      // Allocation optimisee d'entiers courants:
      buffer.append("/* NOTA: gcc signalera cette redefinition! */\n");
      buffer.append("#define ILP_Integer2ILP(i) ILP_make_integer_new(i)\n");
      buffer.append("ILP_Object ILP_make_integer_new (int d) {\n");
      buffer.append("  switch (d) {\n");
      buffer.append("    case 42: return ILP_42;\n");
      buffer.append("    case 43: return ILP_43;\n");
      buffer.append("    default: return ILP_make_integer(d);\n");
      buffer.append("  }\n");
      buffer.append("}\n");
      buffer.append("\n");

      // Declarer les variables globales:
      buffer.append("/* Variables ou prototypes globaux: */\n");
      for ( IAST4globalVariable var : getGlobalVariables() ) {
          var.compileGlobalDeclaration(buffer, lexenv, common);
          if ( ! common.isPresent(var.getName()) ) {
            common.bindGlobal(var);
          }
      }
      IAST4functionDefinition[] definitions = getFunctionDefinitions();
      for ( IAST4functionDefinition fun : definitions ) {
          // On pourrait ne pas compiler les fonctions non recursives car 
          // si elles sont integrees, elles ne sont plus invoquees!
          fun.compileHeader(buffer, lexenv, common);
      }
      // Puis le code des fonctions globales:
      buffer.append("\n/* Fonctions globales: */\n");
      for ( IAST4functionDefinition fun : definitions ) {
          fun.compile(buffer, lexenv, common);
      }
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
      buffer.append("\n/* fin */\n");
  }
}

// end of CEASTprogram.java
