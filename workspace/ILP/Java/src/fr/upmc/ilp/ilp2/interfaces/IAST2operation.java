package fr.upmc.ilp.ilp2.interfaces;

public interface IAST2operation<Exc extends Exception>
extends IAST2expression<Exc> {

    /** Renvoie le nom de l'opérateur concerné par l'opération. */
    String getOperatorName ();

    /** Renvoie l'arité de l'opérateur concerné par l'opération. L'arité
     * est toujours de 1 pour IAST2unaryOperation et de 2 pour une
     * IAST2binaryOperation. */
    int getArity ();

    /** Renvoie les opérandes d'une opération. */
    IAST2expression<Exc>[] getOperands ();
}
