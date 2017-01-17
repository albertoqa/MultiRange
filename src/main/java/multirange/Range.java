package multirange;

/**
 * Created by alberto on 09/01/2017.
 */
public class Range {

    private int id;
    private Double low;
    private Double high;

    Range(int id, Double low, Double high) {
        this.id = id;
        this.low = low;
        this.high = high;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }
}
