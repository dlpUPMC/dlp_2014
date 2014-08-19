package fr.upmc.ilp.ilp5.eval1;

import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;

/**
 * On réunit en un seul objet, le contexte d'évaluation c'est-à-dire
 * l'environnement lexical et global.
 */

public interface IContext {
    ILexicalEnvironment getLexicalEnvironment();
    ICommon getCommonEnvironment();
}
