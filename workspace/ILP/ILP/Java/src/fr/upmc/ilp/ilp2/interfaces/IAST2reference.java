package fr.upmc.ilp.ilp2.interfaces;

/** Cette interface décrit une référence en lecture à une variable
 * car on distingue maintenant lecture et référence à une variable. */

public interface IAST2reference<Exc extends Exception>
extends IAST2expression<Exc> {

    /** Retourne la variable lue */
    IAST2variable getVariable();
}
