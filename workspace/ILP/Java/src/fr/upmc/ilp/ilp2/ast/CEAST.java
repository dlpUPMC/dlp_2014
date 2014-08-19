/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEAST.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.ast;

import java.util.Set;

import fr.upmc.ilp.ilp2.interfaces.IAST2;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;

/** Code commun à CEASTInstruction, à CEASTfunctionDefinition et à
 * CEASTprogram et a toutes leurs sous-classes. */

public abstract class CEAST
  implements IAST2<CEASTparseException> {
    // Pas moyen d'imposer que les sous-classes aient toutes
    // une méthode statique parse(Element, IParser).
    
    /** Chercher les variables globales dans un AST. Les variables trouvées
     * sont insérées dans l'ensemble globalvars. */
    public abstract void
    findGlobalVariables (final Set<IAST2variable> globalvars,
                       final ICgenLexicalEnvironment lexenv  );
}

// end of CEAST.java
