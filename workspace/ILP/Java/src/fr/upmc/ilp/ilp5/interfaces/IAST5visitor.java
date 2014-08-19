package fr.upmc.ilp.ilp5.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;

public interface IAST5visitor<Data, Result, Exc extends Throwable>
 extends IAST4visitor<Data, Result, Exc> {
    Result visit (IAST5codefinedLocalFunctions iast, Data data) throws Exc;
    Result visit (IAST5localFunctionDefinition iast, Data data) throws Exc;
    Result visit (IAST5localFunctionVariable iast, Data data) throws Exc;
    // Et on ajoute tous les types de variables:
    Result visit (IAST4globalVariable v, Data data) throws Exc;
    Result visit (IAST4globalFunctionVariable v, Data data) throws Exc;
    //Result visit (IAST4localVariable v, Data data) throws Exc;
}
