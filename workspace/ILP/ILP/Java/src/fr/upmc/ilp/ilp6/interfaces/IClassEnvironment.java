package fr.upmc.ilp.ilp6.interfaces;


/** Cette interface precise qu'un environnement travaillant avec des
 * classes doit pouvoir les memoriser et les rechercher.  */

public interface IClassEnvironment {

    /** Enregistrer une définition de classe. */

    public void addClassDefinition (IAST6classDefinition cd);

    /** Rechercher la définition d'une classe. Comme une classe peut manquer
     * aussi bien pendant la normalisation que la compilation (ou toute autre
     * phase) on signale ce probleme par une RuntimeException. */

    public IAST6classDefinition findClassDefinition (String className)
      throws RuntimeException;

}
