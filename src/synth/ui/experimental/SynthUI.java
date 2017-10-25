package synth.ui.experimental;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SynthUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Monoid");

        StackPane root = new StackPane();
        root.getChildren().add(label);

        Scene scene = new Scene(root, 640, 480);

        primaryStage.setTitle("Monoid");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
