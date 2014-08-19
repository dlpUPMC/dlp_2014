/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: CgenMethodLexicalEnvironment.java 1190 2011-12-19 15:58:38Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.cgen;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IAST6methodDefinition;

public class CgenMethodLexicalEnvironment 
implements ICgenLexicalEnvironment {
    
    public CgenMethodLexicalEnvironment (final IAST6methodDefinition m,
                                         final ICgenLexicalEnvironment next) {
        this.methodDefinition = m;
        this.next = next;
    }
    protected final IAST6methodDefinition methodDefinition;
    protected final ICgenLexicalEnvironment next; 
    
    public IAST6methodDefinition getMethodDefinition () {
        return this.methodDefinition;
    }
    public ICgenLexicalEnvironment getNext () {
        return this.next;
    }
    // Il n'y a pas de variable associee:
    public IAST2variable getVariable () {
        throw new RuntimeException("Should never be invoked!");
    }
    public boolean isEmpty () {
        return false;
    }

    public String compile (IAST2variable variable) 
    throws CgenerationException {
        return this.next.compile(variable);
    }

    public ICgenLexicalEnvironment extend (final IAST2variable variable) {
        return new CgenLexicalEnvironment6(variable, this);
    }
    public ICgenLexicalEnvironment extend (final IAST2variable variable, 
                                           final String cname) {
        return new CgenLexicalEnvironment6(variable, cname, this);
    }
        
    public boolean isPresent (IAST2variable variable) {
        return this.next.isPresent(variable);
    }
    
    @Override
    public @OrNull ICgenLexicalEnvironment shrink(IAST2variable variable) {
        return this.next.shrink(variable);
    }

}
