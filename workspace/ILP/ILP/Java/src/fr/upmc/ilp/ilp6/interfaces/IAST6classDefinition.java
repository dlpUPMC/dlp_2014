package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

public interface IAST6classDefinition
extends IAST4, IAST6visitable {

    // Les caracteristiques propres a la classe

    /** Le nom de la classe. */

    String getName ();

    /** Le nom de la super-classe. */

    String getSuperClassName ();

    /** Les noms des champs propres introduits par la classe
     * et non pas herites. */

    String[] getProperFieldNames ();

    /** Les noms des methodes propres definies par la classe
     * et non heritees. */

    String[] getProperMethodNames ();

    /** Les definitions des methodes propres introduites par
     * la classe et non heritees. */

    IAST6methodDefinition[] getProperMethodDefinitions ();

    // Les caracteristiques heritees necessite la presence d'une
    // structure de memorisation.

    /** Les noms de tous les champs propres ou herites. */

    String[] getFieldNames (IClassEnvironment common);

    /** La definition d'une methode nommee. */

    IAST6methodDefinition getMethodDefinition (
            String name,
            IClassEnvironment common );

    int fieldSize (IClassEnvironment common);

    int inheritedFieldSize (IClassEnvironment common);

    int getFieldOffset (String fieldName, IClassEnvironment common);

    int getMethodOffset (String methodName, IClassEnvironment common)
    throws CgenerationException;

    int getNumberOfInheritedMethods (IClassEnvironment common);

    int getMethodsCount (IClassEnvironment common);

    // Compilation

    void compileHeader (StringBuffer buffer,
                        ICgenLexicalEnvironment lexenv,
                        ICgenEnvironment common )
    throws CgenerationException;

    void compile (StringBuffer buffer,
                  ICgenLexicalEnvironment lexenv,
                  ICgenEnvironment common )
    throws CgenerationException;

    /** Compilation de toutes les methodes. */

    void compileMethodsTable (StringBuffer buffer,
                              ICgenEnvironment common,
                              IAST6classDefinition clazz)
    throws CgenerationException;
    
    IAST6classDefinition normalize (
            INormalizeLexicalEnvironment lexenv,
            INormalizeGlobalEnvironment common,
            IAST6Factory factory )
      throws NormalizeException;
 }
