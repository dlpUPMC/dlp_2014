/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: CommonPlus.java 1243 2012-09-16 08:01:36Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.runtime;

import java.util.HashMap;
import java.util.Map;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp4.ast.CEASTglobalVariable;
import fr.upmc.ilp.ilp4.ast.CEASTprimitiveInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp6.ast.CEASTself;

/** Cette classe implante les caractéristiques
 * générales d'un interprète du langage ILP6.
 */

public class CommonPlus
extends fr.upmc.ilp.ilp4.runtime.CommonPlus
implements ICommon {

    public CommonPlus () {
        super();
        this.classMap = new HashMap<>();
        this.fillClassMap();
    }

  /** Rechercher une classe par son nom. */

  public ILPClass findClass (final String name)
  throws EvaluationException {
      ILPClass result = classMap.get(name);
      if ( result != null ) {
          return result;
      } else {
          throw new EvaluationException("No such class " + name);
      }
  }
  private final Map<String,ILPClass> classMap;

  /** Ajouter une classe. */

  public void addClass (final ILPClass clazz) {
      classMap.put(clazz.getName(), clazz);
  }

  /** Créer la classe Object et ses deux méthodes prédéfinies. la
   * première méthode est o.print() implantée par un appel à la
   * primitive print(value). La seconde méthode est o.classOf(), elle
   * n'est pas implantée car il faut créer toutes les autres classes
   * dans la bibliothèque d'exécution.
   */

  private void fillClassMap () {
    ILPmethod[] object_methods = new ILPmethod[2];
    CEASTself self = new CEASTself();
    object_methods[0] =
      new ILPmethod(
            "print",
            new IAST4variable[] { self.getLocalVariable() },
            new CEASTprimitiveInvocation(
                   new CEASTglobalVariable("print"),
                   new IAST4expression[] { self }));
    // On réalloue une variable self (pour les besoins de la normalisation):
    self = new CEASTself();
    object_methods[1] =
      new ILPmethod(
            "classOf",
            new IAST4variable[] { self.getLocalVariable() },
            new CEASTprimitiveInvocation(
                   new CEASTglobalVariable("classOf"),
                   new IAST4expression[] { self }));
    classMap.put("Object",
                 new ILPClass("Object",
                              null,           // pas de superclasse
                              new String[0],  // pas de champ
                              new String[] { "print", "classOf" },
                              object_methods));
  }

}

// fin de CommonPlus.java


