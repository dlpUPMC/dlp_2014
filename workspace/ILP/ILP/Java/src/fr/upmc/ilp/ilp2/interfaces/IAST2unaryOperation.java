package fr.upmc.ilp.ilp2.interfaces;

public interface IAST2unaryOperation<Exc extends Exception>
extends IAST2operation<Exc> {

    IAST2expression<Exc> getOperand ();
}
