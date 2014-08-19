package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;

public interface IAST6visitor<Data, Result, Exc extends Throwable> 
 extends IAST4visitor<Data, Result, Exc> {
    Result visit (IAST6classDefinition classDefinition, Data data) throws Exc;
    Result visit (IAST6methodDefinition methodDefinition, Data data) throws Exc;
    Result visit (IAST6instantiation expression, Data data) throws Exc;
    Result visit (IAST6send expression, Data data) throws Exc;
    Result visit (IAST6readField expression, Data data) throws Exc;
    Result visit (IAST6writeField expression, Data data) throws Exc;
    Result visit (IAST6self expression, Data data) throws Exc;
    Result visit (IAST6super expression, Data data) throws Exc;
}
