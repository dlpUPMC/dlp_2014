/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:ASToperationBinaire.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;

import java.util.List;
import java.util.Vector;

import fr.upmc.ilp.ilp1.interfaces.IAST;
import fr.upmc.ilp.ilp1.interfaces.IASTbinaryOperation;

/** Opérations binaires. */

public class ASTbinaryOperation extends ASToperation
implements IASTbinaryOperation {

    public ASTbinaryOperation(String operateur, AST operandeGauche,
            AST operandeDroit) {
        super(operateur, 2);
        this.operandeGauche = operandeGauche;
        this.operandeDroit = operandeDroit;
    }
    private final AST operandeGauche;
    private final AST operandeDroit;

    public IAST getLeftOperand() {
        return this.operandeGauche;
    }

    public IAST getRightOperand() {
        return this.operandeDroit;
    }

    public IAST[] getOperands() {
        // On calcule paresseusement car ce n'est pas une méthode usuelle:
        if (operands == null) {
            List<AST> loperands = new Vector<>();
            loperands.add(operandeGauche);
            loperands.add(operandeDroit);
            operands = loperands.toArray(new AST[0]);
        }
        return operands;
    }

    // NOTA: remarquer que ce mode paresseux interdit de qualifier ce
    // champ de « final » ce qui n'est pas sûr!
    private IAST[] operands;

    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        // Comme signalé par Olivier.Tran@etu.upmc.fr, il faudrait coder
        // getOperatorName() pour utiliser les entités &lt; &gt; etc.
        sb.append("<operationBinaire operateur='" + getOperatorName() + "'>");
        sb.append("<operandeGauche>");
        sb.append(operandeGauche.toXML());
        sb.append("</operandeGauche><operandeDroit>");
        sb.append(operandeDroit.toXML());
        sb.append("</operandeDroit></operationBinaire>");
        return sb.toString();
    }

}

// fin de ASToperationBinaire.java
