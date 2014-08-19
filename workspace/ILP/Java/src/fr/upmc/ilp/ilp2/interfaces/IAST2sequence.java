package fr.upmc.ilp.ilp2.interfaces;

public interface IAST2sequence<Exc extends Exception>
extends IAST2instruction<Exc> {

    /** Renvoie la séquence des instructions contenues. */
    IAST2instruction<Exc>[] getInstructions ();

    /** Renvoie le nombre d'instructions de la sequence. */
    int getInstructionsLength ();

    /** Renvoie la i-eme instruction.
     *
     * @throws Exc lorsqu'un tel argument n'existe pas. */
    IAST2instruction<Exc> getInstruction (int i) throws Exc;
}
