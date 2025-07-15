package ph.edu.dlsu.lbycpei.calculatorapp.controller;

import javafx.scene.control.Button;
import ph.edu.dlsu.lbycpei.calculatorapp.model.CalculatorModel;
import ph.edu.dlsu.lbycpei.calculatorapp.view.CalculatorView;

public class CalculatorController {
    private CalculatorModel model;
    private CalculatorView view;
    private StringBuilder currentInput;
    private boolean waitingForOperand;

    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;
        this.currentInput = new StringBuilder();
        this.waitingForOperand = false;
    }

    public void run() {
        initializeEventHandlers();
    }

    private void initializeEventHandlers() {
        for (String num : new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "."}) {
            Button button = view.getButton(num);
            if (button != null) {
                button.setOnAction(e -> handleNumberInput(num));
            }
        }

        for (String op : new String[]{"+", "-", "*", "/", "%", "^"}) {
            Button button = view.getButton(op);
            if (button != null) {
                button.setOnAction(e -> handleInput(op));
            }
        }

        setupScientificButtons();

        Button absButton = view.getButton("abs");
        if (absButton != null) {
            absButton.setOnAction(e -> handleAbsoluteValue());
        }

        Button squareButton = view.getButton("x²");
        if (squareButton != null) {
            squareButton.setOnAction(e -> handleSpecialFunction("square"));
        }

        Button sqrtButton = view.getButton("√");
        if (sqrtButton != null) {
            sqrtButton.setOnAction(e -> handleSpecialFunction("sqrt"));
        }

        Button piButton = view.getButton("π");
        if (piButton != null) {
            piButton.setOnAction(e -> handlePi());
        }

        Button equalsButton = view.getButton("=");
        if (equalsButton != null) {
            equalsButton.setOnAction(e -> handleEquals());
        }

        Button clearButton = view.getButton("C");
        if (clearButton != null) {
            clearButton.setOnAction(e -> handleClear());
        }

        Button clearEntryButton = view.getButton("CE");
        if (clearEntryButton != null) {
            clearEntryButton.setOnAction(e -> handleClearEntry());
        }

        Button leftParenButton = view.getButton("(");
        if (leftParenButton != null) {
            leftParenButton.setOnAction(e -> handleInput("("));
        }

        Button rightParenButton = view.getButton(")");
        if (rightParenButton != null) {
            rightParenButton.setOnAction(e -> handleInput(")"));
        }
    }

    private void setupScientificButtons() {
        String[][] scientificOps = {
                {"sin", "sin"},
                {"cos", "cos"},
                {"tan", "tan"},
                {"ln", "ln"},
                {"log", "log"},
                {"√", "sqrt"},
                {"!", "factorial"},
                {"^", "power"}
        };

        for (String[] pair : scientificOps) {
            setupScientificButtons(pair[0], pair[1]);
        }
    }

    private void setupScientificButtons(String label, String function) {
        Button sciButton = view.getButton(label);
        if (sciButton != null) {
            if ("power".equals(function)) {
                sciButton.setOnAction(e -> handleInput("^"));
            } else {
                sciButton.setOnAction(e -> handleScientificOperation(function));
            }
        }
    }

    private void handleNumberInput(String number) {
        if (model.isResultDisplayed()) {
            currentInput.setLength(0);
            model.setResultDisplayed(false);
        }

        if (waitingForOperand) {
            currentInput.setLength(0);
            waitingForOperand = false;
        }

        if (number.equals(".")) {
            String[] tokens = currentInput.toString().split("[+\\-*/^()]");
            String lastToken = tokens.length > 0 ? tokens[tokens.length - 1] : "";
            if (lastToken.contains(".")) {
                return;
            }
        }

        currentInput.append(number);
        updateDisplay();
    }

    private void handleInput(String input) {
        boolean isOperator = input.matches("[+\\-*/^%]");

        // Handle factorial directly (special case)
        if (input.equals("!")) {
            try {
                double value = Double.parseDouble(currentInput.toString());
                double result = model.performScientificOperation("factorial", value);
                displayResult(result);
            } catch (Exception e) {
                view.updateDisplay("Error");
                model.setResultDisplayed(true);
            }
            return;
        }

        if (model.isResultDisplayed()) {
            model.setResultDisplayed(false);
        }

        // Prevent invalid double operators like "++", "*-", etc.
        if (!currentInput.isEmpty() && isOperator) {
            char lastChar = currentInput.charAt(currentInput.length() - 1);

            if ("+-*/^%".indexOf(lastChar) != -1) {
                if (lastChar == ')' || input.equals("(")) {
                    currentInput.append(input);
                    updateDisplay();
                    return;
                }

                currentInput.setCharAt(currentInput.length() - 1, input.charAt(0));
                updateDisplay();
                return;
            }
        }

        currentInput.append(input);
        updateDisplay();
    }

    private void handleScientificOperation(String operation) {
        try {
            // Get the numeric value currently shown on the display
            double value = getCurrentDisplayValue();

            // Apply the selected scientific function to the value
            double result = model.performScientificOperation(operation, value);

            // Display the computed result
            displayResult(result);
        } catch (Exception e) {
            view.updateDisplay("Error");
            model.setResultDisplayed(true);
        }
    }

    private void handlePi() {
        // If the last character is a digit, insert multiplication before π
        if (!currentInput.isEmpty()) {
            char lastChar = currentInput.charAt(currentInput.length() - 1);
            if (Character.isDigit(lastChar)) {
                currentInput.append("*");
            }
        }
        // Append the value of π and update the display with its symbol
        currentInput.append(Math.PI);
        String displayValue = currentInput.toString().replace(String.valueOf(Math.PI), "π");
        view.updateDisplay(displayValue);
        model.setDisplayText(currentInput.toString());
    }

    private void handleSpecialFunction(String func) {
        try {
            double value = Double.parseDouble(currentInput.toString());
            double result;

            switch (func) {
                case "square":
                    result = value * value;
                    break;
                case "sqrt":
                    result = Math.sqrt(value);
                    break;
                default:
                    throw new IllegalStateException("Unknown function: " + func);
            }

            currentInput = new StringBuilder(Double.toString(result));
            updateDisplay();
        } catch (Exception e) {
            view.updateDisplay("Error");
        }
    }

    private void handleEquals() {
        try {
            if (currentInput.length() > 0) {
                String expression = currentInput.toString();

                // Basic check for division by zero
                if (expression.contains("/0")) {
                    throw new ArithmeticException("Division by zero");
                }

                double result = model.evaluateExpression(expression);
                displayResult(result);
            }
        } catch (Exception e) {
            view.updateDisplay("Error");
            model.setResultDisplayed(true);
        }
    }


    private void handleClear() {
        model.clear();
        currentInput.setLength(0);
        waitingForOperand = false;
        view.updateDisplay("0");
    }

    private void handleClearEntry() {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
        }

        if (currentInput.length() == 0) {
            view.updateDisplay("0");
        } else {
            updateDisplay();
        }

        waitingForOperand = false;
    }

    private void displayResult(double result) {
        model.setCurrentValue(result);
        String resultText = formatResult(result);
        view.updateDisplay(resultText);
        model.setDisplayText(resultText);
        model.setResultDisplayed(true);
        currentInput.setLength(0);
        currentInput.append(result);
    }

    private void updateDisplay() {
        String displayText = !currentInput.isEmpty() ? currentInput.toString() : "0";
        view.updateDisplay(displayText);
        model.setDisplayText(displayText);
    }

    private double getCurrentDisplayValue() {
        try {
            String displayText = view.getDisplay().getText();
            if (displayText.equals("Error") || displayText.isEmpty()) {
                return 0.0;
            }

            String[] parts = displayText.split("[^0-9\\.]+");
            for (int i = parts.length - 1; i >= 0; i--) {
                try {
                    return Double.parseDouble(parts[i]);
                } catch (NumberFormatException ignored) {
                }
            }

            return model.getCurrentValue();
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String formatResult(double result) {
        if (result == Math.floor(result) && Double.isFinite(result)) {
            return String.format("%.0f", result);
        } else {
            return String.format("%.10g", result);
        }
    }

    private void handleAbsoluteValue() {
        try {
            double value = getCurrentDisplayValue();
            double result = Math.abs(value);
            displayResult(result);
        } catch (Exception e) {
            view.updateDisplay("Error");
            model.setResultDisplayed(true);
        }
    }
}
