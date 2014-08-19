/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: ASTvariable.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1.fromxml;
import fr.upmc.ilp.ilp1.interfaces.*;

/** Description d'une variable. */

public class ASTvariable
  extends AST
  implements IASTvariable {

  public ASTvariable (String name) {
    this.name = name;
  }
  private final String name;

  public String getName () {
    return name;
  }

  @Override
  public String toXML () {
    return "<variable nom='" + name + "'/>";
  }

}

// end of ASTvariable.java
