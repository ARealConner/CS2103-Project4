public class ExponentialExpression extends OperationHandler implements Expression {
    private final Expression leftChild;
    private Expression rightChild;
    private final char operator;

    /**
     * Creates a new ExponentialExpression with the ^ operator.
     * @param left the part of the expression to the left of the operator
     * @param right the part of the expression to the right of the operator
     */
    public ExponentialExpression(Expression left, Expression right) {
        this.leftChild = left;
        this.rightChild = right;
        this.operator = '^';
    }

    /**
     * Creates a new ExponentialExpression with the log operator.
     * @param exp this should always be a parenthetical, literal, or variable expression
     * the left child will be used for the log and the right one will be ignored
     */
    public ExponentialExpression(Expression exp) {
        this.leftChild = exp;
        this.operator = 'l'; // log
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
        if (operator == 'l') {
            return new ExponentialExpression(leftChild.deepCopy());
        } else {
            return new ExponentialExpression(leftChild.deepCopy(), rightChild.deepCopy());
        }
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
     *
     * @return the derivative of this expression
     */
    @Override
    public Expression differentiate() {
        if (operator == 'l') {
            return new MultiplicativeExpression(leftChild.differentiate(), leftChild.deepCopy(), '/');
        } else {
            // If function f(x)=C^h(x) (where C is a positive constant),
            // then its derivative is f'(x)=(log C) C^h(x) h'(x),
            // where log is the natural logarithm function.
            if (leftChild instanceof LiteralExpression) {
                return new MultiplicativeExpression(new MultiplicativeExpression( // (log C) C^h(x)
                        new ExponentialExpression(leftChild.deepCopy()), // log C
                        new ExponentialExpression(leftChild.deepCopy(), // C^h(x)
                                rightChild.deepCopy()), '*'),
                        rightChild.differentiate(), '*'); // h'(x)
            }

            // If function f(x)=g(x)^C (where C is a constant), then its derivative is f'(x)=C * g(x)^(C-1) * g'(x).
            if (rightChild instanceof LiteralExpression) {
                return new MultiplicativeExpression(new MultiplicativeExpression( // C*g(x)^(C-1)
                        rightChild.deepCopy(), // C
                        new ExponentialExpression( // g(x)^(C-1)
                                leftChild.deepCopy(),
                                new AdditiveExpression(
                                        rightChild.deepCopy(),
                                        new LiteralExpression("1"),
                                        '-')
                        ),
                        '*'),
                        leftChild.differentiate(), // g'(x)
                        '*');
            }

            // If function f(x)=g(x)^h(x) then its derivative is f'(x)=(g(x)^h(x)) (h'(x)g(x) + g'(x)h(x)/g(x)).
            return new MultiplicativeExpression(
                new ExponentialExpression(leftChild.deepCopy(), rightChild.deepCopy()), // g(x)^h(x)
                new AdditiveExpression( // h'(x)g(x) + g'(x)h(x)/g(x)
                    new MultiplicativeExpression( // h'(x)g(x)
                            leftChild.differentiate(),
                            rightChild.deepCopy(),
                            '*'),
                        new MultiplicativeExpression( // g'(x)h(x)/g(x)
                            new MultiplicativeExpression( // g'(x)h(x)
                                    rightChild.differentiate(),
                                    leftChild.deepCopy(), '*'),
                            rightChild.deepCopy(), // g(x)
                            '/'
                    ),
                        '+'),
                    '*');
        }
    }
}