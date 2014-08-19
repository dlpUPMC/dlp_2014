package fr.upmc.ilp.ilp6.runtime;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;

public interface ICommon 
extends fr.upmc.ilp.ilp2.interfaces.ICommon {
    
    ILPClass findClass (final String name)
    throws EvaluationException;
    
    void addClass (final ILPClass clazz);
}
