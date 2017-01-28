package multirange.sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import multirange.MultiRange;

/**
 * Created by alberto on 10/12/2016.
 */
public class MultiRangeSample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        MultiRange multiRange = new MultiRange(0, 40);
        multiRange.setShowTickMarks(true);
        multiRange.setShowTickLabels(true);
        multiRange.setMinorTickCount(2);
        multiRange.setMajorTickUnit(2);
        multiRange.setSnapToTicks(true);

        primaryStage.setScene(new Scene(multiRange));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
