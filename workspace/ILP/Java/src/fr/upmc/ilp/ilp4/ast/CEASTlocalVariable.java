/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTlocalVariable.java 1241 2012-09-12 17:14:26Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.IDestination;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

/** Les variables: leur interprétation et compilation. Attention:
 * cette classe est raffinée en plusieurs sous-classes de comportement
 * légérement différents: les variables globales et prédéfinies. */

public class CEASTlocalVariable
  extends CEASTvariable
  implements IAST4localVariable {

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

  @Override
  public IAST4variable normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST4Factory factory )
  throws NormalizeException {
      final IAST4variable lv = lexenv.isPresent(this);
      if ( lv != null ) {
          return lv;
      } else {
          final IAST4globalVariable global = 
                  factory.newGlobalVariable(getName());
          common.add(global);
          return global;
      }
  }

  /** Interprétation d'une référence à une variable locale. */

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
    throws CgenerationException {
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

  @Override
  public <Data, Result, Exc extends Throwable> Result 
    accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
}

// end of CEASTlocalVariable.java
