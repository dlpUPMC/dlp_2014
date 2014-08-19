/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ICgenEnvironment.java 872 2009-10-26 10:31:31Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.interfaces;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;

/** L'interface décrivant l'environnement des opérateurs prédéfinis du
 * langage à compiler vers C. Il est l'analogue de runtime/ICommon
 * pour le paquetage cgen. */

public interface ICgenEnvironment
extends fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment,
        IClassEnvironment {
    
  /** Rechercher l'index associé à une méthode. */

  public int getMethodOffset (String className, String methodName)
    throws CgenerationException;

  /** Rechercher la classe ayant introduit un champ.
   *
   * NOTA: C'est pour assurer qu'elle est unique que l'on restreint
   * les noms des champs. */

  public IAST6classDefinition findDefiningClassDefinition (String fieldName)
    throws CgenerationException;

  /** Mémoriser si un appel approprié à ILP_GenerateClass a déjà été
   * engendré. */

  public boolean alreadyGeneratedClassMacro (int methodCount);

  /** Engendrer l'objet représentant une méthode si elle n'a pas déjà
   * été engendrée! */

  public boolean alreadyGeneratedMethodObject (String methodName);

}

// end of ICgenEnvironment.java
