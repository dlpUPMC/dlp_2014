package fr.upmc.ilp.ilp1.fromxml;

import java.util.List;

import fr.upmc.ilp.ilp1.interfaces.IASTprogram;
import fr.upmc.ilp.ilp1.interfaces.IASTsequence;

public class ASTprogram extends AST implements IASTprogram {

    public ASTprogram (List<AST> body) {
        this.body = body;
    }
    protected List<AST> body;
    
    public IASTsequence getBody() {
        return new ASTsequence(this.body);
    }

    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<programme1>");
        // Juste pour simplifier les tests dans ASTParserTest:
        if ( this.body.size() == 1 ) {
            sb.append(this.body.get(0).toXML());
        } else {
            sb.append(new ASTsequence(this.body).toXML());
        }
        sb.append("</programme1>");
        return sb.toString();
    }
}
