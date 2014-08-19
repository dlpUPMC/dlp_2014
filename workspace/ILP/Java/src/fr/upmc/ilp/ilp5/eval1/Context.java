package fr.upmc.ilp.ilp5.eval1;

import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;

/**
 * On r�unit en un seul objet, le contexte d'�valuation c'est-�-dire
 * l'environnement lexical et global. */

public class Context implements IContext {

    public Context (ILexicalEnvironment lexenv, ICommon common) {
        this.lexenv = lexenv;
        this.common = common;
    }
    private ILexicalEnvironment lexenv;
    private ICommon common;

    public ILexicalEnvironment getLexicalEnvironment() {
        return this.lexenv;
    }
    public ICommon getCommonEnvironment() {
        return this.common;
    }
}
