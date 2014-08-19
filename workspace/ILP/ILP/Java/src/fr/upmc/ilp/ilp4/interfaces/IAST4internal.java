/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:IAST4.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.interfaces;

import java.util.Set;

/** Cette interface definit quelques methodes utilitaires non 
 * destinees a un usage externe. */

public interface IAST4internal extends IAST4 {
   
    /** Indiquer que d'autres fonctions sont invoquees. Renvoie vrai lorsque
     * de nouvelles fonctions ont ete ajoutees qui n'etaient pas encore 
     * presentes (comme la methode Set.addAll()) */
    
    boolean addInvokedFunctions (Set<IAST4globalFunctionVariable> others);

}
