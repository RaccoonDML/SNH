package jimageprocessor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SNH extends Application{

    public static void main(String[] args) {

        launch(args);
    }

    //test-by DML
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));   // Settings for elements on the screen
        primaryStage.setTitle("算你狠");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
