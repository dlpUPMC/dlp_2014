/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2009 <Christian.Queinnec@lip6.fr>
 * $Id$
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/** Cette annotation precise que la valeur attendue peut aussi etre null.
 * Attention null instanceof AnyClass est toujours vrai!
 * Cette annotation pourrait servir a des outils d'analyse statique de code.
 */

@Documented
@Inherited
@Target({ElementType.METHOD,
         ElementType.PARAMETER,
         ElementType.FIELD})
public @interface OrNull {}
