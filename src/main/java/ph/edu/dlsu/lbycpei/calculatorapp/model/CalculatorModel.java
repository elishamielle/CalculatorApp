package ph.edu.dlsu.lbycpei.calculatorapp.model;

public class CalculatorModel {
    private double currentValue;
    private String displayText;
    private boolean isResultDisplayed;
    private Calculator calculator;

    public CalculatorModel() {
        this.calculator = new ScientificCalculator();
        this.currentValue = 0.0;
        this.displayText = "0";
        this.isResultDisplayed = false;
    }

    // Encapsulated getters
    public double getCurrentValue() {
        return currentValue;
    }

    public String getDisplayText() {
        return displayText;
    }

    public boolean isResultDisplayed() {
        return isResultDisplayed;
    }

    // Encapsulated setters
    public void setCurrentValue(double value) {
        this.currentValue = value;
    }

    public void setDisplayText(String text) {
        this.displayText = text;
    }

    public void setResultDisplayed(boolean displayed) {
        this.isResultDisplayed = displayed;
    }

    // Calculator operations delegation
    public double performOperation(String operation, double operand1, double operand2) {
        return calculator.performOperation(operation, operand1, operand2);
    }

    public double performScientificOperation(String operation, double operand) {
        if (calculator instanceof ScientificCalculator) {
            return ((ScientificCalculator) calculator).performScientificOperation(operation, operand);
        }
        return operand;
    }

    public double evaluateExpression(String expression) {
        return calculator.evaluateExpression(expression);
    }

    public void clear() {
        this.currentValue = 0.0;
        this.displayText = "0";
        this.isResultDisplayed = false;
    }
}

