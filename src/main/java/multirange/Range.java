package multirange;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Define a new range with an id, a low and a high values.
 * <p>
 * Created by alberto on 09/01/2017.
 */
public class Range {

    private int id;
    private DoubleProperty low;
    private DoubleProperty high;

    Range(int id, Double low, Double high) {
        this.id = id;
        this.low = new SimpleDoubleProperty(low);
        this.high = new SimpleDoubleProperty(high);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getAmplitude() {
        return getHigh() - getLow();
    }
}
