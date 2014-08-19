package fr.upmc.ilp.ilp4mem;

import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4constant;

public class CEASTglobalConstant 
extends fr.upmc.ilp.ilp4.ast.CEASTglobalVariable
implements IAST4globalConstant {

    public CEASTglobalConstant(IAST4constant iast) {
        super("ilpCONSTANT" + (++counter));
        this.constant = iast;
    }
    private static int counter = 0;
    private final IAST4constant constant;
    
    public IAST4constant getConstant () {
        return this.constant;
    }
    
    /** Génération d'une déclaration et initialisation globale d'une variable globale. */

    @Override
    public void compileGlobalDeclaration (final StringBuffer buffer,
                                           final ICgenLexicalEnvironment lexenv,
                                           final ICgenEnvironment common)
      throws CgenerationException {
      buffer.append("static struct ILP_Object ");
      buffer.append(getMangledName());
      buffer.append("_object = {\n");
      buffer.append("  ._kind    = ILP_INTEGER_KIND, \n");
      buffer.append("  ._content = { \n");
      buffer.append("       .asInteger = ");
      buffer.append(this.constant.getValue());
      buffer.append(" } };\n");
      buffer.append("#define ");
      buffer.append(getMangledName());
      buffer.append(" (&");
      buffer.append(getMangledName());
      buffer.append("_object);\n");
    }
   
}
