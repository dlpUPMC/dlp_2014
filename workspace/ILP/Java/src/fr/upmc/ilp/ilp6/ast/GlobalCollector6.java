package fr.upmc.ilp.ilp6.ast;

import fr.upmc.ilp.ilp4.ast.GlobalCollector;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6instantiation;
import fr.upmc.ilp.ilp6.interfaces.IAST6methodDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6readField;
import fr.upmc.ilp.ilp6.interfaces.IAST6self;
import fr.upmc.ilp.ilp6.interfaces.IAST6send;
import fr.upmc.ilp.ilp6.interfaces.IAST6super;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.IAST6writeField;

public class GlobalCollector6 extends GlobalCollector
implements IAST6visitor<Object, Object, RuntimeException> {
    
    public GlobalCollector6 () {
        super();
    }

    public static IAST4globalVariable[] getGlobalVariables (IAST4program ast) {
        final GlobalCollector6 visitor = new GlobalCollector6();
        ast.accept(visitor, null);
        return visitor.globals.toArray(new IAST4globalVariable[0]);
    }

    @Override
    public Object visit(IAST6classDefinition iast, Object nothing) {
        for (IAST6methodDefinition md : iast.getProperMethodDefinitions()) {
            md.accept(this, nothing);
        }
        return null;
    }

    @Override
    public Object visit(IAST6methodDefinition iast, Object nothing) {
        iast.getBody().accept(this, nothing);
        return null;
    }

    @Override
    public Object visit(IAST6instantiation iast, Object nothing) {
        for (IAST4expression e : iast.getArguments()) {
            e.accept(this, nothing);
        }
        return null;
    }

    @Override
    public Object visit(IAST6send iast, Object nothing) {
        iast.getReceiver().accept(this, nothing);
        for (IAST4expression e : iast.getArguments()) {
            e.accept(this, nothing);
        }
        return null;
    }

    @Override
    public Object visit(IAST6readField iast, Object nothing) {
        iast.getObject().accept(this, nothing); 
        return null;
    }

    @Override
    public Object visit(IAST6writeField iast, Object nothing) {
        iast.getObject().accept(this, nothing);
        iast.getValue().accept(this, nothing);
        return null;
    }

    @Override
    public Object visit(IAST6self iast, Object nothing) {
        return null;
    }

    @Override
    public Object visit(IAST6super iast, Object nothing) {
        return null;
    }

    

}
