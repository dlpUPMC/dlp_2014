/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id:ASTsequence.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;

import java.util.List;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.interfaces.IAST;
import fr.upmc.ilp.ilp1.interfaces.IASTsequence;

public class ASTsequence extends AST
implements IASTsequence {

    public ASTsequence(List<AST> instructions) {
        this.instructions = instructions;
    }
    private final List<AST> instructions;

    public IAST[] getInstructions() {
        return this.instructions.toArray(new IAST[0]);
    }

    // NOTA: si i est hors limite, cette methode ne ramene pas null comme le
    // dit l'interface mais signale une exception IndexOutOfRange.
    public @OrNull IAST getInstruction(int i) {
        return this.instructions.get(i);
    }

    public int getInstructionsLength() {
        return this.instructions.size();
    }

    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer("<sequence>");
        for (AST instruction : this.instructions) {
            sb.append(instruction.toXML());
        }
        sb.append("</sequence>");
        return sb.toString();
    }

}

// fin de ASTsequence.java
