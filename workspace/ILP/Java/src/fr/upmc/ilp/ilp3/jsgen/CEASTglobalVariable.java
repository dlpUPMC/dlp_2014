/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTglobalVariable.java 1189 2011-12-18 22:09:10Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp3.jsgen;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;


/** Les variables globales.  */

public class CEASTglobalVariable
extends CEASTvariable
implements IAST3globalVariable {

  
  public CEASTglobalVariable (final String name) {
	  super (name);
  }

  /** Génération de variables globales utilitaires. Leur nom en C
   * débute par le préfixe "ilp" afin de ne pas perturber les
   * variables du langage ILP.*/

  public static synchronized CEASTglobalVariable generateGlobalVariable () {
    return new CEASTglobalVariable("ilpGLOBAL");
  }

  /** Une fois normalisee, une variable reste normalisee. */

 
  @Override
  public Object eval (final ILexicalEnvironment lexenv,
                       final ICommon common)
    throws EvaluationException {
    return common.globalLookup(this);
  }

  /** Compilation d'une variable en C pour en obtenir sa valeur. */
  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common,
                       final IDestination destination)
    throws CgenerationException {
    destination.compile(buffer, lexenv, common);
    buffer.append(getMangledName());
  }

  /** Tentative de génération d'une déclaration locale pour une
   * variable globale. 
 * @throws CgenerationException */
  //NOTE: cette methode ne devrait jamais etre invoquee. Modifier les
  // interfaces IGlobalVariable pour que ce soit le cas.
  @Override
  public void compileDeclaration (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common) 
  throws CgenerationException {
    final String msg = "Cannot locally declare global variable: " + getName();
    throw new CgenerationException(msg);
  }

  /** Génération d'une déclaration globale d'une variable globale. */

  public void compileGlobalDeclaration (final StringBuffer buffer,
                                        final ICgenLexicalEnvironment lexenv,
                                        final ICgenEnvironment common)
    throws CgenerationException {
    buffer.append("static ILP_Object ");
    buffer.append(getMangledName());
    buffer.append(";\n");
  }
  

}

// end of CEASTglobalVariable.java
