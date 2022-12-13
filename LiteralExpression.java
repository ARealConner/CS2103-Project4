public class LiteralExpression extends OperationHandler implements Expression {
    private float value;
    public LiteralExpression (String value) {
        this.value = Float.parseFloat(value);
    }
    @Override
    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    public Expression deepCopy() {
        return new LiteralExpression(Float.toString(value));
    }

    /**
     * Creates a String representation of this expression with a given starting
     * indent level. If indentLevel is 0, then the produced string should have no
     * indent; if the indentLevel is 1, then there should be 1 tab '\t'
     * character at the start of every line produced by this method; etc.w
     * @param indentLevel how many tab characters should appear at the beginning of each line.
     * @return the String representing this expression.
     */
    @Override
    public String convertToString(int indentLevel) {
        return "\t".repeat(indentLevel) + value;
    }

    /**
     * Given the value of the independent variable x, compute the value of this expression.
     * @param x the value of the independent variable x
     * @return the value of this expression.
     */
    @Override
    public double evaluate(double x) {
        return value;
    }

    /**
     * Produce a new, fully independent (i.e., there should be no shared subtrees) Expression
     * representing the derivative of this expression.
     * @return the derivative of this expression
     */
    @Override
    public Expression differentiate() {
        return new LiteralExpression("0");
    }
}
