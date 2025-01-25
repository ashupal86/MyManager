package com.example.mymanager2;

import java.util.Stack;

public class Expression {

    // Method to evaluate a mathematical expression
    public static double evaluate(String expression) throws Exception {
        if (expression == null || expression.isEmpty()) {
            throw new Exception("Invalid expression");
        }

        // Remove any spaces from the expression
        expression = expression.replaceAll("\\s+", "");
        expression = expression.replace("×", "*");
        expression = expression.replace("÷", "/");

        // Stacks for numbers and operators
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            // If the current character is a digit or a decimal point
            if (Character.isDigit(c) || c == '.') {
                StringBuilder numberBuilder = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numberBuilder.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(numberBuilder.toString()));
                continue;
            }

            // If the current character is an operator
            if (isOperator(c)) {
                // Process previous operators with higher or equal precedence
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    char op = operators.pop();
                    numbers.push(applyOperator(a, b, op));
                }
                operators.push(c);
            }

            // If the current character is a left parenthesis
            if (c == '(') {
                operators.push(c);
            }

            // If the current character is a right parenthesis
            if (c == ')') {
                while (operators.peek() != '(') {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    char op = operators.pop();
                    numbers.push(applyOperator(a, b, op));
                }
                operators.pop(); // Remove the '(' from the stack
            }

            i++;
        }

        // Process remaining operators
        while (!operators.isEmpty()) {
            double b = numbers.pop();
            double a = numbers.pop();
            char op = operators.pop();
            numbers.push(applyOperator(a, b, op));
        }

        return numbers.pop();
    }

    // Helper method to check if a character is an operator
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    // Helper method to get the precedence of an operator
    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            default:
                return -1;
        }
    }

    // Helper method to apply an operator to two operands
    private static double applyOperator(double a, double b, char operator) throws Exception {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new Exception("Division by zero");
                }
                return a / b;
            case '%':
                if (b == 0) {
                    throw new Exception("Division by zero");
                }
                return a % b;
            default:
                throw new Exception("Invalid operator");
        }
    }
}
