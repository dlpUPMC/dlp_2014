package fr.upmc.ilp.ilp3.jsgen;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTboolean;
import fr.upmc.ilp.ilp2.ast.CEASTfunctionDefinition;
import fr.upmc.ilp.ilp2.cgen.ReturnDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp2.interfaces.IAST2alternative;
import fr.upmc.ilp.ilp2.interfaces.IAST2assignment;
import fr.upmc.ilp.ilp2.interfaces.IAST2binaryOperation;
import fr.upmc.ilp.ilp2.interfaces.IAST2boolean;
import fr.upmc.ilp.ilp2.interfaces.IAST2constant;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2float;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2integer;
import fr.upmc.ilp.ilp2.interfaces.IAST2invocation;
import fr.upmc.ilp.ilp2.interfaces.IAST2localBlock;
import fr.upmc.ilp.ilp2.interfaces.IAST2operation;
import fr.upmc.ilp.ilp2.interfaces.IAST2primitiveInvocation;
import fr.upmc.ilp.ilp2.interfaces.IAST2program;
import fr.upmc.ilp.ilp2.interfaces.IAST2reference;
import fr.upmc.ilp.ilp2.interfaces.IAST2sequence;
import fr.upmc.ilp.ilp2.interfaces.IAST2string;
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryBlock;
import fr.upmc.ilp.ilp2.interfaces.IAST2unaryOperation;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.IAST2while;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp3.IAST3try;
import fr.upmc.ilp.tool.CStuff;

/** La génération vers C. */

public class Compiler {

  public Compiler (final ICgenEnvironment common) {
    this.common = common;
  }
  
  public static class Data {
      
      public StringBuffer buffer;
      public ICgenLexicalEnvironment lexenv;
      public ICgenEnvironment common;
      public IDestination destination;
      
      public Data(final StringBuffer buffer,
                  final ICgenLexicalEnvironment lexenv,
                  final ICgenEnvironment common,
                  final IDestination destination) {
          this.buffer = buffer;
          this.lexenv = lexenv;
          this.common = common;
          this.destination = destination;
      }
  }
  
  public void introduceVariable (final IAST2variable var,
          final StringBuffer buffer,
          final ICgenLexicalEnvironment lexenv,
          final ICgenEnvironment common ) {
	  buffer.append("var ");
	  buffer.append(var.getMangledName());
	  buffer.append(";\n");
  }
  
  public void introduceVariable (final IAST3localVariable var,
          final StringBuffer buffer,
          final ICgenLexicalEnvironment lexenv,
          final ICgenEnvironment common ) {
	  buffer.append("var ");
	  buffer.append(var.getMangledName());
	  buffer.append(";\n");
  }
  
  public void introduceVariable (final IAST3globalVariable var,
          final StringBuffer buffer,
          final ICgenLexicalEnvironment lexenv,
          final ICgenEnvironment common ) {
	  buffer.append("var ");
	  buffer.append(var.getMangledName());
	  buffer.append(";\n");
  }
  
  protected final ICgenEnvironment common;
  protected transient StringBuffer buffer;

