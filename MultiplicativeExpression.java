public class MultiplicativeExpression extends OperationHandler implements Expression {
    private final Expression leftChild;
    private final Expression rightChild;
    private final char operator;

    /**
     * Creates a new MultiplicativeExpression with the given left and right children and operator.
     * a multiplicative expression is an expression that has a * or / operator
     * @param left the part of the expression to the left of the operator
     * @param right the part of the expression to the right of the operator
     * @param operator the operator
     */
    public MultiplicativeExpression (Expression left, Expression right, char operator) {
        this.leftChild = left;
        this.rightChild = right;
        this.operator = operator;
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
        return new MultiplicativeExpression(leftChild.deepCopy(), rightChild.deepCopy(), operator);
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
        return opToString(indentLevel, operator, leftChild, rightChild);
    }

    /**
     * Given the value of the independent variable x, compute the value of this expression.
     *
     * @param x the value of the independent variable x
     * @return the value of this expression.
     */
    @Override
    public double evaluate(double x) {
        return operation(leftChild, rightChild, operator, x);
    }

    /**
     * Produce a new, fully independent (i.e., there should be no shared subtrees) Expression
     * representing the derivative of this expression.
     * f(x) * g(x) = f'(x) * g(x) + f(x) * g'(x)
     * @return the derivative of this expression
     */
    @Override
    public Expression differentiate() {
        if (operator == '*') {
            return new AdditiveExpression( // f'(x) * g(x) + f(x) * g'(x)
                    new MultiplicativeExpression(leftChild.differentiate(), rightChild, '*'),
                    new MultiplicativeExpression(leftChild, rightChild.differentiate(), '*'),
                    '+'
            );
        } else {
            return new AdditiveExpression( // a/b
                    new MultiplicativeExpression(leftChild.differentiate(), rightChild, '/'),// (a'/b)
                    new MultiplicativeExpression(
                            new MultiplicativeExpression(leftChild, rightChild.differentiate(), '*'), // (a*b')
                            new ExponentialExpression(rightChild, new LiteralExpression("2")), '/'),
                    '-'
            );
        }
    }
}
