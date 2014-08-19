package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp6.runtime.ICommon;

public interface IAST6program
extends IAST4program {

    IAST6classDefinition[] getClassDefinitions ();

    Object eval6 (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException;

    String compile6 (ICgenLexicalEnvironment lexenv,
                     ICgenEnvironment common )
    throws CgenerationException;
    
    IAST6program normalize6 (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST6Factory factory )
      throws NormalizeException;
}
