package fr.upmc.ilp.ilp5.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IParser;

public interface IParser5<Exc extends Exception>
extends IParser {
    IAST5Factory getFactory ();
}
