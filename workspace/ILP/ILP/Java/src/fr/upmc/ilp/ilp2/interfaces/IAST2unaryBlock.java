package fr.upmc.ilp.ilp2.interfaces;


public interface IAST2unaryBlock<Exc extends Exception>
extends IAST2instruction<Exc> {

    IAST2variable getVariable ();

    IAST2expression<Exc> getInitialization ();

    IAST2instruction<Exc> getBody ();
}
