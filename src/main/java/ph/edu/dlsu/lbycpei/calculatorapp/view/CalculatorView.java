package ph.edu.dlsu.lbycpei.calculatorapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.Map;

public class CalculatorView {
    private VBox root;
    private TextField display;
    private Map<String, Button> buttons;

    public CalculatorView() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getStyleClass().add("calculator-body");

        // Display
        display = new TextField("0");
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.getStyleClass().add("display");
        display.setFont(Font.font("Monospace", 24));

        // Initialize buttons map
        buttons = new HashMap<>();
        createButtons();
    }

    private void createButtons() {
        // Define button labels and their CSS classes
        String[][] buttonLayout = {
                {"(", ")", "C", "CE"},
                {"sin", "cos", "tan", "√"},
                {"ln", "log", "^", "!"},
                {"π", "abs", "x²", "%"},
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"}
        };

        for (String[] row : buttonLayout) {
            for (String label : row) {
                Button button = new Button(label);
                button.setPrefSize(80, 60);
                button.setFont(Font.font("Arial", 14));

                // Apply specific CSS classes
                if (isOperator(label)) {
                    button.getStyleClass().add("operator-button");
                } else if (isScientific(label)) {
                    button.getStyleClass().add("scientific-button");
                } else if (isNumber(label)) {
                    button.getStyleClass().add("number-button");
                } else {
                    button.getStyleClass().add("function-button");
                }
                buttons.put(label, button);
            }
        }
    }

    private void setupLayout() {
        // Add display
        root.getChildren().add(display);

        // Create button grid
        String[][] buttonLayout = {
                {"(", ")", "C", "CE"},
                {"sin", "cos", "tan", "√"},
                {"ln", "log", "^", "!"},
                {"π", "abs", "x²", "%"},
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"}
        };

        for (String[] row : buttonLayout) {
            HBox buttonRow = new HBox(5);
            buttonRow.setAlignment(Pos.CENTER);

            for (String label : row) {
                buttonRow.getChildren().add(buttons.get(label));
            }

            root.getChildren().add(buttonRow);
        }
    }

    // Encapsulated getters
    public VBox getRoot() {
        return root;
    }

    public TextField getDisplay() {
        return display;
    }

    public Button getButton(String label) {
        return buttons.get(label);
    }

    public Map<String, Button> getAllButtons() {
        return new HashMap<>(buttons); // Return defensive copy
    }

    // Helper methods for button classification
    private boolean isOperator(String text) {
        return text.matches("[+\\-*/=^]");
    }

    private boolean isScientific(String text) {
        return text.matches("sin|cos|tan|ln|log|√|!");
    }

    private boolean isNumber(String text) {
        return text.matches("[0-9.]");
    }

    public void updateDisplay(String text) {
        display.setText(text);
    }
}