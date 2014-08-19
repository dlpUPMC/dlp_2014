package fr.upmc.ilp.ilp2.interfaces;

public interface IAST2primitiveInvocation<Exc extends Exception>
extends IAST2invocation<Exc> {
    String getPrimitiveName ();
}
