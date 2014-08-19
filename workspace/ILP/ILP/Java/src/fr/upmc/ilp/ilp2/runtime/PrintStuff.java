/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:PrintStuff.java 405 2006-09-13 17:21:53Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp2.runtime;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICommon;

/** Les primitives pour imprimer à savoir print et newline. En fait,
 * newline pourrait se programmer à partir de print et de la chaîne
 * contenant une fin de ligne mais comme nous n'avons pas encore de
 * fonctions, elle est utile.
 */

public class PrintStuff {

    public PrintStuff () {
        this(new StringWriter());
    }
    public PrintStuff (Writer writer) {
        this.output = writer;
    }
    private Writer output;

    /** Renvoyer les caractères imprimés et remettre à vide le tampon
     * d'impression. */

    public synchronized String getPrintedOutput () {
        final String result = output.toString();
        return result;
    }

    /** Cette classe implante la fonction print() qui permet d'imprimer
     * une valeur. */

  private class PrintPrimitive extends InvokableImpl {
    private PrintPrimitive () {}
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
   * à la ligne. */

  private class NewlinePrimitive extends InvokableImpl {
    private NewlinePrimitive () {}
    // La fonction newline() est zéro-aire:
    @Override
    public Object invoke () {
        try {
            output.append("\n");
        } catch (IOException e) {}
        return Boolean.FALSE;
    }
  }

  public void extendWithPrintPrimitives (ICommon common)
      throws EvaluationException {
    common.bindPrimitive("print", new PrintPrimitive());
    common.bindPrimitive("newline", new NewlinePrimitive());
  }

}

// end of PrintStuff.java
