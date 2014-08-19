/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: AbstractExplicitVisitor.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.interfaces;


/**
 * Ce visiteur abstrait parcourt toutes les expressions
 * d'un AST. On aurait pu aussi le definir en utilisant les capacites
 * reflexives de Java (a faire en AbstractReflectiveVisitor). Ce visiteur
 * prend en entrée (Data) ce qu'il rend en sortie (Data): c'est souvent 
 * utile mais les sous-classes peuvent très bien ignorer l'un ou l'autre.
 * 
 * Attention, ce visiteur ne visite pas les variables.
 */

public abstract class AbstractExplicitVisitor<Data, Exc extends Throwable>
implements IAST4visitor<Data, Data, Exc> {

    public Data visit (IAST4alternative iast, Data data) throws Exc {
        iast.getCondition().accept(this, data);
        iast.getConsequent().accept(this, data);
        if ( null != iast.getAlternant() ) {
            iast.getAlternant().accept(this, data);
        }
        return data;
    }

    public Data visit (IAST4assignment iast, Data data) throws Exc {
        iast.getValue().accept(this, data);
        return data;
    }
   public Data visit (IAST4localAssignment iast, Data data) throws Exc {
        iast.getValue().accept(this, data);
        return data;
    }
    public Data visit (IAST4globalAssignment iast, Data data) throws Exc {
        iast.getValue().accept(this, data);
        return data;
    }

    public Data visit (IAST4constant iast, Data data) throws Exc {
        // pas de sous-expression!
        return data;
    }

    public Data visit (IAST4invocation iast, Data data) throws Exc {
        iast.getFunction().accept(this, data);
        for ( IAST4expression arg : iast.getArguments() ) {
            arg.accept(this, data);
        }
        return data;
    }
    public Data visit (IAST4computedInvocation iast, Data data) throws Exc {
        iast.getFunction().accept(this, data);
        for ( IAST4expression arg : iast.getArguments() ) {
            arg.accept(this, data);
        }
        return data;
    }
    public Data visit (IAST4globalInvocation iast, Data data) throws Exc {
        for ( IAST4expression arg : iast.getArguments() ) {
            arg.accept(this, data);
        }
        return data;
    }
    public Data visit (IAST4primitiveInvocation iast, Data data) throws Exc {
        for ( IAST4expression arg : iast.getArguments() ) {
            arg.accept(this, data);
        }
        return data;
    }

    public Data visit (IAST4functionDefinition iast, Data data) throws Exc {
        iast.getBody().accept(this, data);
        return data;
    }

    public Data visit (IAST4reference iast, Data data) throws Exc {
        return data;
    }
    
    public Data visit (IAST4localBlock iast, Data data) throws Exc {
        for ( IAST4expression init : iast.getInitializations() ) {
            init.accept(this, data);
        }
        iast.getBody().accept(this, data);
        return data;
    }

    public Data visit (IAST4unaryBlock iast, Data data) throws Exc {
        iast.getInitialization().accept(this, data);
        iast.getBody().accept(this, data);
        return data;
    }

    public Data visit (IAST4binaryOperation iast, Data data) throws Exc {
        iast.getLeftOperand().accept(this, data);
        iast.getRightOperand().accept(this, data);
        return data;
    }

    public Data visit (IAST4unaryOperation iast, Data data) throws Exc {
        iast.getOperand().accept(this, data);
        return data;
    }

    public Data visit (IAST4program iast, Data data) throws Exc {
        for ( IAST4functionDefinition fun : iast.getFunctionDefinitions() ) {
            fun.accept(this, data);
        }
        iast.getBody().accept(this, data);
        return data;
    }

    public Data visit (IAST4sequence iast, Data data) throws Exc {
        for ( IAST4expression expr : iast.getInstructions() ) {
            expr.accept(this, data);
        }
        return data;
    }

    public Data visit (IAST4try iast, Data data) throws Exc {
        iast.getBody().accept(this, data);
        if ( null != iast.getCatcher() ) {
            iast.getCatcher().accept(this, data);
        }
        if ( null != iast.getFinallyer() ) {
            iast.getFinallyer().accept(this, data);
        }
        return data;
    }

    public Data visit (IAST4while iast, Data data) throws Exc {
        iast.getCondition().accept(this, data);
        iast.getBody().accept(this, data);
        return data;
    }
}
