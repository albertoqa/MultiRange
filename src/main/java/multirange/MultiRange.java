package multirange;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import multirange.behavior.MultiRangeBehavior;
import multirange.skin.MultiRangeSkin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by alberto on 09/01/2017.
 */
public class MultiRange extends Control implements MultiRangeAPI {

    /**
     * Creates a default (horizontal) multiRange instance.
     */
    public MultiRange() {
        this(0, 1.0);
    }

    /**
     * Instantiates a default (horizontal) multiRange with the specified min/max values.
     *
     * @param min Selector minimum value
     * @param max Selector maximum value
     */
    public MultiRange(double min, double max) {
        setMin(min);
        setMax(max);

        rangesProperty().get().add(new Range(1, min, max));
    }

    /***************************************************************************
     *                                                                         *
     *                              Properties                                 *
     *                                                                         *
     **************************************************************************/

    private ListProperty<Range> ranges;

    public ObservableList<Range> getRanges() {
        return ranges.get();
    }

    public ListProperty<Range> rangesProperty() {
        if (ranges == null) {
            ranges = new SimpleListProperty<>(this, "ranges", FXCollections.observableArrayList());
        }
        return ranges;
    }

    public void setRanges(ObservableList<Range> ranges) {
        this.ranges.set(ranges);
    }

    private int lastId = 1;

    public void setLowRangeValue(int id, double newValue) {
        Optional<Range> rangeOptional = ranges.stream().filter(r -> r.getId() == id).findAny();
        if (rangeOptional.isPresent()) {
            Range range = rangeOptional.get();
            int index = ranges.indexOf(range);
            range.setLow(newValue);

            // need to remove and insert to notify of a change in an element of the list
            ranges.set(index, null);
            ranges.set(index, range);
        }
        lastId = id;
    }

    public double getLowValue() {
        Optional<Range> rangeOptional = ranges.stream().filter(r -> r.getId() == lastId).findAny();
        if (rangeOptional.isPresent()) {
            return rangeOptional.get().getLow();
        }
        return -1;
    }

    private DoubleProperty max;

    public final void setMax(double value) {
        maxProperty().set(value);
    }

    public final double getMax() {
        return max == null ? 100 : max.get();
    }

    public final DoubleProperty maxProperty() {
        if (max == null) {
            max = new DoublePropertyBase(100) {
                @Override
                protected void invalidated() {
                    if (get() < getMin()) {
                        setMin(get());
                    }
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "max";
                }
            };
        }
        return max;
    }


    private DoubleProperty min;

    public final void setMin(double value) {
        minProperty().set(value);
    }

    public final double getMin() {
        return min == null ? 0 : min.get();
    }

    public final DoubleProperty minProperty() {
        if (min == null) {
            min = new DoublePropertyBase(0) {
                @Override
                protected void invalidated() {
                    if (get() > getMax()) {
                        setMax(get());
                    }
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "min";
                }
            };
        }
        return min;
    }


    /***************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "multi-range";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return MultiRange.class.getResource("/multirange/multirange.css").toExternalForm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MultiRangeSkin(this, new MultiRangeBehavior(this));
    }

    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     * @since JavaFX 8.0
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return MultiRange.StyleableProperties.STYLEABLES;
    }

    /**
     * RT-19263
     *
     * @treatAsPrivate implementation detail
     * @since JavaFX 8.0
     * @deprecated This is an experimental API that is not intended for general use and is subject to change in future versions
     */
    @Deprecated
    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

}
