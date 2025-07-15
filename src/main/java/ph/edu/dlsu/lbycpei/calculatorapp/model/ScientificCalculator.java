package ph.edu.dlsu.lbycpei.calculatorapp.model;

import java.util.*;

public class ScientificCalculator extends Calculator {

    @Override
    public double performOperation(String operation, double operand1, double operand2) {
        return switch (operation) {
            case "+" -> operand1 + operand2;
            case "-" -> operand1 - operand2;
            case "*" -> operand1 * operand2;
            case "/" -> {
                if (operand2 == 0) throw new ArithmeticException("Division by zero");
                yield operand1 / operand2;
            }
            case "^" -> Math.pow(operand1, operand2);
            case "%", "mod" -> operand1 % operand2;
            default -> throw new UnsupportedOperationException("Unsupported operation: " + operation);
        };
    }

    // Scientific operations
    public double performScientificOperation(String operation, double operand) {
        return switch (operation.toLowerCase()) {
            case "sin" -> Math.sin(Math.toRadians(operand));
            case "cos" -> Math.cos(Math.toRadians(operand));
            case "tan" -> Math.tan(Math.toRadians(operand));
            case "asin" -> Math.toDegrees(Math.asin(operand));
            case "acos" -> Math.toDegrees(Math.acos(operand));
            case "atan" -> Math.toDegrees(Math.atan(operand));
            case "log" -> Math.log10(operand);
            case "ln" -> Math.log(operand);
            case "sqrt" -> Math.sqrt(operand);
            case "cbrt" -> Math.cbrt(operand);
            case "exp" -> Math.exp(operand);
            case "abs" -> Math.abs(operand);
            case "factorial" -> factorial(operand);
            case "reciprocal" -> {
                if (operand == 0) throw new ArithmeticException("Division by zero");
                yield 1.0 / operand;
            }
            default -> throw new UnsupportedOperationException("Unsupported scientific operation: " + operation);
        };
    }

    private double factorial(double n) {
        if (n < 0 || n != Math.floor(n)) throw new IllegalArgumentException("Factorial only works on non-negative integers.");
        double result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }

    @Override
    public double evaluateExpression(String expression) {
        try {
            expression = expression.replaceAll("π", String.valueOf(Math.PI));

            expression = expression.replaceAll("\\(\\-([\\d\\.]+)\\)", "(0-$1)");

            return evaluatePostfix(toPostfixList(expression));
        } catch (Exception e) {
            throw new ArithmeticException("Invalid expression: " + expression);
        }
    }


    // ✅ RENAMED this method to avoid override conflict
    protected List<String> toPostfixList(String infix) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        StringTokenizer tokenizer = new StringTokenizer(infix, "+-*/^()% ", true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) continue;

            if (token.matches("-?\\d+(\\.\\d+)?")) {
                output.add(token);
            } else if ("(".equals(token)) {
                stack.push(token);
            } else if (")".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty()) stack.pop(); // remove "("
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();
        for (String token : postfix) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+" -> stack.push(a + b);
                    case "-" -> stack.push(a - b);
                    case "*" -> stack.push(a * b);
                    case "/" -> stack.push(a / b);
                    case "^" -> stack.push(Math.pow(a, b));
                    case "%" -> stack.push(a % b);
                    default -> throw new UnsupportedOperationException("Unknown operator: " + token);
                }
            }
        }
        return stack.pop();
    }

    private boolean isOperator(String token) {
        return "+-*/^%".contains(token);
    }

    private int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "/", "%" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }
}