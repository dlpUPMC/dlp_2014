/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CgenEnvironment6.java 1243 2012-09-16 08:01:36Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.cgen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;

/** La représentation de l'environnement des opérateurs prédéfinis. Il
 * définit comment les compiler. C'est un peu l'analogue de
 * runtime/Common pour le paquetage cgen. */

public class CgenEnvironment6
extends fr.upmc.ilp.ilp2.cgen.CgenEnvironment
implements ICgenEnvironment {

  public CgenEnvironment6 () {
      super();
      alreadyGeneratedClassMacroSet = new HashSet<>();
      alreadyGeneratedMethodObjectSet = new HashSet<>();
      alreadyGeneratedMethodObjectSet.add("print");
      alreadyGeneratedMethodObjectSet.add("classOf");
  }

  /** Enregistrer une définition de classe ainsi que ses champs propres. */

  public void addClassDefinition (final IAST6classDefinition cd) {
    classMap.put(cd.getName(), cd);
    final String[] fieldName = cd.getProperFieldNames();
    for ( int i = 0 ; i<fieldName.length ; i++ ) {
      fieldMap.put(fieldName[i], cd);
    }
  }
  protected Map<String,IAST6classDefinition> classMap = new HashMap<>();
  protected Map<String,IAST6classDefinition> fieldMap = new HashMap<>();

  /** Rechercher la définition d'une classe. */

  public IAST6classDefinition findClassDefinition (final String className) {
    IAST6classDefinition result = classMap.get(className);
    if ( result != null ) {
      return result;
    } else {
      final String msg = "No such class " + className;
      throw new RuntimeException(msg);
    }
  }

  /** Rechercher l'offset associé à une méthode.
   *
   * Attention, l'offset peut être le même pour des méthodes n'ayant
   * rien à voir mais étant défini dans des sous-classes sœurs. */

  public int getMethodOffset (final String className,
                              final String methodName)
    throws CgenerationException {
    final IAST6classDefinition cd = classMap.get(className);
    if ( cd == null ) {
      final String msg = "No such class " + className;
      throw new CgenerationException(msg);
    } else {
      return cd.getMethodOffset(methodName, this);
    }
  }

  /** Rechercher la classe ayant introduit un champ. */

  public IAST6classDefinition findDefiningClassDefinition (String fieldName)
    throws CgenerationException {
    final IAST6classDefinition cd = fieldMap.get(fieldName);
    if ( cd != null ) {
      return cd;
    } else {
      final String msg = "No defining class for " + fieldName;
      throw new CgenerationException(msg);
    }
  }

  /** Engendrer la définition d'un type de classe avec un certain
   * nombre de méthodes si elle n'a pas déjà été engendrée! */

  public boolean alreadyGeneratedClassMacro (final int methodCount) {
        return !alreadyGeneratedClassMacroSet.add(methodCount);
  }
  private final Set<Integer> alreadyGeneratedClassMacroSet;

  /** Engendrer l'objet représentant une méthode si elle n'a pas déjà
   * été engendrée! */

  public boolean alreadyGeneratedMethodObject (final String methodName) {
    return !alreadyGeneratedMethodObjectSet.add(methodName);
  }
  private final Set<String> alreadyGeneratedMethodObjectSet;

}

// end of CgenEnvironment.java
