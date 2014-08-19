/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ConstantsStuff.java 930 2010-08-21 07:55:05Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;

import fr.upmc.ilp.ilp1.interfaces.IASTvariable;

/** Cette classe permet d'etendre un environnement lexical avec une definition
 * de la constante Pi.
 */

public class ConstantsStuff {
    
    public ConstantsStuff () {}

    /** Étendre un environnement lexical pour l'evaluation pour y 
     * installer la constante Pi. */

    public ILexicalEnvironment extendWithPredefinedConstants (
            final ILexicalEnvironment lexenv ) {
      final ILexicalEnvironment lexenv1 = lexenv.extend(
             new IASTvariable() {
               public String getName () {
                 return "pi";
               }
             }, 
             new Double(3.141592653589793238462643) );
      return lexenv1;
    }
    
    /* NOTA: ce serait bien de regrouper cette methode avec la precedente
     * mais cela induirait une dependance du paquetage runtime (pour 
     * l'evaluation (cf. cours2)) envers la compilation (cf. cours3) ce qui 
     * est inutile.  
     * 
     * Étendre un environnement lexical pour la compilation pour y 
     * installer la constante Pi.
    
    public static ICgenLexicalEnvironment extendWithPredefinedConstants (
            final ICgenLexicalEnvironment lexenv){
        final ICgenLexicalEnvironment lexenv1 = lexenv.extend(
                new IASTvariable () {
                    public String getName () {
                        return "pi";
                    }
                }, "ILP_PI");
        return lexenv1;
    }
    
    */

}
