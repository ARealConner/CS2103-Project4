public class SimpleExpressionParser implements ExpressionParser {
        /*
         * Attempts to create an expression tree from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * Grammar:
	 * S -> A | P Start variable
	 * A -> A+M | A-M | M
	 * M -> M*E | M/E | E
	 * E -> P^E | P | log(P)
	 * P -> (S) | L | V
	 * L -> <float>
	 * V -> x
         * @param str the string to parse into an expression tree
         * @return the Expression object representing the parsed expression tree
         */
	public Expression parse (String str) throws ExpressionParseException {
		str = str.replaceAll(" ", "");
		Expression expression = parseAdditiveExpression(str);
		if (expression == null) {
			expression = parseParentheticalExpression(str);
			if (expression == null) {
				throw new ExpressionParseException("Cannot parse expression: " + str);
			}
		}

		return expression;
	}
	// * A -> A+M | A-M | M
	protected Expression parseAdditiveExpression (String str) {
		for(int i = 0; i < str.length(); i++){
			char op = str.charAt(i);
			if (op == '+') {
				Expression left = parseAdditiveExpression(str.substring(0, i));
				Expression right = parseMultiplicativeExpression(str.substring(i + 1));
				if (left != null && right != null){
					return new AdditiveExpression(left, right, op);
				}
			}
		}
		for(int i = 0; i < str.length(); i++){
			char op = str.charAt(i);
			if (op == '-') {
				Expression left = parseAdditiveExpression(str.substring(0, i));
				Expression right = parseMultiplicativeExpression(str.substring(i + 1));
				if (left != null && right != null){
					return new AdditiveExpression(left, right, op);
				}
			}
		}
		return parseMultiplicativeExpression(str);
	}

	/*
	 * Attempts to create an expression tree from the specified String.`
	 * Throw an ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @return the Expression object representing the parsed expression tree
	 * M -> M*E | M/E | E
	 */
	protected Expression parseMultiplicativeExpression (String str) {
		for(int i = 0; i < str.length(); i++){
			char op = str.charAt(i);
			if (op == '*') {
				Expression left = parseMultiplicativeExpression(str.substring(0, i));
				Expression right = parseExponentialExpression(str.substring(i + 1));
				if (left != null && right != null){
					return new MultiplicativeExpression(left, right, op);
				}
			}
			if (op == '/') {
				Expression left = parseMultiplicativeExpression(str.substring(0, i));
				Expression right = parseExponentialExpression(str.substring(i + 1));
				if (left != null && right != null){
					return new MultiplicativeExpression(left, right, op);
				}
			}
		}
		return parseExponentialExpression(str);
	}

	/*
	 * Attempts to create an expression tree from the specified String.
	 * Throws an ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @return the Expression object representing the parsed expression tree
	 * E -> P^E | P | log(P)
	 */
	protected Expression parseExponentialExpression (String str) {
		for(int i = 0; i < str.length(); i++){
			char op = str.charAt(i);
			if (op == '^') {
				Expression left = parseAdditiveExpression(str.substring(0, i));
				Expression right = parseAdditiveExpression(str.substring(i + 1));
				if (left != null && right != null){
					return new ExponentialExpression(left, right);
				}
			}
		}
		Expression expression = parseParentheticalExpression(str);
		if (expression != null) {
			return expression;
		}
		for (int i = 0; i < str.length(); i++) {
			char op = str.charAt(i);
			if (op == 'l') {
				Expression right = parseParentheticalExpression(str.substring(i + 3));
				if (right != null){
					return new ExponentialExpression(right);
				}
			}
		}
		return null;
	}

	/*
	 * Attempts to create an expression tree from the specified String.
	 * Throws an ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @return the Expression object representing the parsed expression tree
	 * P -> (S) | L | V
	 */
	protected Expression parseParentheticalExpression (String str) {
		if (str.startsWith("(") && str.endsWith(")")) {
			String sub = str.substring(1, str.length() - 1);
			return new ParentheticalExpression(parseAdditiveExpression(sub));
		}
		Expression endPoint = parseLiteralExpression(str);
		if (endPoint == null) {
			endPoint = parseVariableExpression(str);
		}
		return endPoint;
	}

	/*
	 * Attempts to create an expression tree from the specified String.
	 * Throws an ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @return the Expression object representing the parsed expression tree
	 * V -> x
	 */
	protected VariableExpression parseVariableExpression (String str) {
			if (str.equals("x")) {
					return new VariableExpression();
			}
			return null;
	}

	/*
	 * Attempts to create an expression tree from the specified String.
	 * Throws an ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @return the Expression object representing the parsed expression tree
	 * L -> <float>
	 */
	protected LiteralExpression parseLiteralExpression (String str) {
		// From https://stackoverflow.com/questions/3543729/how-to-check-that-a-string-is-parseable-to-a-double/22936891:
		final String Digits     = "(\\p{Digit}+)";
		final String HexDigits  = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally 
		// signed decimal integer.
		final String Exp        = "[eE][+-]?"+Digits;
		final String fpRegex    =
		    ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
		    "[+-]?(" +         // Optional sign character
		    "NaN|" +           // "NaN" string
		    "Infinity|" +      // "Infinity" string

		    // A decimal floating-point string representing a finite positive
		    // number without a leading sign has at most five basic pieces:
		    // Digits . Digits ExponentPart FloatTypeSuffix
		    // 
		    // Since this method allows integer-only strings as input
		    // in addition to strings of floating-point literals, the
		    // two sub-patterns below are simplifications of the grammar
		    // productions from the Java Language Specification, 2nd 
		    // edition, section 3.10.2.

		    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

		    // . Digits ExponentPart_opt FloatTypeSuffix_opt
		    "(\\.("+Digits+")("+Exp+")?)|"+

		    // Hexadecimal strings
		    "((" +
		    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "(\\.)?)|" +

		    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

		    ")[pP][+-]?" + Digits + "))" +
		    "[fFdD]?))" +
		    "[\\x00-\\x20]*");// Optional trailing "whitespace"

		if (str.matches(fpRegex)) {
			return new LiteralExpression(str);
		}
		return null;
	}

	public static void main (String[] args) throws ExpressionParseException {
		final ExpressionParser parser = new SimpleExpressionParser();
		System.out.println(parser.parse("10*2+12-4.").convertToString(0));
	}
}
