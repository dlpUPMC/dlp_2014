package fr.upmc.ilp.ilp2.interfaces;

public interface IAST2localBlock<Exc extends Exception>
extends IAST2instruction<Exc> {

    IAST2variable[] getVariables ();

    IAST2expression<Exc>[] getInitializations ();

    IAST2instruction<Exc> getBody ();
}
