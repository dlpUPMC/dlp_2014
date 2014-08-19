package fr.upmc.ilp.ilp4.interfaces;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2;

public interface IAST4delegable extends IAST4 {

    /** Rendre l'objet d'ILP2 a qui ILP4 delegue le stockage des donnees
     * et l'interpretation. Son type sera en general bien plus precis. */
    IAST2<CEASTparseException> getDelegate ();
}
