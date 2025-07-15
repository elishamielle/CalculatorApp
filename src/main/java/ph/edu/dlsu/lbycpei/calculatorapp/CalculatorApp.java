package ph.edu.dlsu.lbycpei.calculatorapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ph.edu.dlsu.lbycpei.calculatorapp.controller.CalculatorController;
import ph.edu.dlsu.lbycpei.calculatorapp.model.CalculatorModel;
import ph.edu.dlsu.lbycpei.calculatorapp.view.CalculatorView;

/**
 * Main Application class for the Scientific Calculator
 * Follows MVC pattern with proper separation of concerns
 */
public class CalculatorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create MVC components
        CalculatorModel model = new CalculatorModel();
        CalculatorView view = new CalculatorView();
        CalculatorController controller = new CalculatorController(model, view);
        controller.run();

        // Setup scene
        Scene scene = new Scene(view.getRoot(), 400, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Configure stage
        primaryStage.setTitle("LBYCPEI Calculator - Elisha");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}