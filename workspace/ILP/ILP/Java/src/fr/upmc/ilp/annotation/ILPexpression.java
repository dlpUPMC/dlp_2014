/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: ILPexpression.java 1213 2012-08-27 15:09:51Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Cette annotation, présente à l'exécution, indique qu'une méthode mène
 * à une expression ILP. Divers sous-attributs caractérisent mieux cette
 * annotation.
 *
 * Cas particuliers: <ul>
 * <li> getOperands() n'est pas annoté (ce serait redondant
 *      avec get*Operand()) </li>
 * </ul> */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface ILPexpression {

    /** Indique si la valeur obtenue par la methode peut être null. */

    boolean neverNull () default true;

    /** Indique si la valeur obtenue est en fait un vecteur d'expressions. */

    boolean isArray () default false;
}
