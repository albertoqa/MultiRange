package multirange.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by alberto on 10/12/2016.
 */
public class MultiRangeSample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = FXMLLoader.load(getClass().getResource("/fxml/start.fxml"));
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}