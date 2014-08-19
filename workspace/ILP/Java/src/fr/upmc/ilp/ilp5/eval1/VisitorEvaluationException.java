package fr.upmc.ilp.ilp5.eval1;

public class VisitorEvaluationException
extends RuntimeException {

    private static final long serialVersionUID = 200711241501L;

    public VisitorEvaluationException(Exception exc) {
        super(exc);
    }
    public VisitorEvaluationException(String msg) {
        super(msg);
    }
}
