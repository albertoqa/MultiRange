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
        multiRange.setMin(0);
        multiRange.setMax(100);
        multiRange.setShowTickMarks(true);
        multiRange.setShowTickLabels(true);
        multiRange.setMajorTickUnit(10);
        multiRange.setMinorTickCount(5);
    }
}
