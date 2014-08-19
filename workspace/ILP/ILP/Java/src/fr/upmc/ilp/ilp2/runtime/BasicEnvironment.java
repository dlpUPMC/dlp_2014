/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: BasicEnvironment.java 1190 2011-12-19 15:58:38Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.runtime;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.IEnvironment;

/** Une simple liste non vide chainee de variables (de type V). */

public class BasicEnvironment<V extends IAST2variable>
implements IEnvironment<V> {

    public BasicEnvironment (V variable, IEnvironment<V> next) {
        this.variable = variable;
        this.next = next;
    }
    protected final V variable;
    protected final IEnvironment<V> next;

    public IEnvironment<V> getNext () {
        return this.next;
    }

    public V getVariable () {
        return this.variable;
    }

    public boolean isEmpty () {
        return false;
    }
	
	public @OrNull IEnvironment<V> shrink (V variable) {
        if ( getVariable().equals(variable) ) {
            return this;
        } else {
            return getNext().shrink(variable);
        }
	}
	
    public boolean isPresent (V variable) {
    	return null != this.shrink(variable);
    }
}
