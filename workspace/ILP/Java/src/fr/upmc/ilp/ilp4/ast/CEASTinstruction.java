/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTinstruction.java 504 2006-10-10 11:45:04Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import fr.upmc.ilp.ilp4.interfaces.IAST4instruction;

/** 
 * Cette classe n'est là que pour les sous-classes n'ayant pas 
 * besoin de délégation ce qui n'est pas le cas des AST d'ILP4.
 */
public abstract class CEASTinstruction
extends CEASTexpression
implements IAST4instruction {}
