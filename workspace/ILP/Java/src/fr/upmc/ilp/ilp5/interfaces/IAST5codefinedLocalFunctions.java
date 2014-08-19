package fr.upmc.ilp.ilp5.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4expression;

/** Cet AST correspond à un letrec qui introduit des fonctions locales
 * non anonymes dans une certaine portée. */

public interface IAST5codefinedLocalFunctions
extends IAST4expression, IAST5visitable {

    /** Retourner l'ensemble des fonctions localement définies.
     * Elles sont co-définies c'est-à-dire en récursion mutuelle. */
    IAST5localFunctionDefinition[] getFunctions();

    /** Retourner l'expression (le corps) où seront visibles les fonctions
     * localement définies. */
    IAST4expression getBody();
}
