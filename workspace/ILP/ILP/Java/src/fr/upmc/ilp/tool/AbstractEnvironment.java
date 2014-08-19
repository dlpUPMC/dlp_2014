package fr.upmc.ilp.tool;


/**
 * Une classe utilitaire pour les environnements: c'est une fonction
 * des variables vers les valeurs representee par une liste chainee.
 *
 * @param <V> la classe des variables
 * @param <W> la classe des valeurs
 */

public abstract class AbstractEnvironment<V, W> {

    public AbstractEnvironment(V variable, W value, AbstractEnvironment<V, W> next) {
        this.variable = variable;
        this.value = value;
        this.next = next;
    }
    private final V variable;
    private W value;
    private final AbstractEnvironment<V, W> next;

    public V getVariable () {
        return this.variable;
    }
    public W getValue () {
        return this.value;
    }
    public AbstractEnvironment<V, W> getNextEnvironment () {
        return this.next;
    }

    public W setValue (W newValue) {
        W oldValue = this.value;
        this.value = newValue;
        return oldValue;
    }

    /** Renvoie la valeur d'une variable si prÃ©sente dans
     * l'environnement. */
    public W lookup (V variable) {
        // NOTA: la comparaison s'effectue par equals():
        if (this.variable.equals(variable)) {
            return this.value;
        } else {
            return this.next.lookup(variable);
        }
    }

}

// fin d'AbstractEnvironment.java