  /** Le nom de la fonction Javascript correspondant au programme. */
  public static String PROGRAM = "ilp_program";

  

/** Cette méthode analyse la nature de l'AST à traiter et détermine
* la bonne méthode à appliquer. C'est un envoi de message simulé à
* la main, l'ordre de discrimination n'est pas le plus efficace,
* mais utilise les marqueurs d'interface pour regrouper certains
* tests. Le code spécifique aux divers cas se trouve dans les
* méthodes generate() surchargées.
*/

protected void analyze (final IAST2<CgenerationException> iast, Data data)
throws CgenerationException {
if ( iast instanceof IAST2constant ) {
if ( iast instanceof IAST2boolean ) {
	visit ((IAST2boolean<CgenerationException>) iast, data);
	} else if ( iast instanceof IAST2float ) {
	visit ((IAST2float<CgenerationException>) iast, data);
	} else if ( iast instanceof IAST2integer ) {
	visit ((IAST2integer<CgenerationException>) iast, data);
	} else if ( iast instanceof IAST2string ) {
	visit ((IAST2string<CgenerationException>) iast, data);
} else {
final String msg = "Unknown type of constant: " + iast;
throw new CgenerationException(msg);
}
} else if ( iast instanceof IAST2alternative ) {
visit ((IAST2alternative<CgenerationException>) iast, data);
} else if ( iast instanceof IAST2primitiveInvocation ) {
	visit ((IAST2primitiveInvocation<CgenerationException>) iast, data);
} else if ( iast instanceof IAST2invocation ) {
visit ((IAST2invocation<CgenerationException>) iast, data);
}  else if ( iast instanceof IAST2operation ) {
if ( iast instanceof IAST2unaryOperation ) {
visit((IAST2unaryOperation<CgenerationException>) iast, data);
} else if ( iast instanceof IAST2binaryOperation ) {
visit((IAST2binaryOperation<CgenerationException>) iast, data);
} else {
final String msg = "Unknown type of operation: " + iast;
throw new CgenerationException(msg);
}
} else if ( iast instanceof IAST2sequence ) {
visit ((IAST2sequence<CgenerationException>) iast, data);
} else if ( iast instanceof IAST2unaryBlock ) {
visit ((IAST2unaryBlock<CgenerationException>) iast, data);
} else if ( iast instanceof IAST2variable ) {
visit ((IAST2variable) iast, data);
} else if ( iast instanceof IAST2program ) {
visit ((IAST2program<CgenerationException>) iast, data);
} else if ( iast instanceof IAST3try ) {
	visit ((IAST3try<CgenerationException>) iast, data);
} else if ( iast instanceof IAST2while ) {
	visit ((IAST2while<CgenerationException>) iast, data);
} else if ( iast instanceof IAST2reference ) {
	visit ((IAST2reference<CgenerationException>) iast, data);
}  else if ( iast instanceof IAST2assignment ) {
	visit ((IAST2assignment<CgenerationException>) iast, data);
} else if ( iast instanceof IAST2localBlock ) {
	visit ((IAST2localBlock<CgenerationException>) iast, data);
}

else {
final String msg = "Unknown type of AST: " + iast;
throw new CgenerationException(msg);
}
}
 
  /*
Pas de destination pour la compilation d'un programme:

                  ------------------------->
                  program(definitions, code)
  
  --------->
  definition
  ... // pour chaque definition de fonction
  
  function ilp_program () {
     ----> return  
     code
  }
  
  ilp_program();
  
   */
  
  public Object visit(IAST2program iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      
      // le code des fonctions globales:
      buffer.append("\n/* Fonctions globales: */\n");
      IAST2functionDefinition[] definitions = iast.getFunctionDefinitions();
      for ( IAST2functionDefinition fun : definitions ) {
    	  visit (fun, data);
      }
      // Emettre les instructions regroupees dans une fonction:
      buffer.append("\n/* Code hors fonction: */\n");
      IAST2functionDefinition bodyAsFunction =
          new CEASTfunctionDefinition(
        		  PROGRAM,
                  new IAST2variable[0],
                  iast.getBody() );
      visit (bodyAsFunction, data);
      buffer.append("\n/* fin */\n");
      
      return null;
  }

  
  /*
                      -------------------------------------------> d
                      blocLocal(variables, initialisations, corps)

Les accolades environnantes sont inutiles puisqu'aucune port��e n'est
associ��e aux blocs locaux. Les accolades enserrant le corps des fonctions
sont donc suffisantes.

 var tmpI; ...
 
 --------------> tmpI
 initialisationI
 ... // pour chaque initialisation
 
 var variableI = tmpI;
 ... // pour chaque variable locale
 
 -----> d
 corps

 
   */
  
