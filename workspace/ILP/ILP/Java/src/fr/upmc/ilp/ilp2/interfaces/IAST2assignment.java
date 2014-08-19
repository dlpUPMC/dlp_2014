/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTassignment.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.interfaces;

/** L"interface equivalente n'existait pas en ILP1 */

public interface IAST2assignment<Exc extends Exception>
extends IAST2expression<Exc> {

    IAST2expression<Exc> getValue ();
    IAST2variable getVariable ();
}
