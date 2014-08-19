package fr.upmc.ilp.ilp3;

public interface IParser<Exc extends Exception>
extends fr.upmc.ilp.ilp2.interfaces.IParser<Exc> {
    IAST3Factory<Exc> getFactory ();
}
