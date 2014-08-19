package fr.upmc.ilp.ilp2.interfaces;

public interface IAST2while<Exc extends Exception>
extends IAST2instruction<Exc> {

    IAST2expression<Exc> getCondition ();
    IAST2instruction<Exc> getBody ();
}
