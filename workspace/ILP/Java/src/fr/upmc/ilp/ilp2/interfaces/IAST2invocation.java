package fr.upmc.ilp.ilp2.interfaces;

public interface IAST2invocation<Exc extends Exception>
extends IAST2expression<Exc> {

    /** Renvoie la fonction invoquée. */
    IAST2expression<Exc> getFunction ();

    /** Renvoie les arguments de l'invocation sous forme d'une liste. */
    IAST2expression<Exc>[] getArguments ();

    /** Renvoie le nombre d'arguments de l'invocation. */
    int getArgumentsLength ();

    /** Renvoie le i-ème argument de l'invocation. */
    IAST2expression<Exc> getArgument (int i);
}
