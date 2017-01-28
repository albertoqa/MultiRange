package multirange.sample;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.stage.Stage;
import multirange.MultiRange;

import java.util.Date;

/**
 * Created by alberto on 10/12/2016.
 */
public class MultiRangeSample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        MultiRange multiRange = new MultiRange();

        multiRange.setMax(40);
        multiRange.setMin(0);
        //multiRange.setLabelFormatter(new DateStringConverter(true));   // timeFormat
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
