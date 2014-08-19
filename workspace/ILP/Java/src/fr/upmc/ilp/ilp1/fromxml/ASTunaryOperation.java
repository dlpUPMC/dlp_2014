/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ASToperationUnaire.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;

import java.util.List;
import java.util.Vector;

import fr.upmc.ilp.ilp1.interfaces.IAST;
import fr.upmc.ilp.ilp1.interfaces.IASTunaryOperation;

/** Opérations unaires. */

public class ASTunaryOperation extends ASToperation
implements IASTunaryOperation {

    public ASTunaryOperation(String operateur, AST operand) {
        super(operateur, 1);
        this.operand = operand;
    }
    private final AST operand;

    public IAST getOperand() {
        return this.operand;
    }

    public IAST[] getOperands() {
        // On calcule paresseusement car ce n'est pas une méthode usuelle:
        if (operands == null) {
            List<AST> loperands = new Vector<>();
            loperands.add(operand);
            operands = loperands.toArray(new AST[0]);
        }
        return operands;
    }
    private AST[] operands = null;

    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<operationUnaire operateur='" + getOperatorName() + "'>");
        sb.append("<operande>");
        sb.append(operand.toXML());
        sb.append("</operande></operationUnaire>");
        return sb.toString();
    }

}

// end of ASToperationUnaire.java
