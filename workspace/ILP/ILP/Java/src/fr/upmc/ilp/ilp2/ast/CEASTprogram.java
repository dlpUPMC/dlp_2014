/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTprogram.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp2.interfaces.IAST2Factory;
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
extends CEAST
implements IAST2program<CEASTparseException> {

    public CEASTprogram (
            final IAST2functionDefinition<CEASTparseException>[] definitions,
            final IAST2instruction<CEASTparseException> body ) {
        this.definition = definitions;
        this.body = body;
        this.globalVariables = new HashSet<>();
    }
    protected IAST2functionDefinition<CEASTparseException>[] definition;
    protected IAST2instruction<CEASTparseException> body;

    private final static void sortDefinitionsAndInstructions (
            final List<IAST2<CEASTparseException>> items,
            final List<IAST2functionDefinition<CEASTparseException>> definitions,
            final List<IAST2instruction<CEASTparseException>> instructions ) {
        for ( IAST2<CEASTparseException> item : items ) {
            if ( item instanceof IAST2functionDefinition<?> ) {
                definitions.add((IAST2functionDefinition<CEASTparseException>) item);
            } else if ( item instanceof IAST2instruction<?> ) {
                instructions.add((IAST2instruction<CEASTparseException>) item);
            } else {
                final String msg = "Should never occur!";
                assert false : msg;
                throw new RuntimeException(msg);
            }
        }
    }

    public static IAST2program<CEASTparseException> parse (
            final Element e, final IParser<CEASTparseException> parser)
    throws CEASTparseException {
        final IAST2Factory<CEASTparseException> factory = parser.getFactory();
        final List<IAST2<CEASTparseException>> items =
                parser.parseList(e.getChildNodes());
        final List<IAST2functionDefinition<CEASTparseException>> definitions =
                new ArrayList<>();
        final List<IAST2instruction<CEASTparseException>> instructions =
                new ArrayList<>();
        sortDefinitionsAndInstructions(items, definitions, instructions);
        final IAST2instruction<CEASTparseException> body =
                factory.newSequence(instructions);
        final IAST2program<CEASTparseException> program = 
                factory.newProgram(
                        definitions.toArray(new CEASTfunctionDefinition[0]),
                        body );
        return program;
    }

    public IAST2instruction<CEASTparseException> getBody () {
        return this.body;
    }
    public IAST2functionDefinition<CEASTparseException>[] getFunctionDefinitions () {
        return this.definition;
    }
    public IAST2variable[] getGlobalVariables () {
        return this.globalVariables.toArray(CEASTvariable.EMPTY_VARIABLE_ARRAY);
    }
    private Set<IAST2variable> globalVariables;
    public String[] getGlobalVariableNames () {
        final Set<String> ss = new HashSet<>();
        for ( IAST2variable gv : globalVariables ) {
            ss.add(gv.getName());
        }
        return ss.toArray(new String[0]);
    }

    /** Recenser toutes les variables globales. */

    public void computeGlobalVariables (final ICgenLexicalEnvironment lexenv) {
        findGlobalVariables(this.globalVariables, lexenv);
    }

    //NOTE: Acces direct aux champs interdit a partir d'ici!

    public Object eval (final ILexicalEnvironment lexenv,
                        final ICommon common)
    throws EvaluationException {
        IAST2functionDefinition<CEASTparseException>[] definitions = getFunctionDefinitions();
        for ( int i = 0 ; i<definitions.length ; i++ ) {
            definitions[i].eval(lexenv, common);
        }
        return getBody().eval(lexenv, common);
    }

    /** Compiler une instruction en une chaine de caracteres. */

    public String compile (final ICgenLexicalEnvironment lexenv,
                           final ICgenEnvironment common )
    throws CgenerationException {
        computeGlobalVariables(lexenv);
        final StringBuffer buffer = new StringBuffer(4095);
        this.compile(buffer, lexenv, common);
        return buffer.toString();
    }

    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common )
    throws CgenerationException {
        buffer.append("#include <stdio.h>\n");
        buffer.append("#include <stdlib.h>\n");
        buffer.append("\n");
        buffer.append("#include \"ilp.h\"\n");
        buffer.append("#include \"ilpBasicError.h\"\n");
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
        buffer.append("int main (int argc, char *argv[]) {\n");
        buffer.append("  ILP_print(ilp_program());\n");
        buffer.append("  ILP_newline();\n");
        buffer.append("  return EXIT_SUCCESS;\n");
        buffer.append("}\n\n");
        buffer.append("/* end */\n");
    }

    @Override
    public void findGlobalVariables (
            final Set<IAST2variable> globalvars,
            final ICgenLexicalEnvironment lexenv ) {
        for ( IAST2functionDefinition<CEASTparseException> fun
                : getFunctionDefinitions() ) {
            fun.findGlobalVariables(globalvars, lexenv);
        }
        getBody().findGlobalVariables(globalvars, lexenv);
        // retirer les fonctions
        for ( IAST2functionDefinition<CEASTparseException> fun
                : getFunctionDefinitions() ) {
            globalvars.remove(new CEASTvariable(fun.getFunctionName()));
        }
    }
}

// end of CEASTprogram.java
