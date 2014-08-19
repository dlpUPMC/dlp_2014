/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Idk$
 * GPL version=2
 *
 * D'après une suggestion d'Aurélien Moreau <aurelien.moreau@yienyien.net>
 * ******************************************************************/

package fr.upmc.ilp.ilp4.interfaces;

/** Un noeud d'AST est visitable s'il offre cette methode. Cette methode
 * ne procure qu'un rebond typé vers le visiteur.
 * Cf. fr.upmc.ilp.ilp4.interfaces.IAST4visitor */

public interface IAST4visitable {

    /** Ce visiteur peut prendre des donnees additionnelles dans la
     * variable data et retourne une valeur qui peut eventuellement
     * etre exploitee. */

    <Data, Result, Exc extends Throwable> Result accept (
            IAST4visitor<Data, Result, Exc> visitor, 
            Data data) throws Exc;

}
