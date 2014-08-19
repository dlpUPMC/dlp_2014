package fr.upmc.ilp.ilp4mem;

import fr.upmc.ilp.ilp4.interfaces.IAST4constant;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;

public interface INormalizeConstantEnvironment extends
        INormalizeGlobalEnvironment {
    IAST4globalConstant add (IAST4constant iast);
}
