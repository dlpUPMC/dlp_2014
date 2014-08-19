package fr.upmc.ilp.ilp4mem;

import org.w3c.dom.Element;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2integer;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4reference;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IParser;

public class CEASTinteger extends fr.upmc.ilp.ilp4.ast.CEASTinteger {

    public CEASTinteger(String value) {
        super(value);
    }

    public static IAST2integer<CEASTparseException> parse (
            final Element e, final IParser parser)
    throws CEASTparseException {
        return fr.upmc.ilp.ilp2.ast.CEASTinteger.parse(e, parser);
    }
    
    @Override
    public IAST4expression normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
    throws NormalizeException {
        if ( common instanceof INormalizeConstantEnvironment ) {
            INormalizeConstantEnvironment ince = (INormalizeConstantEnvironment) common;
            IAST4globalConstant gv = ince.add(this);
            IAST4reference reference = factory.newReference(gv);
            return reference;
        } else {
            return this;
        }
    }
}