  public Object visit(IAST2localBlock iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      final IDestination destination = data.destination;

      final IAST2variable[] vars = iast.getVariables();
      final IAST2expression[] inits = iast.getInitializations();
      IAST3localVariable[] temp = new IAST3localVariable[vars.length];
      ICgenLexicalEnvironment templexenv = lexenv;

      for (int i = 0; i < vars.length; i++) {
          temp[i] = CEASTlocalVariable.generateVariable();
          templexenv = templexenv.extend(temp[i]);
          introduceVariable(temp[i], buffer, templexenv, common);
      }

      for (int i = 0; i < vars.length; i++) {
    	  analyze ((IAST2) inits[i], 
              new Data(buffer, lexenv, common,
                  new AssignDestination(temp[i]) ));
          buffer.append(";\n");
      }

      ICgenLexicalEnvironment bodylexenv = templexenv;
      for (int i = 0; i < vars.length; i++) {
          bodylexenv = bodylexenv.extend(vars[i]);
          introduceVariable(vars[i], buffer, bodylexenv, common);
      }

      for (int i = 0; i < vars.length; i++) {
          buffer.append(vars[i].getMangledName());
          buffer.append(" = ");
          buffer.append(temp[i].getMangledName());
          buffer.append(";\n");
      }

      analyze ((IAST2) iast.getBody(), 
              new Data(buffer, bodylexenv, common, destination));
      buffer.append(";\n");
      
      return null;
  }

  /*                        
                            -------------------------------------->
                            definitionFonction(nom variables corps)
                            
Pas de destination pour la compilation d'une fonction.                              

 function nom (variable, ...) {
    -----> return 
    corps
 }
  
   */
  
  public Object visit(IAST2functionDefinition iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;

 
      // ��mettre la d��finition de la fonction:
      buffer.append("\nfunction ");
      buffer.append(iast.getMangledFunctionName());
      compileVariableList(iast, buffer);
      buffer.append("\n{\n");
      // iast.extendWithFunctionVariables(lexenv);
      ICgenLexicalEnvironment bodyLexenv = lexenv;
      final IAST2variable[] variables = iast.getVariables();
      for ( int i = 0 ; i<variables.length ; i++ ) {
        bodyLexenv = bodyLexenv.extend(variables[i]);
      }
      analyze ((IAST2) iast.getBody(),
          new Data(buffer, bodyLexenv, common, ReturnDestination.create()));
      buffer.append(";\n}\n");
      
      return null;
  }
  

  public void compileVariableList (final IAST2functionDefinition iast, 
                                   final StringBuffer buffer) {
      buffer.append(" (");
      final IAST2variable[] variables =  iast.getVariables();
      for ( int i = 0 ; i<variables.length-1 ; i++ ) {
        buffer.append(variables[i].getMangledName());
        buffer.append(",\n");
      }
      if ( variables.length > 0 ) {
        buffer.append(variables[variables.length-1].getMangledName());
      }
      buffer.append(" ) ");
    }
    
 
  
  /*
                     ----------------> d
                     constante(valeur)
                     
d valeur;     // pour entier, flottant
d true;       // pour les bool��ens
d false;                       
d "chaine";   // pour les cha��nes de caract��res

   */

  public Object visit(IAST2constant iast, Data data) 
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      final IDestination destination = data.destination;
      
      destination.compile(buffer, lexenv, common);
      if ( iast.getValue() instanceof String ) {
          buffer.append("\"");
          buffer.append(CStuff.protect(iast.getValue().toString()));
          buffer.append("\"");
      } else {
          buffer.append(iast.getValue().toString());
      }
      
