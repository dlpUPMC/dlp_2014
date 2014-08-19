/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ILPClass.java 1243 2012-09-16 08:01:36Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.runtime;

import java.util.HashMap;
import java.util.Map;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICommon;

/** Une classe pour ILP6. */

public class ILPClass {

  public ILPClass (final String className,
                   final ILPClass superClass,
                   final String[] fieldName,
                   final String[] methodName,
                   final ILPmethod[] method) {
    this.className = className;
    this.superClass = superClass;
    if ( superClass != null ) {
      this.fieldName = new String[superClass.fieldName.length + fieldName.length];
      for ( int i = 0 ; i<superClass.fieldName.length ; i++ ) {
        this.fieldName[i] = superClass.fieldName[i];
      }
      for ( int i = 0 ; i<fieldName.length ; i++ ) {
        this.fieldName[superClass.fieldName.length + i] = fieldName[i];
      }
    } else {
      // Il s'agit de créer la class Object!
      this.fieldName = fieldName;
    }
    assert method.length == methodName.length;
    this.method = new HashMap<>();
    for ( int i = 0 ; i<method.length ; i++ ) {
      this.method.put(methodName[i], method[i]);
      method[i].setDefiningClass(this);
    }
  }
  private final String className;
  private final ILPClass superClass;
  // Tous les champs y compris ceux hérités:
  private final String[] fieldName;
  // Seules les méthodes propres:
  private final Map<String,ILPmethod> method;

  /** Renvoyer le nom de la classe. */

  public String getName () {
    return this.className;
  }
  public ILPClass getSuperClass () {
      return this.superClass;
  }

  /** Renvoyer le nom du rank-ieme champ. */

  public String fieldName (int rank) {
    return this.fieldName[rank];
  }

  /** Indiquer le nombre total de champs (propres ainsi qu'hérité)
   * d'une instance de cette classe. */

  public int fieldSize () {
    return this.fieldName.length;
  }

  /** Calculer le rang d'un champ. */

  public int fieldRank (final String name)
    throws EvaluationException {
    for ( int i = 0 ; i<fieldName.length ; i++ ) {
      if ( fieldName[i].equals(name) ) {
        return i;
      }
    }
    throw new EvaluationException("No such field " + name);
  }

  /** Retourner la methode propre ayant un certain nom. */

  public ILPmethod getMethodByName (final String methodName)
  throws EvaluationException {
      ILPmethod method = this.method.get(methodName);
      if ( method != null ) {
          return method;
      } else {
          final String msg = "No such method " + methodName;
          throw new EvaluationException(msg);
      }
  }

  /** Envoyer un message à une instance. */

  public Object send (final ILPinstance self,
                      final String message,
                      final Object[] argument,
                      final ICommon common)
    throws EvaluationException {
    ILPmethod m = this.method.get(message);
    if ( m != null ) {
      final Object[] newArgument = new Object[1 + argument.length];
      newArgument[0] = self;
      for ( int i = 0 ; i<argument.length ; i++ ) {
        newArgument[i+1] = argument[i];
      }
      return m.invoke(newArgument, common);
    } else {
      // Pas de méthode propre de ce nom!
      if ( superClass != null ) {
        return superClass.send(self, message, argument, common);
      } else {
        // On est sur Object
        throw new EvaluationException("No such method " + message);
      }
    }
  }

  /** Envoyer un message à un bloc d'arguments. */

  public Object send (final String message,
                      final Object[] argument,
                      final ICommon common)
  throws EvaluationException {
      ILPmethod m = this.method.get(message);
      if ( m != null ) {
          return m.invoke(argument, common);
      } else {
          // Pas de méthode propre de ce nom!
          if ( superClass != null ) {
              return superClass.send(message, argument, common);
          } else {
              // On est sur Object
              throw new EvaluationException("No such method " + message);
          }
      }
  }

  /** Fabriquer un vecteur de champs (représentés par des chaînes) en
   * ajoutant de nouveaux champs à ceux de la classe. */

  protected String[] extend (final String[] properFieldName) {
    final int count = fieldName.length + properFieldName.length;
    final String[] result = new String[count];
    for ( int i = 0 ; i<fieldName.length ; i++ ) {
      result[i] = fieldName[i];
    }
    for ( int i = 0 ; i<properFieldName.length ; i++ ) {
      result[fieldName.length + i] = properFieldName[i];
    }
    return result;
  }

}

// end of ILPClass.java
