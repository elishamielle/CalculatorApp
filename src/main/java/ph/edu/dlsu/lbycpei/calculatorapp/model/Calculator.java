package ph.edu.dlsu.lbycpei.calculatorapp.model;

import java.util.Stack;

public abstract class Calculator {
    protected static final double PI = Math.PI;
    protected static final double E = Math.E;

    // Template method pattern
    public abstract double performOperation(String operation, double operand1, double operand2);

    // Common method for expression evaluation
    public double evaluateExpression(String expression) {
        try {
            return evaluatePostfix(infixToPostfix(expression));
        } catch (Exception e) {
            throw new ArithmeticException("Invalid expression");
        }
    }

    // Helper methods with protected access (encapsulation)
    protected String infixToPostfix(String infix) {
        StringBuilder result = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : infix.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                result.append(c);
            } else if (c == ' ') {
                result.append(' ');
            } else if (isOperator(c)) {
                result.append(' ');
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                    result.append(stack.pop()).append(' ');
                }
                stack.push(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    result.append(' ').append(stack.pop());
                }
                stack.pop();
            }
        }

        while (!stack.isEmpty()) {
            result.append(' ').append(stack.pop());
        }

        return result.toString();
    }

    protected double evaluatePostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.trim().split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (isOperator(token.charAt(0)) && token.length() == 1) {
                if (stack.size() < 2) throw new ArithmeticException("Invalid expression");
                double b = stack.pop();
                double a = stack.pop();
                stack.push(performOperation(token, a, b));
            } else {
                stack.push(Double.parseDouble(token));
            }
        }

        return stack.pop();
    }

    protected boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '%';
    }

    protected int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return -1;
        }
    }
}