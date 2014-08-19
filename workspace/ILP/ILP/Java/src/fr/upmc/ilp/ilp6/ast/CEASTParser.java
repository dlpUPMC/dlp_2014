/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEASTParser.java 922 2010-08-18 14:42:09Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6program;
import fr.upmc.ilp.ilp6.interfaces.IParser6;

/** Transformer un document XML en un CEAST. */

public class CEASTParser
extends fr.upmc.ilp.ilp4.ast.CEASTParser
implements IParser6 {

    @Override
    public IAST6Factory getFactory () {
        return CEAST6.narrowToIAST6Factory(super.getFactory());
    }

    public CEASTParser (IAST6Factory factory) {
        super(factory);
        addParser("definitionClasse", CEASTclassDefinition.class);
        addParser("creationObjet", CEASTinstantiate.class);
        addParser("lectureChamp", CEASTreadField.class);
        addParser("envoiMessage", CEASTsend.class);
        addParser("ecritureChamp", CEASTwriteField.class);
        addParser("moi", CEASTself.class);
        addParser("appelSuper", CEASTsuper.class);
    }

    @Override
    public IAST6program parse (final Document d)
    throws CEASTparseException {
        final Element e = d.getDocumentElement();
        return CEASTprogram.parse(e, this);
    }
}

// end of CEASTParser.java
