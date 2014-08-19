package fr.upmc.ilp.ilp6.ast;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6methodDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6program;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.runtime.ICommon;

public abstract class CEAST6
extends fr.upmc.ilp.ilp4.ast.CEAST
implements IAST4 {

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

    public void compile (
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv,
            final fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment common )
      throws CgenerationException {
        compile6(buffer,
                 lexenv,
                 CEAST6.narrowToICgenEnvironment(common) );
    }
    public void compile (
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv,
            final ICgenEnvironment common )
      throws CgenerationException {
        compile6(buffer, lexenv, common);
    }
    public abstract void compile6 (final StringBuffer buffer,
                                   final ICgenLexicalEnvironment lexenv,
                                   final ICgenEnvironment common )
    throws CgenerationException;

    public void compileHeader (
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv,
            final fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment common )
      throws CgenerationException {
        compileHeader6(buffer,
                       lexenv,
                       CEAST6.narrowToICgenEnvironment(common) );
    }
    public void compileHeader (
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv,
            final ICgenEnvironment common )
    throws CgenerationException {
        compileHeader6(buffer, lexenv, common);
    }
    public abstract void compileHeader6 (
            final StringBuffer buffer,
            final ICgenLexicalEnvironment lexenv,
            final ICgenEnvironment common )
    throws CgenerationException;

    // Quelques retrecisseurs:

    public static <Data, Result, Exc extends Throwable> IAST6visitor<Data, Result, Exc> 
    narrowToIAST6visitor (IAST4visitor<Data, Result, Exc> visitor) {
        if ( visitor instanceof IAST6visitor<?,?,?> ) {
            return (IAST6visitor<Data, Result, Exc>) visitor;
        } else {
            final String msg = "Not an IAST6visitor: " + visitor;
            throw new ClassCastException(msg);
        }
    }
    public static IAST6methodDefinition narrowToIAST6methodDefinition (
            IAST4 o ) {
        if ( o instanceof IAST6methodDefinition ) {
            return (IAST6methodDefinition) o;
        } else {
            final String msg = "Not an IAST6methodDefinition: " + o;
            throw new ClassCastException(msg);
        }
    }
    public static ICommon narrowToICommon (
            fr.upmc.ilp.ilp2.interfaces.ICommon o) {
        if ( o instanceof ICommon ) {
            return (ICommon) o;
        } else {
            final String msg = "Not an ICommon6: " + o;
            throw new ClassCastException(msg);
        }
    }
    public static IAST6Factory narrowToIAST6Factory (
            IAST4Factory f ) {
        if ( f instanceof IAST6Factory ) {
            return (IAST6Factory) f;
        } else {
            final String msg = "Not an IAST6Factory: " + f;
            throw new ClassCastException(msg);
        }
    }
    public static IAST6program narrowToIAST6program (IAST4program f ) {
        if ( f instanceof IAST6program) {
            return (IAST6program) f;
        } else {
            final String msg = "Not an IAST6program: " + f;
            throw new ClassCastException(msg);
        }
    }
    public static INormalizeGlobalEnvironment
        narrowToINormalizeGlobalEnvironment (
            fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment o) {
        if ( o instanceof INormalizeGlobalEnvironment) {
            return (INormalizeGlobalEnvironment) o;
        } else {
            final String msg = "Not an INormalizeGlobalEnvironment6: " + o;
            throw new ClassCastException(msg);
        }
    }
    public static ICgenLexicalEnvironment narrowToICgenLexicalEnvironment (
            fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment o) {
        return o;
    }
    public static ICgenEnvironment narrowToICgenEnvironment (
            fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment o) {
        if ( o instanceof ICgenEnvironment) {
            return (ICgenEnvironment) o;
        } else {
            final String msg = "Not an ICgenEnvironment6: " + o;
            throw new ClassCastException(msg);
        }
    }
}
