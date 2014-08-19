package fr.upmc.ilp.ilp1tme1;

import fr.upmc.ilp.ilp1.fromxml.ASTException;
import fr.upmc.ilp.ilp1.interfaces.IAST;

public interface IASTsequence extends fr.upmc.ilp.ilp1.interfaces.IASTsequence {
    IAST[] getAllButLastInstructions() throws ASTException;
}