      return null;
  }
  
  // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u06
  
  /*
                           ------------------------------------> d
                           operationUnaire(operateur, operande)

var tmp1;

--------> tmp1
operande;

d (operateur tmp1);

   */

  public Object visit(IAST2unaryOperation iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      final IDestination destination = data.destination;
      
      final IAST3localVariable tmp = CEASTlocalVariable.generateVariable();
      introduceVariable(tmp, buffer, lexenv, common);
      ICgenLexicalEnvironment bodyLexenv = lexenv;
      bodyLexenv = bodyLexenv.extend(tmp);
      analyze ((IAST2) iast.getOperand(),
          new Data(buffer, bodyLexenv, common,
              new AssignDestination(tmp) ));
      buffer.append(";\n");
      destination.compile(buffer, bodyLexenv, common);
      buffer.append("(");
      buffer.append(iast.getOperatorName());
      buffer.append(tmp.getMangledName());
      buffer.append(");");

      return null;
  }

  // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u07
  
  /*
                     ------------------------------------------------> d
                     operationBinaire(operateur, operande1, operande2)

var tmp1;
var tmp2;

---------> tmp1
operande1;

---------> tmp2
operande2;

d (tmp1 operateur tmp2);

   */

  public Object visit(IAST2binaryOperation iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      final IDestination destination = data.destination;
      
      final IAST3localVariable right = CEASTlocalVariable.generateVariable();
      final IAST3localVariable left  = CEASTlocalVariable.generateVariable();
      ICgenLexicalEnvironment bodyLexenv = lexenv.extend(left).extend(right);
      introduceVariable(right, buffer, lexenv, common);
      introduceVariable(left, buffer, lexenv, common);
      analyze ((IAST2) iast.getLeftOperand(),
          new Data(buffer, bodyLexenv, common,
              new AssignDestination(left) ));
      buffer.append(";\n");
      analyze ((IAST2) iast.getRightOperand(),
          new Data(buffer, bodyLexenv, common,
              new AssignDestination(right) ));
      buffer.append(";\n");
      destination.compile(buffer, bodyLexenv, common);
      buffer.append("(");
      buffer.append(left.getMangledName());
      buffer.append(iast.getOperatorName());
      buffer.append(right.getMangledName());
      buffer.append(");");
      
      return null;
  }

  // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u20
  // mais en sautant u13 (test operateur modulo) car il n'y a pas de
  // vrais entiers en javascript.
  
  /*
   * Que l'on n'emploie pas la valeur d'une expression n'est pas g��nant
   * en Javascript. En revanche, pr��fixer par (void) n'a pas de sens en 
   * Javascript.
   */
  
  public static class JSVoidDestination implements IDestination {
      private static final JSVoidDestination VOID_DESTINATION = 
          new JSVoidDestination();
      private JSVoidDestination() {}

      // Singleton
      public static JSVoidDestination create() {
          return VOID_DESTINATION;
      }

      public void compile (final StringBuffer buffer,
                           final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common) {
          buffer.append(" ");
      }
  }

  /*
                          ----------------------> d
                          sequence(instructions)
  
------------>jsvoid
instruction1;
... // pour toutes les instructions sauf la derni��re

-------------> d
instructionN;

   */
  
  public Object visit(IAST2sequence iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      
      IAST2instruction[] instructions =  iast.getInstructions();
      for ( int i = 0 ; i<instructions.length-1 ; i++ ) {
    	  analyze ((IAST2) instructions[i],
           new Data(buffer, lexenv, common, JSVoidDestination.create() ));
        // ATTENTION meme si js a des ; souvent implicites, instructions
        // est de type IAST4expression.
        buffer.append(";\n");
      }
      final IAST2instruction last = instructions[instructions.length-1];
      analyze ((IAST2) last, data);
      buffer.append(";\n");

      return null;
  }

  // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u22
  
  /*
                           ---------------------------------------------> d
                           alternative(condition, consequence, alternant)

var tmp;

----------> tmp
condition;

if ( tmp ) {
 -----------> d
 consequence;
} else {
 ----------> d
 alternant;
}

   */
  
  public Object visit(IAST2alternative iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      
      final IAST3localVariable tmp = CEASTlocalVariable.generateVariable();
      introduceVariable(tmp, buffer, lexenv, common);
      analyze ((IAST2) iast.getCondition(),
              new Data(buffer, lexenv, common, new AssignDestination(tmp)));
      buffer.append(";\n if ( ");
      buffer.append(tmp.getMangledName());
      buffer.append(" ) {\n");
      analyze ((IAST2) iast.getConsequent(), data);
      buffer.append(";\n } else {\n");
      try {
		analyze ((IAST2) iast.getAlternant(), data);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
      buffer.append(";\n }\n");

      return null;
  }

  // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u25
  
  /*
                    -------------------> d
                    reference(variable)
                    
d variable;
//au renommage pr��s de la variable.
              
   */

  public Object visit(IAST2reference iast, Data data) 
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      final IDestination destination = data.destination;
      
      destination.compile(buffer, lexenv, common);
      buffer.append(" ");
      try {
          buffer.append(lexenv.compile(iast.getVariable()));
      } catch (CgenerationException e) {
          try {
              buffer.append(common.compileGlobal(iast.getVariable()));
          } catch (CgenerationException e1) {
              throw new RuntimeException(e1);
          }
      }
      buffer.append(" ");
      
      return null;
  }

  /*
Cf. blocLocal 
                      ------------------------------------------> d
                      blocUnaire(variable, initialisation, corps)
                      
var tmp;

--------------> tmp
initialisation;

var variable = tmp;

-----> d
corps;

   */
  
  public Object visit(IAST2unaryBlock iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      final IDestination destination = data.destination;
      
      final IAST3localVariable tmp = CEASTlocalVariable.generateVariable();
      final ICgenLexicalEnvironment lexenv2 = lexenv.extend(tmp);
      final ICgenLexicalEnvironment lexenv3 =
          lexenv2.extend(iast.getVariable());

      introduceVariable(tmp, buffer, lexenv, common);
      analyze ((IAST2) iast.getInitialization(),
          new Data(buffer, lexenv, common,
              new AssignDestination(tmp) ));
      buffer.append(";\n");

      buffer.append("{\n");
      introduceVariable(iast.getVariable(), buffer, lexenv2, common);
      buffer.append(iast.getVariable().getMangledName());
      buffer.append(" = ");
      buffer.append(tmp.getMangledName());
      buffer.append(";\n");

      analyze ((IAST2) iast.getBody(),
          new Data(buffer, lexenv3, common, destination));
      buffer.append(";}\n");
      
      return null;
  }
  
  /*
  Cas non int��gr��:
                        -----------------------------------------> d
                        invocationGlobale(nomFonction, arguments)
                        
  var tmpI;
  //autant que d'arguments

  ---------> tmpI
  argumentI
  ... // autant que d'arguments 

  d nomFonction(tmp1, tmp2, ...);

     */
    
    public Object visit (IAST2invocation iast, Data data) 
    throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        final IDestination destination = data.destination;
        
        	IAST2expression[] args = iast.getArguments();
        	IAST3localVariable[] tmps = new IAST3localVariable[args.length];
        	ICgenLexicalEnvironment bodyLexenv = lexenv;
        	for ( int i=0; i<args.length ; i++ ) {
        		tmps[i] = CEASTlocalVariable.generateVariable();
        		introduceVariable(tmps[i], buffer, lexenv, common);
        		bodyLexenv = bodyLexenv.extend(tmps[i]);
            }	
            for ( int i=0 ; i<args.length ; i++ ) {
          	  analyze ((IAST2) args[i], 
                    new Data(buffer, bodyLexenv, common,
                        new AssignDestination(tmps[i]) ));
                buffer.append(";\n");
            }
            destination.compile(buffer, bodyLexenv, common);
            iast.getFunction().compileExpression(buffer, lexenv, common);
            //buffer.append(iast.getFunctionGlobalVariable().getMangledName());
            buffer.append("(");
            for ( int i=0 ; i<(args.length-1) ; i++ ) {
                buffer.append(tmps[i].getMangledName());
                buffer.append(", ");
            }
            if ( args.length > 0 ) {
                buffer.append(tmps[args.length-1].getMangledName());
            }
            buffer.append(");\n");
            
     
        
        return null;
    }
    

  // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u36
  // Attention u35 demande a definir PI.
  
