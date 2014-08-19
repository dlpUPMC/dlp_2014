/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:PrintStuff.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.runtime;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import fr.upmc.ilp.ilp1.interfaces.IASTvariable;

/** les primitives pour imprimer à savoir print et newline. En fait,
 * newline pourrait se programmer à partir de print et de la chaîne
 * contenant une fin de ligne mais comme nous n'avons pas encore de
 * fonctions, elle est utile.
 */

public class PrintStuff {

    private Writer output;
      
    public PrintStuff () {
        this(new StringWriter());
    }
    public PrintStuff (Writer writer) {
        this.output = writer;
    }

    /** Renvoyer les caractères imprimés. */
    
    public synchronized String getPrintedOutput () {
        final String result = output.toString();
        return result;
    }
    
    /** étendre un environnement lexical pour l'evaluation pour y installer 
     * les primitives print() et newline(). */
    
    public ILexicalEnvironment
    extendWithPrintPrimitives (final ILexicalEnvironment lexenv) {
        final ILexicalEnvironment lexenv1 = lexenv.extend(
                new IASTvariable() {
                    public String getName () {
                        return "print";
                    }
                }, new PrintPrimitive());
        final ILexicalEnvironment lexenv2 = lexenv1.extend(
                new IASTvariable() {
                    public String getName () {
                        return "newline";
                    }
                }, new NewlinePrimitive());
        return lexenv2;
    }
    
    // Cf. NOTA de ConstantsStuff pour l'extension de l'environnement de
    // compilation.
    
    /** Cette classe implante la fonction print() qui permet d'imprimer
     * une valeur. Elle ne se soucie pas du succès de l'opération! */
    
    protected class PrintPrimitive extends AbstractInvokableImpl {
        public PrintPrimitive () {}
        // La fonction print() est unaire:
        @Override
        public Object invoke (Object value) {
            try {
                output.append(value.toString());
            } catch (IOException e) {}
            return Boolean.FALSE;
        }
    }
    
    /** Cette classe implante la fonction newline() qui permet de passer
     * à la ligne. Elle ne se soucie pas du succès de l'opération! */
    
    protected class NewlinePrimitive extends AbstractInvokableImpl {
        public NewlinePrimitive () {}
        // La fonction newline() est zéro-aire:
        @Override
        public Object invoke () {
            try {
                output.append("\n");
            } catch (IOException e) {}
            return Boolean.FALSE;
        }
    }
}

// end of PrintStuff.java
