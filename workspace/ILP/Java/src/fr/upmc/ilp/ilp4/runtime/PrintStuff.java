/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: PrintStuff.java 869 2009-10-23 16:48:43Z queinnec $
 * GPL version>=2
 * ******************************************************************/
package fr.upmc.ilp.ilp4.runtime;

import fr.upmc.ilp.ilp1.runtime.AbstractInvokableImpl;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;

/** Les primitives pour imprimer à savoir print et newline. En fait,
 * newline pourrait se programmer à partir de print et de la chaîne
 * contenant une fin de ligne mais comme nous n'avons pas encore de
 * fonctions, elle est utile.
 */
public class PrintStuff {

    public PrintStuff() {
        this.output = new StringBuffer();
    }
    private final StringBuffer output;
    
    /** On peut aussi imposer le flux de sortie. 
     * 
     * MOCHE: devrait plutot etre un flux qu'un tampon. */
    public PrintStuff (StringBuffer output) {
        this.output = output;
    }

    /** Renvoyer les caractères imprimés et remettre à vide le tampon
     * d'impression. */
    public synchronized String getPrintedOutput() {
        final String result = output.toString();
        output.delete(0, output.length());
        return result;
    }

    public void extendWithPrintPrimitives(final ICommon common)
        throws EvaluationException {
        common.bindPrimitive("print", new PrintPrimitive());
        common.bindPrimitive("newline", new NewlinePrimitive());
    }

    /** Cette classe implante la fonction print() qui permet d'imprimer
     * une valeur. */
    private class PrintPrimitive extends AbstractInvokableImpl {
        private PrintPrimitive() {}

        // La fonction print() est unaire:
        @Override
        public Object invoke (Object value) {
            output.append(value.toString());
            return Boolean.FALSE;
        }
    }

    /** Cette classe implante la fonction newline() qui permet de passer
     * à la ligne. */
    private class NewlinePrimitive extends AbstractInvokableImpl {
        private NewlinePrimitive() {}

        // La fonction newline() est zéro-aire:
        @Override
        public Object invoke () {
            output.append("\n");
            return Boolean.FALSE;
        }
    }
}

// end of PrintStuff.java
