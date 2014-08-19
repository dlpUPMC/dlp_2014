package fr.upmc.ilp.ilp2.interfaces;

public interface IAST2constant<Exc extends Exception>
extends IAST2expression<Exc> {

    /** La description precise et sans perte de la constante. */
    String getDescription();

    /** Une valeur Java representant une constante ILP sans garantie
     * d'exactitude. Utile pour l'interpretation! */
    Object getValue ();
}
