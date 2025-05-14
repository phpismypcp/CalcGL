package org.isep.cleancode.calculator;

public class Calculator {
    /**
     */
    public double evaluateMathExpression(String expression) {
        // Remove all spaces to simplify parsing
        expression = expression.replaceAll("\\s+", "");

        // Start parsing from the beginning of the expression
        Result result = parseExpression(expression, 0);

        // Check if we consumed the entire expression
        if (result.position < expression.length()) {
            throw new IllegalArgumentException("Unexpected character at position " + result.position);
        }

        return result.value;
    }

    /**
     */
    private Result parseExpression(String expression, int position) {
        Result result = parseTerm(expression, position);

        while (result.position < expression.length()) {
            char operator = expression.charAt(result.position);

            if (operator == '+' || operator == '-') {
                // Parse the next term
                Result rightTerm = parseTerm(expression, result.position + 1);

                if (operator == '+') {
                    result.value += rightTerm.value;
                } else {
                    result.value -= rightTerm.value;
                }

                result.position = rightTerm.position;
            } else {
                // Not a + or - operator, exit the loop
                break;
            }
        }

        return result;
    }

    /**
     */
    private Result parseTerm(String expression, int position) {
        Result result = parseFactor(expression, position);

        while (result.position < expression.length()) {
            char operator = expression.charAt(result.position);

            if (operator == '*') {
                // Parse the next factor
                Result rightFactor = parseFactor(expression, result.position + 1);

                result.value *= rightFactor.value;
                result.position = rightFactor.position;
            } else {
                // Not a * operator, exit the loop
                break;
            }
        }

        return result;
    }

    /**
     */
    private Result parseFactor(String expression, int position) {
        if (position >= expression.length()) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }

        char ch = expression.charAt(position);

        // Check for negative numbers
        if (ch == '-') {
            // Parse the next factor and negate it
            Result result = parseFactor(expression, position + 1);
            result.value = -result.value;
            return result;
        }

        // Check for parentheses
        if (ch == '(') {
            // Parse the expression inside the parentheses
            Result result = parseExpression(expression, position + 1);

            // Ensure there's a closing parenthesis
            if (result.position < expression.length() && expression.charAt(result.position) == ')') {
                result.position++;
                return result;
            } else {
                throw new IllegalArgumentException("Invalid expression: Missing closing parenthesis");
            }
        }

        // Parse a number
        return parseNumber(expression, position);
    }

    /**
     */
    private Result parseNumber(String expression, int position) {
        StringBuilder numberBuilder = new StringBuilder();

        // Parse the number
        while (position < expression.length()) {
            char ch = expression.charAt(position);

            if (Character.isDigit(ch) || ch == '.') {
                numberBuilder.append(ch);
                position++;
            } else {
                break;
            }
        }

        if (numberBuilder.length() == 0) {
            throw new IllegalArgumentException("Invalid expression: Expected a number at position " + position);
        }

        return new Result(Double.parseDouble(numberBuilder.toString()), position);
    }

    /**
     */
    private static class Result {
        double value;
        int position;

        Result(double value, int position) {
            this.value = value;
            this.position = position;
        }
    }
}