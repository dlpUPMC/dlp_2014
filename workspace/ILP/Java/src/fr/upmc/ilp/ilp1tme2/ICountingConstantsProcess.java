package fr.upmc.ilp.ilp1tme2;

public interface ICountingConstantsProcess {
    // Compte les constantes a partir du DOM:
    public int getNbConstantesDOM() ;
    
    // Compte les constantes a partir de l'AST:
    public int getNbConstantesAST() ;
}
