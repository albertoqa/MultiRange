package multirange;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by alberto on 09/01/2017.
 */
public class Range {

    private DoubleProperty low;
    private DoubleProperty high;

    public Range(Double low, Double high) {
        this.low = new SimpleDoubleProperty(low);
        this.high = new SimpleDoubleProperty(high);
    }

    public double getLow() {
        return low.get();
    }

    public DoubleProperty lowProperty() {
        return low;
    }

    public void setLow(double low) {
        this.low.set(low);
    }

    public double getHigh() {
        return high.get();
    }

    public DoubleProperty highProperty() {
        return high;
    }

    public void setHigh(double high) {
        this.high.set(high);
    }
}
