/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: ILPexpression.java 505 2006-10-11 06:58:35Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Cette annotation, presente a l'execution, indique qu'une methode mene
 * a une variable ILP (ou a un tableau de variables) lue ou ecrite. Les
 * variables introduites par de nouvelles liaisons ne sont pas annotees.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface ILPvariable {

    /** Indique si la valeur obtenue par la methode peut etre null. */

    boolean neverNull () default true;

    /** Indique si la valeur obtenue est en fait un vecteur de variables. */

    boolean isArray() default false;
}
