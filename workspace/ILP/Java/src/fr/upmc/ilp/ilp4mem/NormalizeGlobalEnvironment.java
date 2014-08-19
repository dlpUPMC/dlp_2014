package fr.upmc.ilp.ilp4mem;

import java.util.HashMap;
import java.util.Map;

import fr.upmc.ilp.ilp4.interfaces.IAST4constant;

public class NormalizeGlobalEnvironment 
extends fr.upmc.ilp.ilp4.ast.NormalizeGlobalEnvironment
implements INormalizeConstantEnvironment {
    
    public NormalizeGlobalEnvironment () {
        super();
        this.constants = new HashMap<>();
    }
    private final Map<String,IAST4globalConstant> constants;
     
    public IAST4globalConstant add (IAST4constant iast) {
        IAST4globalConstant gv = constants.get(iast.getDescription());
        if ( gv == null ) {
            gv = new CEASTglobalConstant(iast);
            constants.put(iast.getDescription(), gv);
            add(gv);
        }
        return gv;
    }
}
