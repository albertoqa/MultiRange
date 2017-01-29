package multirange.sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import multirange.MultiRange;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alberto on 28/01/2017.
 */
public class Control implements Initializable {

    @FXML private MultiRange multiRange;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        multiRange.setMin(1.479617941000E12);
        multiRange.setMax(1.480351741000E12);
        multiRange.setHighRangeValue(1.480351741000E12);
        multiRange.setLowRangeValue(1.479917941000E12);
        multiRange.setShowTickMarks(true);
        multiRange.setShowTickLabels(true);

        double timeInBetween = 1.480351741000E12 - 1.479617941000E12;
        int majorTickUnit = (int) timeInBetween / 8;

        multiRange.setMajorTickUnit(majorTickUnit);
    }
}
