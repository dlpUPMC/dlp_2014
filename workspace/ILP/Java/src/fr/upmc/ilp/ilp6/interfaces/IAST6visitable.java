package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4visitable;

public interface IAST6visitable extends IAST4visitable {

    <Data, Result, Exc extends Throwable> Result accept (
            IAST6visitor<Data, Result, Exc> visitor, Data data) throws Exc;
}
