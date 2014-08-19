/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: IProcess.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.interfaces;

import org.w3c.dom.Document;

import fr.upmc.ilp.tool.IContent;
import fr.upmc.ilp.tool.IFinder;

/** Cette interface décrit un java.bean pour l'évaluation d'un
 * programme ILP. 
 */

public interface IProcess {
    
    // Rendre le processus plus verbeux:
    void setVerbose (boolean verbose);
    
    // Indiquer où trouver l'utilitaire qui sait chercher les fichiers utiles:
    IFinder getFinder ();
    void setFinder (IFinder finder);

    // Cycle de vie

    /** Initialiser le processus d'évaluation avec un contenu (le plus
     * souvent un fichier) c'est-à-dire le texte du programme. Si une
     * exception survient lors de l'initialisation, elle sera stockée 
     * en getInitializationFailure(). Si tout se passe bien, le texte du
     * programme à considérer sera accessible en getProgramText(). */
    void initialize (IContent ic);
    boolean isInitialized ();
    Throwable getInitializationFailure ();
    String getProgramText ();

    /** Préparer le processus d'évaluation jusqu'à choisir entre
     * interprétation ou compilation. Toutes les phases d'analyse communes
     * sont effectuées ici. Si une exception survient lors de la préparation,
     * elle sera stockée en getPreparationFailure(). Si tout se passe bien
     * l'AST obtenu sera accessible via getIAST(). */
    void prepare ();
    boolean isPrepared ();
    Throwable getPreparationFailure ();
    Document getDocument ();
    void setGrammar(java.io.File rngFile);
    java.io.File getGrammar();
    IAST getIAST();
    void setIAST(IAST iast);
    // getParser()
    // setParser(IParser iparser)

    /** Évaluer par interprétation. Si une exception survient, elle sera
     * accessible en getInterpretationFailure(). Si une valeur est obtenue,
     * elle sera accessible en getInterpretationValue(), ce qui est écrit
     * par les primitives print et newline est accumulé et accessible par
     * getInterpretationPrinting(). */
    void interpret ();
    boolean isInterpreted ();
    Throwable getInterpretationFailure ();
    Object getInterpretationValue ();
    String getInterpretationPrinting ();

    /** Compiler vers un fichier. Si une exception survient, elle sera
     * accessible avec getCompilationFailure(). Si la compilation se passe
     * bien, le code produit sera accessible via getCompiledProgram(). La
     * compilation vers C est aidée par un patron (setCTemplateFile) et
     * par un script (seCompileThenRunScript) */
    void compile ();
    boolean isCompiled ();
    Throwable getCompilationFailure ();
    String getCompiledProgram ();
    void setCFile(java.io.File cFile);
    void setCompileThenRunScript (java.io.File scriptFile);
    // File getCompiledFile();

    /** Exécuter le fichier compilé. Si un problème survient, il sera
     * stocké en getExecutionFailure(). Sinon, ce qu'imprime le programme
     * compilé est accessible via getExecutionPrinting(). */
    void runCompiled ();
    boolean isExecuted ();
    Throwable getExecutionFailure ();
    String getExecutionPrinting ();
}
