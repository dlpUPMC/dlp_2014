package fr.upmc.ilp.ilp3.jsgen;

/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTlocalVariable.java 1241 2012-09-12 17:14:26Z queinnec $
 * GPL version>=2
 * ******************************************************************/


import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;



/** Les variables: leur interprétation et compilation. Attention:
 * cette classe est raffinée en plusieurs sous-classes de comportement
 * légérement différents: les variables globales et prédéfinies. */

public class CEASTlocalVariable
  extends CEASTvariable
  implements IAST3localVariable {

  /** Créer une variable avec un certain nom. Le nom peut être modifié
   * afin de se conformer à C, il faut donc toujours demander le nom
   * de la variable plutôt que de le supposer.  */

public CEASTlocalVariable (final String name) {
		    super(name);
		  }
  

  /** Génération de variables temporaires. Leur nom en C débute par le
   * préfixe "ilp" afin de ne pas perturber les variables du langage
   * ILP. */

  public static synchronized CEASTlocalVariable generateVariable () {
    return new CEASTlocalVariable("ilpLOCAL");
  }

  /** Une fois normalisee, une variable reste normalisee. */
  
  public IAST3globalVariable newGlobalVariable(String name) {
      return new CEASTglobalVariable(name);
  }
 
  @Override
  public Object eval (final ILexicalEnvironment lexenv,
                      final ICommon common)
    throws EvaluationException {
    return lexenv.lookup(this);
  }

  /** Compilation en C d'une référence à une variable locale. */

  public void compile (final StringBuffer buffer,
                       final ICgenLexicalEnvironment lexenv,
                       final ICgenEnvironment common,
                       final IDestination destination)
    throws CgenerationException {
    destination.compile(buffer, lexenv, common);
    buffer.append(getMangledName());
  }

  /** Génération d'une déclaration introduisant une variable locale. */

  @Override
  public void compileDeclaration (final StringBuffer buffer,
                                  final ICgenLexicalEnvironment lexenv,
                                  final ICgenEnvironment common)
{
    buffer.append("ILP_Object ");
    buffer.append(getMangledName());
    buffer.append(";\n");
  }

  /** Tentative de génération d'une déclaration globale. Une variable locale
   * n'est pas globale. */

  public void compileGlobalDeclaration (final StringBuffer buffer,
                                        final ICgenLexicalEnvironment lexenv,
                                        final ICgenEnvironment common)
    throws CgenerationException {
    final String msg = "Non global variable " + getName();
    throw new CgenerationException(msg);
  }


}

// end of CEASTlocalVariable.java