/*
             ------------------------------------------> d
             invocationPrimitive(nomFonction, arguments)
  
var tmpI;
... // autant que d'arguments

---------> tmpI
argumentI
... // autant que d'arguments 

d nomFonction(tmp1, tmp2, ...);

*/

  public Object visit(IAST2primitiveInvocation iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      final IDestination destination = data.destination;
      
      IAST2expression[] args = iast.getArguments();
      IAST3localVariable[] tmps = new IAST3localVariable[args.length];
      ICgenLexicalEnvironment bodyLexenv = lexenv;
      for ( int i=0; i<args.length ; i++ ) {
          tmps[i] = CEASTlocalVariable.generateVariable();
          introduceVariable(tmps[i], buffer, lexenv, common);
          bodyLexenv = bodyLexenv.extend(tmps[i]);
      }
      for ( int i=0 ; i<args.length ; i++ ) {
    	  analyze ((IAST2) args[i],
              new Data(buffer, bodyLexenv, common,
                  new AssignDestination(tmps[i]) ));
          buffer.append(";\n");
      }
      destination.compile(buffer, bodyLexenv, common);
      buffer.append("ILP['");
      buffer.append(iast.getPrimitiveName());
      buffer.append("'](");
      for ( int i=0 ; i<(args.length-1) ; i++ ) {
          buffer.append(tmps[i].getMangledName());
          buffer.append(", ");
      }
      if ( args.length > 0 ) {
          buffer.append(tmps[args.length-1].getMangledName());
      }
      buffer.append(");\n");
      
      return null;
  }

  // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u48
  
  /*
                       --------------------------------> d
                       affectation(variable, expression)

var tmp;

----------> tmp
expression;

d ( variable = tmp );                         

   */
  
  public Object visit(IAST2assignment iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      
      analyze ((IAST2) iast.getValue(),
          new Data(buffer, lexenv, common,
              new AssignDestination(iast.getVariable()) ));
      buffer.append(";\n");
      buffer.append(iast.getVariable().getMangledName());
      buffer.append(";\n");       
      
      return null;
  }

  // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u51
  
  /*
                       -----------------------> d
                       boucle(condition, corps)


while ( true ) {
 var tmp;
 
 ---------> tmp
 condition;
 
 if ( tmp ) {
    ------> jsvoid
    corps;
 } else {
    break;
 }
}
d false;
                    
   */
  
  public Object visit(IAST2while iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;
      
      IAST3localVariable tmp = CEASTlocalVariable.generateVariable();
      buffer.append(" while ( true ) {\n");
      introduceVariable(tmp, buffer, lexenv, common);
      ICgenLexicalEnvironment bodyLexenv = lexenv;
      bodyLexenv = bodyLexenv.extend(tmp);
      analyze ((IAST2) iast.getCondition(),
          new Data(buffer, bodyLexenv, common,
              new AssignDestination(tmp) ));
      IDestination garbage = JSVoidDestination.create();
      buffer.append(" if ( ");
      buffer.append(tmp.getMangledName());
      buffer.append(" ) {\n");
      analyze ((IAST2) iast.getBody(),
          new Data(buffer, bodyLexenv, common, garbage));
      buffer.append("}\n else { break; }\n}\n");
      analyze ((IAST2) (new CEASTboolean("false")), data);
      buffer.append(";\n");

      return null;
  }

  // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u59025
  
  /*
                          ---------------------------------------> d
                          try(corps, catchVar, catcher, finallyer)

On suppose ici avoir �� la fois catch et finally:

try {
 ------> d
 corps;
} catch ( catchVar ) {
 -------> jsvoid
 catcher;
} finally {
 ---------> jsvoid
 finallyer;
}

   */

  public Object visit(IAST3try iast, Data data)
          throws CgenerationException {
      final StringBuffer buffer = data.buffer;
      final ICgenLexicalEnvironment lexenv = data.lexenv;
      final ICgenEnvironment common = data.common;

      // NOTA: pas du tout comme en C:
      buffer.append("try {\n");
      analyze ((IAST2) iast.getBody(), data);
      buffer.append("\n}");
      if ( iast.getCatcher() != null ) {
          IAST2variable cv = iast.getCaughtExceptionVariable(); 
          final ICgenLexicalEnvironment catcherLexenv = lexenv.extend(cv);
          buffer.append(" catch (");
          buffer.append(cv.getMangledName());
          buffer.append(") {\n");
          analyze ((IAST2) iast.getCatcher(),
              new Data(buffer, catcherLexenv, common, JSVoidDestination.create()));
          buffer.append("\n}");
      }
      if ( iast.getFinallyer() != null ) {
          buffer.append(" finally {\n");
          analyze ((IAST2) iast.getFinallyer(),
              new Data(buffer, lexenv, common, JSVoidDestination.create()));
          buffer.append("\n}");
      }
      buffer.append("\n");

      return null;
  }
  
  // NOTE: tout ce qui est au-dessus suffit pour tous les tests sauf
  // u13 (pas d'entier) et u645 (qui n'est pas une erreur en javascript).


  public Object visit(IAST2variable iast, Data data)
          throws CgenerationException {
      return null;
  }
  
  public Object visit(IAST2expression iast, Data data)
          throws CgenerationException {
      return null;
  }
  
  public Object visit(IAST2instruction iast, Data data)
          throws CgenerationException {
      return null;
  }

}

  