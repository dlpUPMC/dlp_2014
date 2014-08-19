/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: IDestination.java 1190 2011-12-19 15:58:38Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp2.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;

/** Une interface pour décrire la destination d'une compilation.
 * Actuellement, il y a trois destinations possibles c'est-à-dire
 * trois implantations de cette interface: (void), return ou
 * l'affectation à une variable.  */

public interface IDestination {
    
    /** Émettre une destination dans le tampon de sortie. */
    void compile (final StringBuffer buffer,
                  final ICgenLexicalEnvironment lexenv, 
                  final ICgenEnvironment common ) throws CgenerationException ;
}

// end of IDestination.java
