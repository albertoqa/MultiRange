package multirange.sample;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.stage.Stage;
import multirange.MultiRange;

/**
 * Created by alberto on 10/12/2016.
 */
public class MultiRangeSample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        MultiRange multiRange = new MultiRange(0, 100);
        multiRange.setShowTickLabels(true);
        multiRange.setShowTickMarks(true);
        multiRange.setMinorTickCount(10);
        multiRange.setSnapToTicks(true);

        primaryStage.setScene(new Scene(multiRange));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
