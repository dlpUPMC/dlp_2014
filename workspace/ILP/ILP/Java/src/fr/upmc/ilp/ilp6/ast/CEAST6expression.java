package fr.upmc.ilp.ilp6.ast;

import java.util.Set;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.cgen.NoDestination;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6expression;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.runtime.ICommon;

/** Cette classe abstraite ne sert qu'a partager cette methode
 * que l'on n'aurait jamais du heriter. */

public abstract class CEAST6expression
extends fr.upmc.ilp.ilp4.ast.CEASTexpression 
implements IAST6expression {

    /**
     * Pratique en Eclipse! Ainsi, dans la perspective de mise au point,
     * la valeur d'un CEASTprogram s'affichera de maniere plus lisible. Il
     * egalement possible de positionner (menu contextuel: edit detail
     * formatter) sur la variable Process.ceast qu'on veut la voir s'afficher
     * avec: "return new XMLwriter().process(this);". Cette meme astuce doit
     * fonctionner avec toute instance d'IAST4.
     */
    @Override
    public String toString () {
        try {
            if ( xmlwriter == null ) {
                xmlwriter = new XMLwriter();
            }
            return xmlwriter.process(this);
        } catch (Throwable t) {
            return super.toString();
        }
    }
    private static XMLwriter xmlwriter;

    @Override
    public void findGlobalVariables (
            final Set<IAST2variable> globalvars,
            final fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment lexenv ) {
        return;
    }

    @Override
    public IAST4expression normalize (
            final INormalizeLexicalEnvironment lexenv,
            final fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
    throws NormalizeException {
        return normalize6(lexenv, 
                CEAST6.narrowToINormalizeGlobalEnvironment(common), 
                CEAST6.narrowToIAST6Factory(factory) );
    }
    
    public abstract IAST4expression normalize6 (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST6Factory factory )
    throws NormalizeException;

    @Override
    public Object eval (final ILexicalEnvironment lexenv,
                        final fr.upmc.ilp.ilp2.interfaces.ICommon common)
    throws EvaluationException {
        return eval6(
                lexenv,
                CEAST6.narrowToICommon(common) );
    }
    public abstract Object eval6 (final ILexicalEnvironment lexenv,
                                  final ICommon common)
    throws EvaluationException;

    @Override
    public void compile (final StringBuffer buffer,
                         final ICgenLexicalEnvironment lexenv,
                         final fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment common,
                         final IDestination destination)
      throws CgenerationException {
        compile6(buffer,
                 lexenv,
                 CEAST6.narrowToICgenEnvironment(common),
                 destination );
    }
    public abstract void compile6 (final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final ICgenEnvironment common,
                                   final IDestination destination )
    throws CgenerationException;

    @Override
    @Deprecated
    public void compileExpression (final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment common,
                                   final IDestination destination )
    throws CgenerationException {
        this.compile6(buffer, 
                      lexenv, 
                      CEAST6.narrowToICgenEnvironment(common), 
                      NoDestination.create());
    }
    
    @Override
    @Deprecated
    public void compileInstruction (final StringBuffer buffer,
                                    final ICgenLexicalEnvironment lexenv,
                                    final fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment common,
                                    final IDestination destination)
    throws CgenerationException {
        this.compile6(buffer, 
                lexenv, 
                CEAST6.narrowToICgenEnvironment(common), 
                destination);
    }

    //public void accept (IAST4visitor visitor) {
    //    CEAST6.narrowToIAST6visitor(visitor).visit(this);
    // On ne visite pas les CEAST6expression!
    //}

}
