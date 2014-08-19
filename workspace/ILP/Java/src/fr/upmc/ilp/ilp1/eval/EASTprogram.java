package fr.upmc.ilp.ilp1.eval;

import java.util.List;

import fr.upmc.ilp.ilp1.interfaces.IASTprogram;
import fr.upmc.ilp.ilp1.interfaces.IASTsequence;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

public class EASTprogram extends EAST implements IASTprogram {
    public EASTprogram (List<EAST> instructions) {
        this.body = new EASTsequence(instructions);
    }
    protected EASTsequence body;
    
    public IASTsequence getBody() {
        return body;
    }
    
    @Override
    public Object eval(ILexicalEnvironment lexenv, ICommon common)
            throws EvaluationException {
        return body.eval(lexenv, common);
    }
}
