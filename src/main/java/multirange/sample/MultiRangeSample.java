package multirange.sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import multirange.MultiRange;
import multirange.MultiRange1;
import multirange.RangeSlider.RangeSlider;

/**
 * Created by alberto on 10/12/2016.
 */
public class MultiRangeSample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(new MultiRange()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
