package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;

public interface IAST2functionDefinition<Exc extends Exception>
extends IAST2<Exc> {

    String getFunctionName ();
    String getMangledFunctionName ();
    
    IAST2variable[] getVariables ();

    IAST2instruction<Exc> getBody ();

    void compileHeader (StringBuffer buffer,
                        ICgenLexicalEnvironment lexenv,
                        ICgenEnvironment common )
    throws CgenerationException;

    void compile (StringBuffer buffer,
                  ICgenLexicalEnvironment lexenv,
                  ICgenEnvironment common )
    throws CgenerationException;
}
