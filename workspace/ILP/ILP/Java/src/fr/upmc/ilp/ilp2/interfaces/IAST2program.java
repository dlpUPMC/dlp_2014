package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;

public interface IAST2program<Exc extends Exception> 
extends IAST2<Exc> {
    IAST2instruction<Exc> getBody ();

    IAST2functionDefinition<Exc>[] getFunctionDefinitions ();

    IAST2variable[] getGlobalVariables ();

    void computeGlobalVariables (ICgenLexicalEnvironment lexenv);

    String compile (ICgenLexicalEnvironment lexenv,
                    ICgenEnvironment common )
        throws CgenerationException;
}
