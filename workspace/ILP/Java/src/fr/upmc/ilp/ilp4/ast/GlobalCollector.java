package fr.upmc.ilp.ilp4.ast;

import java.util.HashSet;
import java.util.Set;

import fr.upmc.ilp.ilp4.interfaces.AbstractExplicitVisitor;
import fr.upmc.ilp.ilp4.interfaces.IAST4assignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4localAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4reference;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;

/** Ce visiteur collecte les variables globales par visite plutot que
 * d'utiliser les methodes des delegues. Les variables globales sont 
 * accumulees dans le visiteur meme. La visite est triviale puisque ce
 * visiteur ne visite que des AST normalisés où les variables globales
 * sont repérées par des instances de IAST4globalVariable.
 */

public class GlobalCollector 
extends AbstractExplicitVisitor<Object, RuntimeException> {
    
    public GlobalCollector () {
        this.globals = new HashSet<>();
    }
    protected final Set<IAST4globalVariable> globals;
    
    // Le point d'entrée de ce collecteur:
    public static IAST4globalVariable[] getGlobalVariables (IAST4program iast) {
        final GlobalCollector visitor = new GlobalCollector();
        iast.accept(visitor, null);
        return visitor.globals.toArray(new IAST4globalVariable[0]);
    }

    // Les visiteurs specialises: Ici l'on visite les variables!
    
    @Override
    public Object visit (IAST4assignment iast, Object nothing) {
        // Visiter la variable affectee:
        iast.getVariable().accept(this, nothing);
        iast.getValue().accept(this, nothing);
        return null;
    }
    @Override
    public Object visit (IAST4localAssignment iast, Object nothing) {
        visit((IAST4assignment) iast, nothing);
        return null;
    }
    @Override
    public Object visit (IAST4globalAssignment iast, Object nothing){
        visit((IAST4assignment) iast, nothing);
        return null;
    }

    @Override
    public Object visit (IAST4reference iast, Object nothing) {
        // Visiter la variable referencee:
        iast.getVariable().accept(this, nothing);
        return null;
    }

    @Override
    public Object visit (IAST4variable iast, Object nothing) {
        if ( iast instanceof IAST4globalVariable ) {
            globals.add((IAST4globalVariable) iast);
        }
        return null;
     }
    public Object visit (IAST4globalVariable iast, Object nothing) {
        globals.add((IAST4globalVariable) iast);
        return null;
     }
}
