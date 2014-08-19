package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp4.ast.NormalizeException;

public interface IAST4expression
extends IAST4, IAST4visitable, IAST2expression<CEASTparseException> {

    IAST4expression normalize (INormalizeLexicalEnvironment lexenv,
                               INormalizeGlobalEnvironment common,
                               IAST4Factory factory )
    throws NormalizeException;
    
    /** Compilation d'une instruction ou expression. Production de code
     * C par ajout à un tampon, dans un environnement lexical et un
     * environnement global. Le résultat est produit avec une certaine
     * destination. */

    void compile (StringBuffer buffer,
                  ICgenLexicalEnvironment lexenv,
                  ICgenEnvironment common,
                  IDestination destination )
    throws CgenerationException;

    //NOTE: Methodes heritees d'ILP2 nuisibles en ILP4. On se
    // contente de les marquer comme obsoletes.

    @Deprecated
    void compileExpression (
            StringBuffer buffer,
            ICgenLexicalEnvironment lexenv,
            ICgenEnvironment common,
            IDestination destination )
    throws CgenerationException;

    @Deprecated
    void compileExpression (
            StringBuffer buffer,
            ICgenLexicalEnvironment lexenv,
            ICgenEnvironment common )
    throws CgenerationException;
}
