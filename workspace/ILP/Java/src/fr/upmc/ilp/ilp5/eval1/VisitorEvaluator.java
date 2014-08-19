package fr.upmc.ilp.ilp5.eval1;

import javax.xml.parsers.ParserConfigurationException;

import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp1.runtime.Invokable;
import fr.upmc.ilp.ilp2.interfaces.ICommon;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.interfaces.IAST4alternative;
import fr.upmc.ilp.ilp4.interfaces.IAST4assignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4binaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4computedInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4constant;
import fr.upmc.ilp.ilp4.interfaces.IAST4expression;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalFunctionVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4globalVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4invocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4localAssignment;
import fr.upmc.ilp.ilp4.interfaces.IAST4localBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4localVariable;
import fr.upmc.ilp.ilp4.interfaces.IAST4primitiveInvocation;
import fr.upmc.ilp.ilp4.interfaces.IAST4program;
import fr.upmc.ilp.ilp4.interfaces.IAST4reference;
import fr.upmc.ilp.ilp4.interfaces.IAST4sequence;
import fr.upmc.ilp.ilp4.interfaces.IAST4try;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryBlock;
import fr.upmc.ilp.ilp4.interfaces.IAST4unaryOperation;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitable;
import fr.upmc.ilp.ilp4.interfaces.IAST4while;
import fr.upmc.ilp.ilp5.ast.XMLwriter;
import fr.upmc.ilp.ilp5.interfaces.IAST5codefinedLocalFunctions;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionDefinition;
import fr.upmc.ilp.ilp5.interfaces.IAST5localFunctionVariable;
import fr.upmc.ilp.ilp5.interfaces.IAST5visitor;

/**
 * Un évaluateur pour ILP5 realisé par un visiteur.
 */

