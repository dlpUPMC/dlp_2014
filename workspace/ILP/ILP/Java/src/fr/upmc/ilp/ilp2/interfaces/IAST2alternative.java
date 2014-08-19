package fr.upmc.ilp.ilp2.interfaces;

public interface IAST2alternative<Exc extends Exception>
extends IAST2instruction<Exc> {

      /** Renvoie la condition. */
      IAST2expression<Exc> getCondition ();

      /** Renvoie la conséquence. */
      IAST2instruction<Exc> getConsequent ();

      /** Renvoie l'alternant si présent.
       *
       * @throws Exc lorsqu'un tel argument n'existe pas.
       */
      IAST2instruction<Exc> getAlternant () throws Exc;

      /** Indique si l'alternative est ternaire (qu'elle a un alternant). */
      boolean isTernary ();
}
