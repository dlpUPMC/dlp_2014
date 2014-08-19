/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: BasicEmptyEnvironment.java 931 2010-08-21 11:35:41Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.runtime;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.IEnvironment;

/** Une simple liste vide chainee de variables (de type V).
 *
 * Attention: pas moyen de parametrer le type de l'exception
 * a signaler en cas d'environnement vide. */

public class BasicEmptyEnvironment<V extends IAST2variable>
implements IEnvironment<V> {

    protected BasicEmptyEnvironment () {}

    public BasicEmptyEnvironment<V> getNext () {
        final String msg = "Basic empty environment!";
        throw new RuntimeException(msg);
    }

    public V getVariable () {
        final String msg = "No variable in an empty environment!";
        throw new RuntimeException(msg);
    }

    public boolean isEmpty () {
        return true;
    }

    public boolean isPresent (V variable) {
        return false;
    }

	public @OrNull IEnvironment<V> shrink(V variable) {
		return null;
	}

}
