package fr.upmc.ilp.ilp4.interfaces;

public interface IAST4globalInvocation
extends IAST4invocation {
    IAST4globalFunctionVariable getFunctionGlobalVariable ();
    IAST4expression             getInlined ();
    void  setInlined(IAST4expression inlined);
}
