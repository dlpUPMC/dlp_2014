package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;

public interface IParser
extends fr.upmc.ilp.ilp3.IParser<CEASTparseException> {
    // On raffine la signature de la fabrique:
    IAST4Factory getFactory ();
}
