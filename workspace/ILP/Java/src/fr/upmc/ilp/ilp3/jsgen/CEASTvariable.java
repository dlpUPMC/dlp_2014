/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTvariable.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp3.jsgen;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp3.IParser;
import fr.upmc.ilp.tool.CStuff;

public class CEASTvariable
implements IAST3variable {

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
        if ( that instanceof IAST3variable ) {
            IAST3variable iv = (IAST3variable) that;
            return this.name.equals(iv.getName());
        }
        return false;
    }
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public static IAST3variable parse (
            final Element e, 
            final IParser<CEASTparseException> parser)
    throws CEASTparseException {
        return (IAST3variable) parser.getFactory().newVariable(e.getAttribute("nom"));
    }

    /** Determiner la nature plus precise de la variable. */


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


}

// end of CEASTvariable.java
