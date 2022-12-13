public class OperationHandler {
    /**
     * Given two expressions and an operator, compute the result of the operation.
     *
     * @param l  the left child of the operation
     * @param r the right child of the operation
     * @param op the operator
     * @param x the value of the independent variable x
     * @return the result of the operation
     */
    double operation(Expression l, Expression r, char op, double x) {
        switch (op) {
            case '+':
                return l.evaluate(x) + r.evaluate(x);
            case '-':
                return l.evaluate(x) - r.evaluate(x);
            case '*':
                return l.evaluate(x) * r.evaluate(x);
            case '/':
                return l.evaluate(x) / r.evaluate(x);
            case '^':
                return Math.pow(l.evaluate(x), r.evaluate(x));
            case 'l':
                return Math.log(l.evaluate(x));
            default:
                throw new IllegalArgumentException("Invalid operator: " + op);
        }
    }

    /**
     * coverts the operation into a string
     * @param indentLevel how many tab characters should appear at the beginning of each line.
     * @param op the operator
     * @param l the left child
     * @param r the right child
     * @return the String representing this expression.
     */
    String opToString(int indentLevel, char op, Expression l, Expression r) {
        if (op == '(') {
            return "\t".repeat(indentLevel) + "()" + "\n"
                    + l.convertToString(indentLevel+1);
        }
        return "\t".repeat(indentLevel) + op + "\n"
                + l.convertToString(indentLevel+1)
                + (op != 'l' ? r.convertToString(indentLevel+1) : ""); // log only has one child
    }
}
