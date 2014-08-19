/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTvariable.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;
import fr.upmc.ilp.tool.CStuff;

public class CEASTvariable
implements IAST4variable {

    public CEASTvariable (final String name) {
        this.name = name;
        synchronized (this) {
            if (    name.startsWith("ilp_")
                 || name.startsWith("ILP_") ) {
                this.mangledName = name;
            } else {
                counter++;
                this.mangledName = CStuff.mangle(this.name)
                    + "_" + counter;
            }
        }
    }
    final String name;
    final String mangledName;
    private static int counter = 0;
    
    public CEASTvariable (final String name, final String mangledName) {
        this.name = name;
        this.mangledName = mangledName;
    }

    public String getName () {
        return this.name;
    }
    public String getMangledName () {
        return this.mangledName;
    }
    
    @Override
    public boolean equals (final Object that) {
        if ( that instanceof IAST4variable ) {
            IAST4variable iv = (IAST4variable) that;
            return this.name.equals(iv.getName());
        }
        return false;
    }
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public static IAST2variable parse (final Element e, final IParser parser)
    throws CEASTparseException {
        return parser.getFactory().newVariable(e.getAttribute("nom"));
    }

    /** Determiner la nature plus precise de la variable. */

    public IAST4variable normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
    throws NormalizeException {
        // Les variables locales ont precedence:
        final IAST4variable lv = lexenv.isPresent(this);
        if ( lv != null ) {
            return lv;
        }
        // puis les globales:
        final IAST4globalVariable gv = common.isPresent(this);
        if ( gv != null ) {
            return gv;
        }
        // Sinon c'est une globale:
        final IAST4globalVariable global = factory.newGlobalVariable(getName());
        common.add(global);
        return global;
    }
    // NOTE: les globalVariable peuvent aussi etre raffinees en
    // globalFunctionVariable.

    public Object eval (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException {
        final String msg = "Should not evaluate vanilla variable!";
        throw new EvaluationException(msg);
    }

    public void compileDeclaration(
            StringBuffer buffer,
            ICgenLexicalEnvironment lexenv,
            ICgenEnvironment common )
    throws CgenerationException {
        final String msg = "No compileDeclaration on vanilla variable!";
        throw new CgenerationException(msg);
    }

    public <Data, Result, Exc extends Throwable> Result 
      accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
        return visitor.visit(this, data);
    }
}

// end of CEASTvariable.java
