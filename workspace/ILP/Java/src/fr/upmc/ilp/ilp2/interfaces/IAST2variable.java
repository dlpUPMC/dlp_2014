package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.interfaces.IASTvariable;

public interface IAST2variable
extends IASTvariable {

    String getMangledName ();

    void compileDeclaration (StringBuffer buffer,
                             ICgenLexicalEnvironment lexenv,
                             ICgenEnvironment common )
    throws CgenerationException;
}
