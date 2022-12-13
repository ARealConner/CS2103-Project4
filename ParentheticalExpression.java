public class ParentheticalExpression extends OperationHandler implements Expression {
    private final Expression child;

    public ParentheticalExpression(Expression child) {
        this.child = child;
    }

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     *
     * @return the deep copy
     */
    @Override
    public Expression deepCopy() {
        return new ParentheticalExpression(child.deepCopy());
    }

    /**
     * Creates a String representation of this expression with a given starting
     * indent level. If indentLevel is 0, then the produced string should have no
     * indent; if the indentLevel is 1, then there should be 1 tab '\t'
     * character at the start of every line produced by this method; etc.
     *
     * @param indentLevel how many tab characters should appear at the beginning of each line.
     * @return the String representing this expression.
     */
    @Override
    public String convertToString(int indentLevel) {
        return opToString(indentLevel, '(', child, null);
    }

    /**
     * Given the value of the independent variable x, compute the value of this expression.
     *
     * @param x the value of the independent variable x
     * @return the value of this expression.
     */
    @Override
    public double evaluate(double x) {
        return child.evaluate(x);
    }

    /**
     * Produce a new, fully independent (i.e., there should be no shared subtrees) Expression
     * representing the derivative of this expression.
     * (f(x))' = f'(x)
     * @return the derivative of this expression
     */
    @Override
    public Expression differentiate() {
        return new ParentheticalExpression(child.differentiate());
    }
}
