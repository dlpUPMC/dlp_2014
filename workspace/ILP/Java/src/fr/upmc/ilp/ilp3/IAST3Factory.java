package fr.upmc.ilp.ilp3;

import fr.upmc.ilp.ilp2.interfaces.IAST2Factory;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;

public interface IAST3Factory<Exc extends Exception>
extends IAST2Factory<Exc> {

        IAST3try<Exc> newTry (
                IAST2instruction<Exc> body,
                IAST2variable caughtExceptionVariable,
                IAST2instruction<Exc> catcher,
                IAST2instruction<Exc> finallyer);
}
