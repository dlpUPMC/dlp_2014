/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id: CEAST.java 1243 2012-09-16 08:01:36Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp4.ast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.annotation.ILPvariable;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.interfaces.IAST2expression;
import fr.upmc.ilp.ilp2.interfaces.IAST2functionDefinition;
import fr.upmc.ilp.ilp2.interfaces.IAST2instruction;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4;
import fr.upmc.ilp.ilp4.interfaces.IAST4Factory;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4instruction;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;

/** Code commun à tous les noeuds de l'AST d'ILP4. */

public abstract class CEAST
extends fr.upmc.ilp.ilp2.ast.CEAST
implements IAST4 {

    protected CEAST () {
        this.invokedFunctions = new HashSet<>();
    }
    
    public abstract Object eval (ILexicalEnvironment lexenv, 
                                 ICommon common)
    throws EvaluationException;

    /** Normaliser l'AST. Par défaut, le nœud est normalisé. La
     * normalisation porte surtout sur les variables, la ternarisation
     * des alternatives, la réduction des séquences triviales. */

    public IAST4 normalize (
            final INormalizeLexicalEnvironment lexenv,
            final INormalizeGlobalEnvironment common,
            final IAST4Factory factory )
    throws NormalizeException {
        return this;
    }

    /**
     * Pratique en Eclipse! Ainsi, dans la perspective de mise au point,
     * la valeur d'un CEASTprogram s'affichera de maniere plus lisible. Il
     * egalement possible de positionner (menu contextuel: edit detail
     * formatter) sur la variable Process.ceast qu'on veut la voir s'afficher
     * avec: "return new XMLwriter().process(this);". Cette meme astuce doit
     * fonctionner avec toute instance d'IAST4.
     */
    @Override
    public String toString () {
        try {
            if ( xmlwriter == null ) {
               xmlwriter = new XMLwriter();
            }
            return xmlwriter.process(this);
        } catch (Throwable t) {
            return super.toString();
        }
    }
    private static XMLwriter xmlwriter;

    /** Calculer le graphe d'appel c'est-à-dire pour chaque expression,
     * les fonctions globales qu'elle invoque. Par défaut, l'expression
     * n'invoque aucune fonction globale. */

    public void computeInvokedFunctions ()
    throws FindingInvokedFunctionsException {
        final Class<? extends CEAST> clazz = this.getClass();
        for ( final Method m : clazz.getMethods() ) {
            // FUTURE: mettre en cache cette recherche serait plus efficace!
            final ILPexpression ee = m.getAnnotation(ILPexpression.class);
            handleAnnotation(ee, m);
            final ILPvariable ev = m.getAnnotation(ILPvariable.class);
            handleAnnotation (ev, m);
        }
    }
    private Set<IAST4globalFunctionVariable> invokedFunctions;
    // NOTE: un tel champ par instance est dispendieux!
    public void setInvokedFunctions (Set<IAST4globalFunctionVariable> funvars) {
        this.invokedFunctions = funvars;
    }

    public void handleAnnotation (ILPexpression e, Method m)
    throws FindingInvokedFunctionsException {
        try {
            if ( e != null ) {
                if ( e.isArray() ) {
                    final Object[] results = (Object[])
                    m.invoke(this, EMPTY_ARGUMENT_ARRAY);
                    for ( Object result : results ) {
                        if ( e.neverNull() || result != null ) {
                            final IAST4expression component =
                                CEAST.narrowToIAST4expression(result);
                            this.findAndAdjoinToInvokedFunctions(component);
                        }
                    }
                } else {
                    final Object result =
                        m.invoke(this, EMPTY_ARGUMENT_ARRAY);
                    if ( e.neverNull() || result != null ) {
                        final IAST4expression component =
                            CEAST.narrowToIAST4expression(result);
                        this.findAndAdjoinToInvokedFunctions(component);
                    }
                }
            }
        } catch (IllegalArgumentException exc) {
            throw new FindingInvokedFunctionsException(exc);
        } catch (IllegalAccessException exc) {
            throw new FindingInvokedFunctionsException(exc);
        } catch (InvocationTargetException exc) {
            throw new FindingInvokedFunctionsException(exc);
        }
    }

    public void handleAnnotation (ILPvariable e, Method m)
    throws FindingInvokedFunctionsException {
        try {
            if ( e != null ) {
                if ( e.isArray() ) {
                    final Object[] results = (Object[])
                    m.invoke(this, EMPTY_ARGUMENT_ARRAY);
                    for ( Object result : results ) {
                        if ( e.neverNull() || result != null ) {
                            final IAST4variable component =
                                CEAST.narrowToIAST4variable(result);
                            this.findAndAdjoinToInvokedFunctions(component);
                        }
                    }
                } else {
                    final Object result =
                        m.invoke(this, EMPTY_ARGUMENT_ARRAY);
                    if ( e.neverNull() || result != null ) {
                        final IAST4variable component =
                            CEAST.narrowToIAST4variable(result);
                        this.findAndAdjoinToInvokedFunctions(component);
                    }
                }
            }
        } catch (IllegalArgumentException exc) {
            throw new FindingInvokedFunctionsException(exc);
        } catch (IllegalAccessException exc) {
            throw new FindingInvokedFunctionsException(exc);
        } catch (InvocationTargetException exc) {
            throw new FindingInvokedFunctionsException(exc);
        }
    }

    /** Une methode utilitaire pour fusionner des ensembles de fonctions
     * globales provenant des sous-arbres de l'expression courante. */

    protected void findAndAdjoinToInvokedFunctions (
            final IAST4expression e)
    throws FindingInvokedFunctionsException {
        e.computeInvokedFunctions();
        this.invokedFunctions.addAll(e.getInvokedFunctions());
    }

    protected void findAndAdjoinToInvokedFunctions (
            final IAST4variable e)
    throws FindingInvokedFunctionsException {
        if ( e instanceof IAST4globalFunctionVariable ) {
            IAST4globalFunctionVariable gfv = (IAST4globalFunctionVariable) e;
            this.invokedFunctions.add(gfv);
        }
    }

    /** Renvoyer l'ensemble des fonctions globales invoquées (qui doivent avoir
     * ete précédemment calculées). */

    public Set<IAST4globalFunctionVariable> getInvokedFunctions () {
        return this.invokedFunctions;
    }

    /** Indiquer qu'une fonction est invoquee. */

    public void addInvokedFunction (
            final IAST4globalFunctionVariable variable) {
        this.invokedFunctions.add(variable);
    }

    /** Indiquer que d'autres fonctions sont invoquees. Renvoie vrai lorsque
     * de nouvelles fonctions ont ete ajoutees qui n'etaient pas encore
     * presentes (comme la methode Set.addAll()) */

    public boolean addInvokedFunctions (
            final Set<IAST4globalFunctionVariable> others) {
        return this.invokedFunctions.addAll(others);
    }

    /** Intégrer les fonctions non récursives. Cette implantation use de
     * réflexivité. */

    public void inline (IAST4Factory factory)
    throws InliningException {
        final Class<? extends CEAST> clazz = this.getClass();
        for ( Method m : clazz.getMethods() ) {
            try {
                final ILPexpression e = m.getAnnotation(ILPexpression.class);
                if ( e != null ) {
                    if ( e.isArray() ) {
                        final Object[] results = (Object[])
                            m.invoke(this, EMPTY_ARGUMENT_ARRAY);
                        for ( Object result : results ) {
                            if ( e.neverNull() || result != null ) {
                                final IAST4expression component =
                                    CEAST.narrowToIAST4expression(result);
                                component.inline(factory);
                            }
                        }
                    } else {
                        final Object result =
                            m.invoke(this, EMPTY_ARGUMENT_ARRAY);
                        if ( e.neverNull() || result != null ) {
                            final IAST4expression component =
                                CEAST.narrowToIAST4expression(result);
                            component.inline(factory);
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                throw new InliningException(e);
            } catch (IllegalAccessException e) {
                throw new InliningException(e);
            } catch (InvocationTargetException e) {
                throw new InliningException(e);
            }
        }
    }
    private static final Object[] EMPTY_ARGUMENT_ARRAY = new Object[0];

  /** Les rétrécisseurs spécialisés. */

    public static IAST4 narrowToIAST4 (Object o) {
      if ( o instanceof IAST4) {
        return (IAST4) o;
      } else {
        final String msg = "Cannot cast into IAST4: " + o;
        throw new ClassCastException(msg);
      }
    }

  public static IAST4variable narrowToIAST4variable (Object o) {
    if ( o instanceof IAST4variable ) {
      return (IAST4variable) o;
    } else {
      final String msg = "Cannot cast into IAST4variable: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4variable[] narrowToIAST4variableArray (Object o) {
    if ( o instanceof IAST4variable[] ) {
      return (IAST4variable[]) o;
    } else if ( o instanceof IAST2variable[] ) {
      IAST2variable[] v = (IAST2variable[]) o;
      IAST4variable[] result = new IAST4variable[v.length];
      for ( int i=0 ; i<v.length ; i++ ) {
          result[i] = CEAST.narrowToIAST4variable(v[i]);
      }
      return result;
    } else {
      final String msg = "Cannot cast into IAST4variable[]: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4globalVariable narrowToIAST4globalVariable (Object o) {
    if ( o instanceof IAST4globalVariable ) {
      return (IAST4globalVariable) o;
    } else {
      final String msg = "Cannot cast into IAST4globalVariable: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4globalVariable[] narrowToIAST4globalVariableArray (Object o) {
    if ( o instanceof IAST4globalVariable[] ) {
      return (IAST4globalVariable[]) o;
    } else if ( o instanceof IAST2variable[] ) {
        IAST2variable[] v = (IAST2variable[]) o;
        IAST4globalVariable[] result = new IAST4globalVariable[v.length];
        for ( int i=0 ; i<v.length ; i++ ) {
            result[i] = CEAST.narrowToIAST4globalVariable(v[i]);
        }
        return result;
    } else {
      final String msg = "Cannot cast into IAST4globalVariable: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4localVariable narrowToIAST4localVariable (Object o) {
    if ( o instanceof IAST4localVariable ) {
      return (IAST4localVariable) o;
    } else {
      final String msg = "Cannot cast into IAST4localVariable: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4localVariable[] narrowToIAST4localVariableArray (Object o) {
    if ( o instanceof IAST4localVariable[] ) {
      return (IAST4localVariable[]) o;
    } else if ( o instanceof IAST2variable[] ) {
        IAST2variable[] v = (IAST2variable[]) o;
        IAST4localVariable[] result = new IAST4localVariable[v.length];
        for ( int i=0 ; i<v.length ; i++ ) {
            result[i] = CEAST.narrowToIAST4localVariable(v[i]);
        }
        return result;
    } else {
      final String msg = "Cannot cast into IAST4localVariable: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4globalFunctionVariable
    narrowToIAST4globalFunctionVariable (Object o) {
    if ( o instanceof IAST4globalFunctionVariable ) {
      return (IAST4globalFunctionVariable) o;
    } else {
      final String msg = "Cannot cast into IAST4globalFunctionVariable: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4expression narrowToIAST4expression (Object o) {
    if ( o instanceof IAST4expression ) {
      return (IAST4expression) o;
    } else {
      final String msg = "Cannot cast into IAST4expression: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4expression[] narrowToIAST4expressionArray (Object o) {
    if ( o instanceof IAST4expression[] ) {
      return (IAST4expression[]) o;
    } else if ( o instanceof IAST2expression<?>[] ) {
        IAST2expression<?>[] v = (IAST2expression[]) o;
        IAST4expression[] result = new IAST4expression[v.length];
        for ( int i=0 ; i<v.length ; i++ ) {
            result[i] = CEAST.narrowToIAST4expression(v[i]);
        }
        return result;
    } else if ( o instanceof IAST2instruction[] ) {
      IAST2instruction<?>[] v = (IAST2instruction[]) o;
      IAST4expression[] result = new IAST4expression[v.length];
      for ( int i=0 ; i<v.length ; i++ ) {
          result[i] = CEAST.narrowToIAST4expression(v[i]);
      }
      return result;
    } else {
      final String msg = "Cannot cast into IAST4expression[]: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4instruction narrowToIAST4instruction (Object o) {
    if ( o instanceof IAST4instruction ) {
      return (IAST4instruction) o;
    } else {
      final String msg = "Cannot cast into IAST4instruction: " + o;
      throw new ClassCastException(msg);
    }
  }

  public static IAST4functionDefinition narrowToIAST4functionDefinition (Object o) {
      if ( o instanceof IAST4functionDefinition ) {
        return (IAST4functionDefinition) o;
      } else {
        final String msg = "Cannot cast into IAST4functionDefinition: " + o;
        throw new ClassCastException(msg);
      }
    }

  public static IAST4functionDefinition[] narrowToIAST4functionDefinitionArray (Object o) {
      if ( o instanceof IAST4functionDefinition[] ) {
        return (IAST4functionDefinition[]) o;
      } else if ( o instanceof IAST2functionDefinition<?>[] ) {
          IAST2functionDefinition<?>[] v = (IAST2functionDefinition[]) o;
          IAST4functionDefinition[] result = new IAST4functionDefinition[v.length];
          for ( int i=0 ; i<v.length ; i++ ) {
              result[i] = CEAST.narrowToIAST4functionDefinition(v[i]);
          }
          return result;
      } else {
        final String msg = "Cannot cast into IAST4functionDefinition[]: " + o;
        throw new ClassCastException(msg);
      }
    }
  
  public static IAST4Factory narrowToIAST4Factory (Object o) {
      if ( o instanceof IAST4Factory ) {
        return (IAST4Factory) o;
      } else {
        final String msg = "Cannot cast into IAST4Factory: " + o;
        throw new ClassCastException(msg);
      }
    }
  
  public static IAST4program narrowToIAST4program(Object o) {
      if ( o instanceof IAST4program ) {
        return (IAST4program) o;
      } else {
        final String msg = "Cannot cast into IAST4program: " + o;
        throw new ClassCastException(msg);
      }
    }
  public static fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment 
    narrowToLexicalEnvironment2(Object o) {
      if ( o instanceof fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment ) {
          return (fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment) o;
        } else {
          final String msg = "Cannot cast into ilp2.interfaces.ILexicalEnvironment: " + o;
          throw new ClassCastException(msg);
        }
  }
}

// end of CEAST.java
