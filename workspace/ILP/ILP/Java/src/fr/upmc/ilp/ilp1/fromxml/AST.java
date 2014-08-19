/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: AST.java 768 2008-11-02 15:56:00Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import fr.upmc.ilp.ilp1.interfaces.*;

public abstract class AST implements IAST {

  /** Décrit l'AST en XML (surtout utile pour la mise au point).
   *
   * NOTA: cette signature conduit à une implantation naïve
   * déraisonnablement coûteuse! Il vaudrait mieux se trimballer un
   * unique StringBuffer dans lequel concaténer les fragments XML.
   */

  public abstract String toXML ();

}

// fin de AST.java
