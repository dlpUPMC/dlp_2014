package fr.upmc.ilp.ilp4.jsgen;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.cgen.ReturnDestination;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.ast.CEASTboolean;
import fr.upmc.ilp.ilp4.ast.CEASTfunctionDefinition;
import fr.upmc.ilp.ilp4.ast.CEASTglobalFunctionVariable;
import fr.upmc.ilp.ilp4.ast.CEASTlocalVariable;
import fr.upmc.ilp.ilp4.cgen.AssignDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4alternative;
import fr.upmc.ilp.ilp4.interfaces.IAST4assignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4binaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4computedInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4constant;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4invocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4localAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4localBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4primitiveInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4reference;
import fr.upmc.ilp.ilp4.interfaces.IAST4sequence;
import fr.upmc.ilp.ilp4.interfaces.IAST4try;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.IAST4while;
import fr.upmc.ilp.tool.CStuff;

/*
 * Du fait de la sémantique des variables locales en javascript, 
 * il n'y a plus besoin d'enserrer les instructions dans des accolades
 * pour limiter la portée des variables locales temporaires. 
 */
 
public class Compiler 
implements IAST4visitor<fr.upmc.ilp.ilp4.jsgen.Compiler.Data, 
                        Object, CgenerationException> {
    
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
    
    public Object visit(IAST4program iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        
        // le code des fonctions globales:
        buffer.append("\n/* Fonctions globales: */\n");
        IAST4functionDefinition[] definitions = iast.getFunctionDefinitions();
        for ( IAST4functionDefinition fun : definitions ) {
            fun.accept(this, data);
        }
        // Emettre les instructions regroupees dans une fonction:
        buffer.append("\n/* Code hors fonction: */\n");
        IAST4globalFunctionVariable program =
            new CEASTglobalFunctionVariable(PROGRAM);
        IAST4functionDefinition bodyAsFunction =
            new CEASTfunctionDefinition(
                    program,
                    new IAST4variable[0],
                    iast.getBody() );
        bodyAsFunction.accept(this, data);
        buffer.append("\n/* fin */\n");
        
        return null;
    }
    /** Le nom de la fonction Javascript correspondant au programme. */
    public static String PROGRAM = "ilp_program";

    public void introduceVariable (final IAST4variable var,
                                   final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final ICgenEnvironment common ) {
        buffer.append("var ");
        buffer.append(var.getMangledName());
        buffer.append(";\n");
    }
    
    /*
                        -------------------------------------------> d
                        blocLocal(variables, initialisations, corps)

Les accolades environnantes sont inutiles puisqu'aucune portée n'est
associée aux blocs locaux. Les accolades enserrant le corps des fonctions
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
    
    public Object visit(IAST4localBlock iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        final IDestination destination = data.destination;

        final IAST4variable[] vars = iast.getVariables();
        final IAST4expression[] inits = iast.getInitializations();
        IAST4localVariable[] temp = new IAST4localVariable[vars.length];
        ICgenLexicalEnvironment templexenv = lexenv;

        for (int i = 0; i < vars.length; i++) {
            temp[i] = CEASTlocalVariable.generateVariable();
            templexenv = templexenv.extend(temp[i]);
            introduceVariable(temp[i], buffer, templexenv, common);
        }

        for (int i = 0; i < vars.length; i++) {
            inits[i].accept(this, 
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

        iast.getBody().accept(this, 
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
    
    public Object visit(IAST4functionDefinition iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;

        // Émettre en commentaire les fonctions appelées:
        if ( iast.getInvokedFunctions().size() > 0 ) {
            buffer.append("/* Fonctions globales invoquées: ");
            for ( IAST4globalFunctionVariable gv : iast.getInvokedFunctions() ) {
                buffer.append(gv.getMangledName());
                buffer.append(" ");
            }
            buffer.append(" */\n");
        }
        if ( iast.isRecursive() ) {
            buffer.append("/* Cette fonction est récursive. */\n");
        }
        // Émettre la définition de la fonction:
        buffer.append("\nfunction ");
        buffer.append(iast.getDefinedVariable().getMangledName());
        compileVariableList(iast, buffer);
        buffer.append("\n{\n");
        // iast.extendWithFunctionVariables(lexenv);
        ICgenLexicalEnvironment bodyLexenv = lexenv;
        final IAST4variable[] variables = iast.getVariables();
        for ( int i = 0 ; i<variables.length ; i++ ) {
          bodyLexenv = bodyLexenv.extend(variables[i]);
        }
        iast.getBody().accept(this,
            new Data(buffer, bodyLexenv, common, ReturnDestination.create()));
        buffer.append(";\n}\n");
        
        return null;
    }

    public void compileVariableList (final IAST4functionDefinition iast, 
                                     final StringBuffer buffer) {
        buffer.append(" (");
        final IAST4variable[] variables = iast.getVariables();
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
Cas non intégré:
                        -----------------------------------------> d
                        invocationGlobale(nomFonction, arguments)
                        
var tmpI;
// autant que d'arguments

---------> tmpI
argumentI
... // autant que d'arguments 

d nomFonction(tmp1, tmp2, ...);

     */
    
    public Object visit(IAST4globalInvocation iast, Data data) 
    throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        final IDestination destination = data.destination;
        
        if ( iast.getInlined() == null ) {
            // L'invocation n'a pas été intégrée.

            IAST4expression[] args = iast.getArguments();
            IAST4localVariable[] tmps = new IAST4localVariable[args.length];
            ICgenLexicalEnvironment bodyLexenv = lexenv;
            for ( int i=0; i<args.length ; i++ ) {
                tmps[i] = CEASTlocalVariable.generateVariable();
                introduceVariable(tmps[i], buffer, lexenv, common);
                bodyLexenv = bodyLexenv.extend(tmps[i]);
            }
            for ( int i=0 ; i<args.length ; i++ ) {
                args[i].accept(this, 
                    new Data(buffer, bodyLexenv, common,
                        new AssignDestination(tmps[i]) ));
                buffer.append(";\n");
            }
            destination.compile(buffer, bodyLexenv, common);
            buffer.append(iast.getFunctionGlobalVariable().getMangledName());
            buffer.append("(");
            for ( int i=0 ; i<(args.length-1) ; i++ ) {
                buffer.append(tmps[i].getMangledName());
                buffer.append(", ");
            }
            if ( args.length > 0 ) {
                buffer.append(tmps[args.length-1].getMangledName());
            }
            buffer.append(");\n");
            
        } else {
          // L'invocation a été intégrée.
            
          buffer.append("/* Appel intégré à ");
          buffer.append(iast.getFunctionGlobalVariable().getMangledName());
          buffer.append(" */\n");
          iast.getInlined().accept(this, 
                new Data(buffer, lexenv, common, destination));
        }
        
        return null;
    }
    
    /*
                       ----------------> d
                       constante(valeur)
                       
d valeur;     // pour entier, flottant
d true;       // pour les booléens
d false;                       
d "chaine";   // pour les chaînes de caractères

     */

    public Object visit(IAST4constant iast, Data data) 
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

    public Object visit(IAST4unaryOperation iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        final IDestination destination = data.destination;
        
        final IAST4localVariable tmp = CEASTlocalVariable.generateVariable();
        introduceVariable(tmp, buffer, lexenv, common);
        ICgenLexicalEnvironment bodyLexenv = lexenv;
        bodyLexenv = bodyLexenv.extend(tmp);
        iast.getOperand().accept(this,
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

    public Object visit(IAST4binaryOperation iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        final IDestination destination = data.destination;
        
        final IAST4variable right = CEASTlocalVariable.generateVariable();
        final IAST4variable left  = CEASTlocalVariable.generateVariable();
        ICgenLexicalEnvironment bodyLexenv = lexenv.extend(left).extend(right);
        introduceVariable(right, buffer, lexenv, common);
        introduceVariable(left, buffer, lexenv, common);
        iast.getLeftOperand().accept(this,
            new Data(buffer, bodyLexenv, common,
                new AssignDestination(left) ));
        buffer.append(";\n");
        iast.getRightOperand().accept(this,
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
     * Que l'on n'emploie pas la valeur d'une expression n'est pas gênant
     * en Javascript. En revanche, préfixer par (void) n'a pas de sens en 
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
 ... // pour toutes les instructions sauf la dernière
 
 -------------> d
 instructionN;
 
     */
    
    public Object visit(IAST4sequence iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        
        IAST4expression[] instructions = iast.getInstructions();
        for ( int i = 0 ; i<instructions.length-1 ; i++ ) {
          instructions[i].accept(this,
             new Data(buffer, lexenv, common, JSVoidDestination.create() ));
          // ATTENTION meme si js a des ; souvent implicites, instructions
          // est de type IAST4expression.
          buffer.append(";\n");
        }
        final IAST4expression last = instructions[instructions.length-1];
        last.accept(this, data);
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
    
    public Object visit(IAST4alternative iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        
        final IAST4variable tmp = CEASTlocalVariable.generateVariable();
        introduceVariable(tmp, buffer, lexenv, common);
        iast.getCondition().accept(this,
                new Data(buffer, lexenv, common, new AssignDestination(tmp)));
        buffer.append(";\n if ( ");
        buffer.append(tmp.getMangledName());
        buffer.append(" ) {\n");
        iast.getConsequent().accept(this, data);
        buffer.append(";\n } else {\n");
        iast.getAlternant().accept(this, data);
        buffer.append(";\n }\n");

        return null;
    }

    // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u25
    
    /*
                      -------------------> d
                      reference(variable)
                      
d variable;
// au renommage près de la variable.
                
     */

    public Object visit(IAST4reference iast, Data data) 
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
    
    public Object visit(IAST4unaryBlock iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        final IDestination destination = data.destination;
        
        final IAST4variable tmp = CEASTlocalVariable.generateVariable();
        final ICgenLexicalEnvironment lexenv2 = lexenv.extend(tmp);
        final ICgenLexicalEnvironment lexenv3 =
            lexenv2.extend(iast.getVariable());

        introduceVariable(tmp, buffer, lexenv, common);
        iast.getInitialization().accept(this,
            new Data(buffer, lexenv, common,
                new AssignDestination(tmp) ));
        buffer.append(";\n");

        buffer.append("{\n");
        introduceVariable(iast.getVariable(), buffer, lexenv2, common);
        buffer.append(iast.getVariable().getMangledName());
        buffer.append(" = ");
        buffer.append(tmp.getMangledName());
        buffer.append(";\n");

        iast.getBody().accept(this,
            new Data(buffer, lexenv3, common, destination));
        buffer.append(";}\n");
        
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

    public Object visit(IAST4primitiveInvocation iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        final IDestination destination = data.destination;
        
        IAST4expression[] args = iast.getArguments();
        IAST4localVariable[] tmps = new IAST4localVariable[args.length];
        ICgenLexicalEnvironment bodyLexenv = lexenv;
        for ( int i=0; i<args.length ; i++ ) {
            tmps[i] = CEASTlocalVariable.generateVariable();
            introduceVariable(tmps[i], buffer, lexenv, common);
            bodyLexenv = bodyLexenv.extend(tmps[i]);
        }
        for ( int i=0 ; i<args.length ; i++ ) {
            args[i].accept(this,
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
    
    public Object visit(IAST4assignment iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;

        iast.getValue().accept(this,
            new Data(buffer, lexenv, common,
                new AssignDestination(iast.getVariable()) ));
        buffer.append(";\n");
        buffer.append(iast.getVariable().getMangledName());
        buffer.append(";\n");       
        
        return null;
    }
    public Object visit(IAST4localAssignment iast, Data data)
            throws CgenerationException {
        return visit((IAST4assignment) iast, data);
    }
    public Object visit(IAST4globalAssignment iast, Data data)
            throws CgenerationException {
        return visit((IAST4assignment) iast, data);
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
    
    public Object visit(IAST4while iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;
        
        IAST4localVariable tmp = CEASTlocalVariable.generateVariable();
        buffer.append(" while ( true ) {\n");
        introduceVariable(tmp, buffer, lexenv, common);
        ICgenLexicalEnvironment bodyLexenv = lexenv;
        bodyLexenv = bodyLexenv.extend(tmp);
        iast.getCondition().accept(this,
            new Data(buffer, bodyLexenv, common,
                new AssignDestination(tmp) ));
        IDestination garbage = JSVoidDestination.create();
        buffer.append(" if ( ");
        buffer.append(tmp.getMangledName());
        buffer.append(" ) {\n");
        iast.getBody().accept(this,
            new Data(buffer, bodyLexenv, common, garbage));
        buffer.append("}\n else { break; }\n}\n");
        (new CEASTboolean("false")).accept(this, data);
        buffer.append(";\n");

        return null;
    }

    // NOTE: tout ce qui est au-dessus suffit pour les tests u01 a u59025
    
    /*
                            ---------------------------------------> d
                            try(corps, catchVar, catcher, finallyer)
 
 On suppose ici avoir à la fois catch et finally:
 
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

    public Object visit(IAST4try iast, Data data)
            throws CgenerationException {
        final StringBuffer buffer = data.buffer;
        final ICgenLexicalEnvironment lexenv = data.lexenv;
        final ICgenEnvironment common = data.common;

        // NOTA: pas du tout comme en C:
        buffer.append("try {\n");
        iast.getBody().accept(this, data);
        buffer.append("\n}");
        if ( iast.getCatcher() != null ) {
            IAST4variable cv = iast.getCaughtExceptionVariable(); 
            final ICgenLexicalEnvironment catcherLexenv = lexenv.extend(cv);
            buffer.append(" catch (");
            buffer.append(cv.getMangledName());
            buffer.append(") {\n");
            iast.getCatcher().accept(this,
                new Data(buffer, catcherLexenv, common, JSVoidDestination.create()));
            buffer.append("\n}");
        }
        if ( iast.getFinallyer() != null ) {
            buffer.append(" finally {\n");
            iast.getFinallyer().accept(this,
                new Data(buffer, lexenv, common, JSVoidDestination.create()));
            buffer.append("\n}");
        }
        buffer.append("\n");

        return null;
    }
    
    // NOTE: tout ce qui est au-dessus suffit pour tous les tests sauf
    // u13 (pas d'entier) et u645 (qui n'est pas une erreur en javascript).

    public Object visit(IAST4invocation iast, Data data)
            throws CgenerationException {
        return null;
    }

    public Object visit(IAST4computedInvocation iast, Data data)
            throws CgenerationException {
        return null;
    }

    public Object visit(IAST4variable iast, Data data)
            throws CgenerationException {
        return null;
    }

}