public class VisitorEvaluator 
implements IAST5visitor<IContext, Object, VisitorEvaluationException> {

    public VisitorEvaluator () {
        this.xmlwriter = null;
        try {
            this.xmlwriter = new XMLwriter();
        } catch (ParserConfigurationException e) {
            // pas d'XMLwriter!
        }
    }

    // FUTURE emettre sur un log4j

    protected Object tracedAccept (final IAST4visitable iast,
                                   final IContext context) 
        throws VisitorEvaluationException {
        // Annoncer ce que l'on va evaluer:
        String msg = "\nEval: ";
        try {
            // On tente de montrer l'ast!
            msg += xmlwriter.process(iast);
        } catch (Throwable t) {
            msg += "???";
        }
        System.err.println(msg);

        // evaluer
        Object value = iast.accept(this, context);

        // Annoncer la valeur obtenue:
        msg = "Value: " + value;
        System.err.println(msg);

        return value;
    }
    private XMLwriter xmlwriter;

    // La signature d'un visiteur s'interprète comme suit:
    // context définit les environnements lexicaux et globaux
    // l'objet calculé est la valeur de l'iast reçu.

    public Object visit(IAST4program iast, IContext context) 
            throws VisitorEvaluationException {
        IAST4functionDefinition[] definitions =
            iast.getFunctionDefinitions();
        for ( int i = 0 ; i<definitions.length ; i++ ) {
            tracedAccept(definitions[i], context);
        }
        Object result = null;
        try {
            result = tracedAccept(iast.getBody(), context);
        } catch ( ThrownException exc ) {
            // Deballer la valeur si c'est une valeur obtenue par
            // echappement. Cela revient a admettre qu'un programme P est
            // equivalent a try { P } catch (e) { e };
            result = exc.getThrownValue();
        }
        return result;
    }

    public Object visit(IAST4alternative iast, IContext context) 
            throws VisitorEvaluationException {
        Object bool = tracedAccept(iast.getCondition(), context);
        if ( bool instanceof Boolean ) {
          Boolean b = (Boolean) bool;
          if ( b.booleanValue() ) {
            return tracedAccept(iast.getConsequent(), context);
          } else {
            return tracedAccept(iast.getAlternant(), context);
          }
        } else {
          return tracedAccept(iast.getConsequent(), context);
        }
    }

    public Object visit(IAST4constant iast, IContext context) 
            throws VisitorEvaluationException {
        return iast.getValue();
    }

    public Object visit(IAST4reference iast, IContext context) 
            throws VisitorEvaluationException {
        return tracedAccept(iast.getVariable(), context);
    }

    public Object visit(IAST4variable iast, IContext context) 
            throws VisitorEvaluationException {
        if (iast instanceof IAST4globalVariable) {
            // regle aussi le cas des IAST4globalFunctionVariable
            return visit((IAST4globalVariable)iast, context);
        } else if (iast instanceof IAST4localVariable) {
            return visit((IAST4localVariable)iast, context);
        } else if (iast instanceof IAST5localFunctionVariable) {
            return visit((IAST5localFunctionVariable)iast, context);
        } else {
            throw new RuntimeException("Should not occur!");
        }
    }

    public Object visit(IAST4globalVariable iast, IContext context) 
            throws VisitorEvaluationException {
        ICommon common = ((Context)context).getCommonEnvironment();
        try {
            return common.globalLookup(iast);
        } catch (EvaluationException e) {
            throw new VisitorEvaluationException(e);
        }
    }
    
    public Object visit(IAST4globalFunctionVariable iast, IContext context) 
            throws VisitorEvaluationException {
        return visit((IAST4globalVariable)iast, context);
    }

    public Object visit(IAST4localVariable iast, IContext context) 
            throws VisitorEvaluationException {
        ILexicalEnvironment lexenv = context.getLexicalEnvironment();
        try {
            return lexenv.lookup(iast);
        } catch (EvaluationException e) {
            throw new VisitorEvaluationException(e);
        }
    }

    public Object visit(IAST5localFunctionVariable iast, IContext context) 
            throws VisitorEvaluationException {
        return visit((IAST4localVariable) iast, context);
    }

    public Object visit(IAST4assignment iast, IContext context) 
            throws VisitorEvaluationException {
        ILexicalEnvironment lexenv = context.getLexicalEnvironment();
        ICommon common = context.getCommonEnvironment();
        Object newValue = tracedAccept(iast.getValue(), context);
        try {
            lexenv.update(iast.getVariable(), newValue);
        } catch (EvaluationException e) {
            try {
                common.updateGlobal(iast.getVariable().getName(), newValue);
            } catch (EvaluationException e1) {
                throw new VisitorEvaluationException(e1);
            }
        }
        return newValue;
    }
    public Object visit(IAST4localAssignment iast, IContext context) 
            throws VisitorEvaluationException {
        ILexicalEnvironment lexenv = context.getLexicalEnvironment();
        Object newValue = tracedAccept(iast.getValue(), context);
        try {
            lexenv.update(iast.getVariable(), newValue);
        } catch (EvaluationException e) {
            throw new VisitorEvaluationException(e);
        }
        return newValue;
    }
    public Object visit(IAST4globalAssignment iast, IContext context) 
            throws VisitorEvaluationException {
        ICommon common = context.getCommonEnvironment();
        Object newValue = tracedAccept(iast.getValue(), context);
        try {
            common.updateGlobal(iast.getVariable().getName(), newValue);
        } catch (EvaluationException e) {
            throw new VisitorEvaluationException(e);
        }
        return newValue;
    }

    public Object visit(IAST4sequence iast, IContext context) 
            throws VisitorEvaluationException {
        Object last = Boolean.FALSE;
        for ( IAST4expression instr : iast.getInstructions() ) {
            last = tracedAccept(instr, context);
        }
        return last;
    }

    public Object visit(IAST4unaryBlock iast, IContext context) 
            throws VisitorEvaluationException {
        ILexicalEnvironment lexenv = context.getLexicalEnvironment();
        ICommon common = context.getCommonEnvironment();
        ILexicalEnvironment newlexenv =
            lexenv.extend(iast.getVariable(),
                          tracedAccept(iast.getInitialization(), context));
        Context newContext = new Context(newlexenv, common);
        return tracedAccept(iast.getBody(), newContext);
    }

    public Object visit(IAST4localBlock iast, IContext context) 
            throws VisitorEvaluationException {
        ILexicalEnvironment lexenv = context.getLexicalEnvironment();
        ICommon common = context.getCommonEnvironment();
        ILexicalEnvironment newlexenv = lexenv;
        final IAST4variable[] vars = iast.getVariables();
        final IAST4expression[] inits = iast.getInitializations();
        for (int i = 0; i < vars.length; i++) {
            Object arg = tracedAccept(inits[i], context);
            newlexenv = newlexenv.extend(vars[i], arg);
        }
        Context newContext = new Context(newlexenv, common);
        return tracedAccept(iast.getBody(), newContext);
    }

    public Object visit(IAST4unaryOperation iast, IContext context) 
            throws VisitorEvaluationException {
        ICommon common = ((Context)context).getCommonEnvironment();
        try {
            return common.applyOperator(
                    iast.getOperatorName(),
                    tracedAccept(iast.getOperand(), context));
        } catch (EvaluationException e) {
            throw new VisitorEvaluationException(e);
        }
    }

    public Object visit(IAST4binaryOperation iast, IContext context) 
            throws VisitorEvaluationException {
        ICommon common = ((Context)context).getCommonEnvironment();
        try {
            return common.applyOperator(
                    iast.getOperatorName(),
                    tracedAccept(iast.getLeftOperand(), context),
                    tracedAccept(iast.getRightOperand(), context));
        } catch (EvaluationException e) {
            throw new VisitorEvaluationException(e);
        }
    }

    public Object visit(IAST4invocation iast, IContext context) 
            throws VisitorEvaluationException {
        final Object fn = tracedAccept(iast.getFunction(), context);
        if ( fn instanceof IVisitableUserFunction ) {
          final IVisitableUserFunction function = (IVisitableUserFunction) fn;
          final IAST4expression[] arguments = iast.getArguments();
          final Object[] args = new Object[arguments.length];
          for ( int i = 0 ; i<arguments.length; i++ ) {
            args[i] = tracedAccept(arguments[i], context);
          }
          return function.invoke(args, this, (IContext) context);
        } else {
          final String msg = "Not a function: " + fn;
          throw new VisitorEvaluationException(msg);
        }
    }

    public Object visit(IAST4globalInvocation iast, IContext context)  
            throws VisitorEvaluationException {
        if ( iast.getInlined() == null ) {
            return visit((IAST4invocation)iast, context);
        } else {
          return tracedAccept(iast.getInlined(), context);
        }
    }

    public Object visit(IAST4primitiveInvocation iast, IContext context) 
            throws VisitorEvaluationException {
        ICommon common = context.getCommonEnvironment();
        Object fn = null;
        try {
            fn = common.primitiveLookup(iast.getPrimitiveName());
        } catch (EvaluationException e) {
            final String msg = "No such primitive " + iast.getPrimitiveName();
            throw new VisitorEvaluationException(msg);
        }
        if ( fn instanceof Invokable ) {
          Invokable invokable = (Invokable) fn;
          final IAST4expression[] arguments = iast.getArguments();
          final Object[] args = new Object[arguments.length];
          for ( int i = 0 ; i<arguments.length ; i++ ) {
            args[i] = tracedAccept(arguments[i], context);
          }
          try {
            return invokable.invoke(args);
        } catch (EvaluationException e) {
            final String msg = "Problem invoking primitive "
                + iast.getPrimitiveName();
            throw new VisitorEvaluationException(msg);
        }
        } else {
          final String msg = "Not a function: " + fn;
          throw new VisitorEvaluationException(msg);
        }
    }

    public Object visit(IAST4computedInvocation iast, IContext data) 
            throws VisitorEvaluationException {
        return visit((IAST4invocation) iast, data);
    }

    public Object visit(IAST4functionDefinition iast, IContext context) 
            throws VisitorEvaluationException {
        ICommon common = context.getCommonEnvironment();
        final Object function =
            new VisitableUserFunction(
                    iast.getFunctionName(),
                    iast.getVariables(),
                    iast.getBody());
        try {
            common.updateGlobal(iast.getFunctionName(), function);
        } catch (EvaluationException e) {
            throw new VisitorEvaluationException(e);
        }
        return function;
    }

    public Object visit(IAST4try iast, IContext context) 
            throws VisitorEvaluationException {
        ILexicalEnvironment lexenv = context.getLexicalEnvironment();
        ICommon common = context.getCommonEnvironment();
        Object result = Boolean.FALSE;
        try {
            result = tracedAccept(iast.getBody(), context);
        } catch (ThrownException e) {
            if ( iast.getCatcher() != null ) {
                final ILexicalEnvironment catcherLexenv =
                    lexenv.extend(iast.getCaughtExceptionVariable(),
                                  e.getThrownValue());
                IContext newContext = new Context(catcherLexenv, common);
                tracedAccept(iast.getCatcher(), newContext);
            } else {
                throw e;
            }
        } catch (RuntimeException e) {
            if ( iast.getCatcher() != null ) {
                final ILexicalEnvironment catcherLexenv =
                    lexenv.extend(iast.getCaughtExceptionVariable(), e);
                IContext newContext = new Context(catcherLexenv, common);
                tracedAccept(iast.getCatcher(), newContext);
            } else {
                throw e;
            }
        } finally {
            if ( iast.getFinallyer() != null ) {
                tracedAccept(iast.getFinallyer(), context);
            }
        }
        return result;
    }

    public Object visit(IAST4while iast, IContext context) 
            throws VisitorEvaluationException {
        while ( true ) {
            Object bool = tracedAccept(iast.getCondition(), context);
            if ( Boolean.FALSE == bool ) {
              break;
            }
            tracedAccept(iast.getBody(), context);
          }
          return Boolean.FALSE;
    }

    public Object visit(IAST5codefinedLocalFunctions iast, IContext context) 
            throws VisitorEvaluationException {
        ILexicalEnvironment lexenv = context.getLexicalEnvironment();
        ICommon common = context.getCommonEnvironment();
        ILexicalEnvironment newlexenv = lexenv;
        for ( IAST5localFunctionDefinition fundef : iast.getFunctions() ) {
            newlexenv = newlexenv.extend(fundef.getDefinedVariable(),
                                         Boolean.FALSE);
        }
        for ( IAST5localFunctionDefinition fundef : iast.getFunctions() ) {
            IVisitableUserFunction fn = new VisitableUserFunction(
                    fundef.getFunctionName(),
                    fundef.getVariables(),
                    fundef.getBody(),
                    newlexenv);
            try {
                newlexenv.update(fundef.getDefinedVariable(), fn);
            } catch (EvaluationException e) {
                throw new RuntimeException("Should never happen!");
            }
        }
        IContext newContext = new Context(newlexenv, common);
        return tracedAccept(iast.getBody(), newContext);
    }

    public Object visit(IAST5localFunctionDefinition iast, IContext context) 
            throws VisitorEvaluationException {
        throw new RuntimeException("Should never happen!");
    }
}
