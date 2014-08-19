/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTglobalFunctionVariable.java 936 2010-08-21 14:47:11Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;


/** Les variables globales nommant les fonctions globales forment une
 * classe à part car elles mènent à la définition de la fonction
 * qu'elles nomment. */
public class CEASTglobalFunctionVariable
extends CEASTglobalVariable
implements IAST4globalFunctionVariable {

    public CEASTglobalFunctionVariable (final String name) {
        super(name);
    }
    public CEASTglobalFunctionVariable (
            final String name,
            final IAST4functionDefinition functionDefinition ) {
        super(name);
        this.function = functionDefinition;
    }
    private IAST4functionDefinition function;

    /** Obtenir la définition de la fonction ainsi nommée. */
    public IAST4functionDefinition getFunctionDefinition () {
        assert(this.function != null);
        return this.function;
    }

    /** Modifier la définition de la fonction ainsi nommée. */
    public void setFunctionDefinition (IAST4functionDefinition function) {
        this.function = function;
    }

    /** Génération de variables de fonctions globales utilitaires. Leur
     * nom en C débute par le préfixe "ilp" afin de ne pas perturber les
     * variables du langage ILP.*/
    public static IAST4globalFunctionVariable generateGlobalFunctionVariable(
            final IAST4Factory factory) {
        return factory.newGlobalFunctionVariable("ilpFUNCTION");
    }

    /** Génération d'une déclaration globale d'une variable globale
     * nommant une fonction globale. */
    @Override
    public void compileGlobalDeclaration (
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv,
            final ICgenEnvironment common )
    throws CgenerationException {
        getFunctionDefinition().compileHeader(buffer, lexenv, common);
    }

    /** Compilation d'une variable en C pour en obtenir sa valeur.
     * Comme elle est de type ILP_Primitive, on recoltera un avertissement
     * de la part du typeur car, en ISO C, on ne peut convertir (ni dans un
     * sens, ni dans l'autre) un pointeur sur une fonction et un pointeur
     * sur une donnee. Cf. http://www.lysator.liu.se/c/rat/c2.html */
    @Override
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final ICgenEnvironment common,
                         final IDestination destination)
      throws CgenerationException {
        super.compile(buffer, lexenv, common, destination);
    }
}

// end of CEASTglobalFunctionVariable.java
