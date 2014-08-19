/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id:EASTsequence.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.eval;

import java.util.List;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.interfaces.IASTsequence;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.ICommon;
import fr.upmc.ilp.ilp1.runtime.ILexicalEnvironment;

/** Les sequences d'instructions. Les grammaires imposent normalement
 * qu'il y a au moins une instruction dans une sequence.  */

public class EASTsequence extends EAST
implements IASTsequence {

    public EASTsequence (List<EAST> instructions) {
        this.instruction = instructions.toArray(new EAST[0]);
    }
    protected EAST[] instruction;

    public EAST[] getInstructions () {
        return this.instruction;
    }
    public @OrNull EAST getInstruction (int i) {
        return this.instruction[i];
    }
    public int getInstructionsLength () {
        return this.instruction.length;
    }

    /**
     * L'évaluation d'une séquence passe par celle, ordonnée, de toutes
     * les instructions qu'elle contient.
     *
     * NOTA: inutile de se compliquer la vie, Java ne supporte pas la
     * récursion terminale.
     * NOTA2: Le cas de la séquence vide est prévu.
     */

    @Override
    public Object eval (ILexicalEnvironment lexenv, ICommon common)
    throws EvaluationException {
        Object last = EAST.voidConstantValue();
        for ( int i = 0 ; i < instruction.length ; i++ ) {
            last = instruction[i].eval(lexenv, common);
        }
        return last;
    }

}

//end of EASTsequence.java
