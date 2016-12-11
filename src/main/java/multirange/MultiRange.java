package multirange;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Pair;
import multirange.skin.MultiRangeSkin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiRange extends Control implements MultiRangeAPI {

    /**
     * Creates a default (horizontal) MultiRange instance.
     */
    public MultiRange() {
        this(0, 1.0);
    }

    /**
     * Instantiates a default (horizontal) MultiRange with the specified min/max values.
     *
     * @param min Selector minimum value
     * @param max Selector maximum value
     */
    public MultiRange(double min, double max) {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        setMax(max);
        setMin(min);
        adjustValues();
    }

    //***************************************************************************************

    /**
     * Ensures that min is always < max, that value is always
     * somewhere between the two, and that if snapToTicks is set then the
     * value will always be set to align with a tick mark.
     */
    private void adjustValues() {

    }

    /***************************************************************************
     *                                                                         *
     *                              Properties                                 *
     *                                                                         *
     **************************************************************************/


    private DoubleProperty rangeA;

    public final void setRangeA(double value) {
        rangeAProperty().set(value);
    }

    public final Double getRangeA() {
        return rangeA == null ? null : rangeA.getValue();
    }

    public final DoubleProperty rangeAProperty() {

        // TODO


        return rangeA;
    }


    private DoubleProperty rangeB;

    public final void setRangeB(double value) {
        rangeBProperty().set(value);

        ranges.add(new Pair<>(rangeA, rangeB));
        rangeA = null;
        rangeB = null;
    }

    public final double getRangeB() {
        return rangeB == null ? 100 : rangeB.get();
    }

    public final DoubleProperty rangeBProperty() {

        if (rangeB == null) {

        }

        // TODO

        return rangeB;
    }


    private List<Pair<DoubleProperty, DoubleProperty>> ranges = new ArrayList<>();

    public List<Pair<DoubleProperty, DoubleProperty>> getRanges() {
        return ranges;
    }

    /***************************************************************************
     *                                                                         *
     *                    Properties copied from Slider                        *
     *                                                                         *
     **************************************************************************/

    /**
     * The maximum value represented by this Slider. This must be a
     * value greater than {@link #minProperty() min}.
     */
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
                    adjustValues();
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

    /**
     * The minimum value represented by this Slider. This must be a
     * value less than {@link #maxProperty() max}.
     */
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
                    adjustValues();
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
        return MultiRange.class.getResource("multirange.css").toExternalForm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MultiRangeSkin(this);
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

    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");


}
