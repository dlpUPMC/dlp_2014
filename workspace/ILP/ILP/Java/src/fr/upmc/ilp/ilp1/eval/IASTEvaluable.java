package fr.upmc.ilp.ilp1.eval;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

public interface IASTEvaluable {

    /** La méthode qui évalue un IAST et retourne sa valeur. Attention:
     * les valeurs sont des objets JAVA (POJO comme l'on dit). */

    Object eval (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException;

}
