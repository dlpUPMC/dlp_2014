package fr.upmc.ilp.ilp5.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4visitable;

public interface IAST5visitable extends IAST4visitable {

    <Data, Result, Exc extends Throwable> Result accept (
            IAST5visitor<Data, Result, Exc> visitor, Data data) throws Exc;
}
