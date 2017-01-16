package multirange;

import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.EnumConverter;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.geometry.Orientation;
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
public class MultiRange extends Control {

    /**
     * Creates a default (horizontal) multiRange instance using default values of 0.0,
     * 1.0 for min/max respectively.
     */
    public MultiRange() {
        this(0, 1.0);
    }

    /**
     * Instantiates a default (horizontal) multiRange with the specified min/max values.
     *
     * @param min The minimum allowable value that the MultiRange will allow
     * @param max The maximum allowable value that the MultiRange will allow
     */
    public MultiRange(double min, double max) {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        setMin(min);
        setMax(max);

        // TODO
        //adjustValues();
        //setLowValue(lowValue);
        //setHighValue(highValue);

        // Add the first range to the slider with the values min/max for the
        // lower/higher values respectively.
        ranges.add(new Range(currentRangeId.get(), min, max));
    }

    /***************************************************************************
     *                                                                         *
     *                              Properties                                 *
     *                                                                         *
     **************************************************************************/

    private List<Range> ranges = new ArrayList<>();
    private IntegerProperty currentRangeId = new SimpleIntegerProperty(0);
    private BooleanProperty valueChanging;

    public int getCurrentRangeId() {
        return currentRangeId.get();
    }

    public IntegerProperty currentRangeIdProperty() {
        return currentRangeId;
    }

    public void setCurrentRangeId(int currentRangeId) {
        this.currentRangeId.set(currentRangeId);
    }

    /**
     * Create a new range
     *
     * @param low
     * @param high
     */
    public void createNewRange(double low, double high) {
        ranges.add(new Range(currentRangeId.get(), low, high));
        valueChangingProperty().setValue(true);
    }

    public BooleanProperty valueChangingProperty() {
        if (valueChanging == null) {
            valueChanging = new SimpleBooleanProperty(false);
        }
        return valueChanging;
    }

    public void setValueChanging(boolean valueChanging) {
        this.valueChanging.set(valueChanging);
    }

    public void setLowRangeValue(double newValue) {
        setValue(newValue, true);
    }

    public double getLowValue() {
        return getValue(true);
    }

    public void setHighRangeValue(double newValue) {
        setValue(newValue, false);
    }

    public double getHighValue() {
        return getValue(false);
    }

    /***************************************************************************
     *                                                                         *
     * Properties copied from Slider (and slightly edited)                     *
     *                                                                         *
     **************************************************************************/

    private DoubleProperty max;

    /**
     * Sets the maximum value for this Slider.
     *
     * @param value
     */
    public final void setMax(double value) {
        maxProperty().set(value);
    }

    /**
     * @return The maximum value of this slider. 100 is returned if
     * the maximum value has never been set.
     */
    public final double getMax() {
        return max == null ? 100 : max.get();
    }

    /**
     * @return A DoubleProperty representing the maximum value of this Slider.
     * This must be a value greater than {@link #minProperty() min}.
     */
    public final DoubleProperty maxProperty() {
        if (max == null) {
            max = new DoublePropertyBase(100) {
                @Override
                protected void invalidated() {
                    if (get() < getMin()) {
                        setMin(get());
                    }
                    //adjustValues();
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "max"; //$NON-NLS-1$
                }
            };
        }
        return max;
    }

    private DoubleProperty min;

    /**
     * Sets the minimum value for this Slider.
     *
     * @param value
     */
    public final void setMin(double value) {
        minProperty().set(value);
    }

    /**
     * @return the minimum value for this Slider. 0 is returned if the minimum
     * has never been set.
     */
    public final double getMin() {
        return min == null ? 0 : min.get();
    }

    /**
     * @return A DoubleProperty representing The minimum value of this Slider.
     * This must be a value less than {@link #maxProperty() max}.
     */
    public final DoubleProperty minProperty() {
        if (min == null) {
            min = new DoublePropertyBase(0) {
                @Override
                protected void invalidated() {
                    if (get() > getMax()) {
                        setMax(get());
                    }
                    //adjustValues();
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "min"; //$NON-NLS-1$
                }
            };
        }
        return min;
    }

    /**
     *
     */
    private BooleanProperty snapToTicks;

    /**
     * Sets the value of SnapToTicks.
     *
     * @param value
     * @see #snapToTicksProperty()
     */
    public final void setSnapToTicks(boolean value) {
        snapToTicksProperty().set(value);
    }

    /**
     * @return the value of SnapToTicks.
     * @see #snapToTicksProperty()
     */
    public final boolean isSnapToTicks() {
        return snapToTicks == null ? false : snapToTicks.get();
    }

    /**
     * Indicates whether the {@link #lowValueProperty()} value} /
     * {@link #highValueProperty()} value} of the {@code Slider} should always
     * be aligned with the tick marks. This is honored even if the tick marks
     * are not shown.
     *
     * @return A BooleanProperty.
     */
    public final BooleanProperty snapToTicksProperty() {
        if (snapToTicks == null) {
            snapToTicks = new StyleableBooleanProperty(false) {
                @Override
                public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return MultiRange.StyleableProperties.SNAP_TO_TICKS;
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "snapToTicks"; //$NON-NLS-1$
                }
            };
        }
        return snapToTicks;
    }

    /**
     *
     */
    private DoubleProperty majorTickUnit;

    /**
     * Sets the unit distance between major tick marks.
     *
     * @param value
     * @see #majorTickUnitProperty()
     */
    public final void setMajorTickUnit(double value) {
        if (value <= 0) {
            throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0."); //$NON-NLS-1$
        }
        majorTickUnitProperty().set(value);
    }

    /**
     * @return The unit distance between major tick marks.
     * @see #majorTickUnitProperty()
     */
    public final double getMajorTickUnit() {
        return majorTickUnit == null ? 25 : majorTickUnit.get();
    }

    /**
     * The unit distance between major tick marks. For example, if
     * the {@link #minProperty() min} is 0 and the {@link #maxProperty() max} is 100 and the
     * {@link #majorTickUnitProperty() majorTickUnit} is 25, then there would be 5 tick marks: one at
     * position 0, one at position 25, one at position 50, one at position
     * 75, and a final one at position 100.
     * <p>
     * This value should be positive and should be a value less than the
     * span. Out of range values are essentially the same as disabling
     * tick marks.
     *
     * @return A DoubleProperty
     */
    public final DoubleProperty majorTickUnitProperty() {
        if (majorTickUnit == null) {
            majorTickUnit = new StyleableDoubleProperty(25) {
                @Override
                public void invalidated() {
                    if (get() <= 0) {
                        throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0."); //$NON-NLS-1$
                    }
                }

                @Override
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return StyleableProperties.MAJOR_TICK_UNIT;
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "majorTickUnit"; //$NON-NLS-1$
                }
            };
        }
        return majorTickUnit;
    }

    /**
     *
     */
    private IntegerProperty minorTickCount;

    /**
     * Sets the number of minor ticks to place between any two major ticks.
     *
     * @param value
     * @see #minorTickCountProperty()
     */
    public final void setMinorTickCount(int value) {
        minorTickCountProperty().set(value);
    }

    /**
     * @return The number of minor ticks to place between any two major ticks.
     * @see #minorTickCountProperty()
     */
    public final int getMinorTickCount() {
        return minorTickCount == null ? 3 : minorTickCount.get();
    }

    /**
     * The number of minor ticks to place between any two major ticks. This
     * number should be positive or zero. Out of range values will disable
     * disable minor ticks, as will a value of zero.
     *
     * @return An InterProperty
     */
    public final IntegerProperty minorTickCountProperty() {
        if (minorTickCount == null) {
            minorTickCount = new StyleableIntegerProperty(3) {
                @Override
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return MultiRange.StyleableProperties.MINOR_TICK_COUNT;
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "minorTickCount"; //$NON-NLS-1$
                }
            };
        }
        return minorTickCount;
    }

    /**
     *
     */
    private DoubleProperty blockIncrement;

    /**
     * Sets the amount by which to adjust the slider if the track of the slider is
     * clicked.
     *
     * @param value
     * @see #blockIncrementProperty()
     */
    public final void setBlockIncrement(double value) {
        blockIncrementProperty().set(value);
    }

    /**
     * @return The amount by which to adjust the slider if the track of the slider is
     * clicked.
     * @see #blockIncrementProperty()
     */
    public final double getBlockIncrement() {
        return blockIncrement == null ? 10 : blockIncrement.get();
    }

    /**
     * The amount by which to adjust the slider if the track of the slider is
     * clicked. This is used when manipulating the slider position using keys. If
     * {@link #snapToTicksProperty() snapToTicks} is true then the nearest tick mark to the adjusted
     * value will be used.
     *
     * @return A DoubleProperty
     */
    public final DoubleProperty blockIncrementProperty() {
        if (blockIncrement == null) {
            blockIncrement = new StyleableDoubleProperty(10) {
                @Override
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return MultiRange.StyleableProperties.BLOCK_INCREMENT;
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "blockIncrement"; //$NON-NLS-1$
                }
            };
        }
        return blockIncrement;
    }

    /**
     *
     */
    private ObjectProperty<Orientation> orientation;

    /**
     * Sets the orientation of the Slider.
     *
     * @param value
     */
    public final void setOrientation(Orientation value) {
        orientationProperty().set(value);
    }

    /**
     * @return The orientation of the Slider. {@link Orientation#HORIZONTAL} is
     * returned by default.
     */
    public final Orientation getOrientation() {
        return orientation == null ? Orientation.HORIZONTAL : orientation.get();
    }

    /**
     * The orientation of the {@code Slider} can either be horizontal
     * or vertical.
     *
     * @return An Objectproperty representing the orientation of the Slider.
     */
    public final ObjectProperty<Orientation> orientationProperty() {
        if (orientation == null) {
            orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
                @Override
                protected void invalidated() {
                    final boolean vertical = (get() == Orientation.VERTICAL);
                    pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, vertical);
                    pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, !vertical);
                }

                @Override
                public CssMetaData<? extends Styleable, Orientation> getCssMetaData() {
                    return MultiRange.StyleableProperties.ORIENTATION;
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "orientation"; //$NON-NLS-1$
                }
            };
        }
        return orientation;
    }

    private BooleanProperty showTickLabels;

    /**
     * Sets whether labels of tick marks should be shown or not.
     *
     * @param value
     */
    public final void setShowTickLabels(boolean value) {
        showTickLabelsProperty().set(value);
    }

    /**
     * @return whether labels of tick marks are being shown.
     */
    public final boolean isShowTickLabels() {
        return showTickLabels == null ? false : showTickLabels.get();
    }

    /**
     * Indicates that the labels for tick marks should be shown. Typically a
     * {@link Skin} implementation will only show labels if
     * {@link #showTickMarksProperty() showTickMarks} is also true.
     *
     * @return A BooleanProperty
     */
    public final BooleanProperty showTickLabelsProperty() {
        if (showTickLabels == null) {
            showTickLabels = new StyleableBooleanProperty(false) {
                @Override
                public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return MultiRange.StyleableProperties.SHOW_TICK_LABELS;
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "showTickLabels"; //$NON-NLS-1$
                }
            };
        }
        return showTickLabels;
    }

    /**
     *
     */
    private BooleanProperty showTickMarks;

    /**
     * Specifies whether the {@link Skin} implementation should show tick marks.
     *
     * @param value
     */
    public final void setShowTickMarks(boolean value) {
        showTickMarksProperty().set(value);
    }

    /**
     * @return whether the {@link Skin} implementation should show tick marks.
     */
    public final boolean isShowTickMarks() {
        return showTickMarks == null ? false : showTickMarks.get();
    }

    /**
     * @return A BooleanProperty that specifies whether the {@link Skin}
     * implementation should show tick marks.
     */
    public final BooleanProperty showTickMarksProperty() {
        if (showTickMarks == null) {
            showTickMarks = new StyleableBooleanProperty(false) {
                @Override
                public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return MultiRange.StyleableProperties.SHOW_TICK_MARKS;
                }

                @Override
                public Object getBean() {
                    return MultiRange.this;
                }

                @Override
                public String getName() {
                    return "showTickMarks"; //$NON-NLS-1$
                }
            };
        }
        return showTickMarks;
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private void setValue(double newValue, boolean isLow) {
        Optional<Range> rangeOptional = ranges.stream().filter(r -> r.getId() == currentRangeId.get()).findAny();
        if (rangeOptional.isPresent()) {
            Range range = rangeOptional.get();
            int index = ranges.indexOf(range);

            if (isLow) range.setLow(newValue);
            else range.setHigh(newValue);

            // need to remove and insert to notify of a change in an element of the list
            //ranges.set(index, null);
            ranges.set(index, range);
            valueChangingProperty().setValue(true);
        }
    }

    private double getValue(boolean isLow) {
        Optional<Range> rangeOptional = ranges.stream().filter(r -> r.getId() == currentRangeId.get()).findAny();
        if (rangeOptional.isPresent()) {
            if (isLow) return rangeOptional.get().getLow();
            else return rangeOptional.get().getHigh();
        }
        return -1;
    }

    private double snapValueToTicks(double d) {
        double d1 = d;
        if (isSnapToTicks()) {
            double d2 = 0.0D;
            if (getMinorTickCount() != 0) {
                d2 = getMajorTickUnit() / (double) (Math.max(getMinorTickCount(), 0) + 1);
            } else {
                d2 = getMajorTickUnit();
            }
            int i = (int) ((d1 - getMin()) / d2);
            double d3 = (double) i * d2 + getMin();
            double d4 = (double) (i + 1) * d2 + getMin();
            d1 = Utils.nearest(d3, d1, d4);
        }
        return Utils.clamp(getMin(), d1, getMax());
    }


//    /**
//     * Ensures that min is always < max, that value is always
//     * somewhere between the two, and that if snapToTicks is set then the
//     * value will always be set to align with a tick mark.
//     */
//    private void adjustValues() {
//        adjustLowValues();
//        adjustHighValues();
//    }
//
//    private void adjustLowValues() {
//        /**
//         * We first look if the LowValue is between the min and max.
//         */
//        if (getLowValue() < getMin() || getLowValue() > getMax()) {
//            double value = Utils.clamp(getMin(), getLowValue(), getMax());
//            setLowValue(value);
//            /**
//             * If the LowValue seems right, we check if it's not superior to
//             * HighValue ONLY if the highValue itself is right. Because it may
//             * happen that the highValue has not yet been computed and is
//             * wrong, and therefore force the lowValue to change in a wrong way
//             * which may end up in an infinite loop.
//             */
//        } else if (getLowValue() >= getHighValue() && (getHighValue() >= getMin() && getHighValue() <= getMax())) {
//            double value = Utils.clamp(getMin(), getLowValue(), getHighValue());
//            setLowValue(value);
//        }
//    }
//
//    private void adjustHighValues() {
//        if (getHighValue() < getMin() || getHighValue() > getMax()) {
//            setHighValue(Utils.clamp(getMin(), getHighValue(), getMax()));
//        } else if (getHighValue() < getLowValue() && (getLowValue() >= getMin() && getLowValue() <= getMax())) {
//            setHighValue(Utils.clamp(getLowValue(), getHighValue(), getMax()));
//        }
//    }


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
        private static final CssMetaData<MultiRange, Number> BLOCK_INCREMENT =
                new CssMetaData<MultiRange, Number>("-fx-block-increment", SizeConverter.getInstance(), 10.0) {

                    @Override
                    public boolean isSettable(MultiRange n) {
                        return n.blockIncrement == null || !n.blockIncrement.isBound();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MultiRange n) {
                        return (StyleableProperty<Number>) n.blockIncrementProperty();
                    }
                };

        private static final CssMetaData<MultiRange, Boolean> SHOW_TICK_LABELS =
                new CssMetaData<MultiRange, Boolean>("-fx-show-tick-labels", BooleanConverter.getInstance(), Boolean.FALSE) {

                    @Override
                    public boolean isSettable(MultiRange n) {
                        return n.showTickLabels == null || !n.showTickLabels.isBound();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Boolean> getStyleableProperty(MultiRange n) {
                        return (StyleableProperty<Boolean>) n.showTickLabelsProperty();
                    }
                };

        private static final CssMetaData<MultiRange, Boolean> SHOW_TICK_MARKS =
                new CssMetaData<MultiRange, Boolean>("-fx-show-tick-marks", //$NON-NLS-1$
                        BooleanConverter.getInstance(), Boolean.FALSE) {

                    @Override
                    public boolean isSettable(MultiRange n) {
                        return n.showTickMarks == null || !n.showTickMarks.isBound();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Boolean> getStyleableProperty(MultiRange n) {
                        return (StyleableProperty<Boolean>) n.showTickMarksProperty();
                    }
                };

        private static final CssMetaData<MultiRange, Boolean> SNAP_TO_TICKS =
                new CssMetaData<MultiRange, Boolean>("-fx-snap-to-ticks", //$NON-NLS-1$
                        BooleanConverter.getInstance(), Boolean.FALSE) {

                    @Override
                    public boolean isSettable(MultiRange n) {
                        return n.snapToTicks == null || !n.snapToTicks.isBound();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Boolean> getStyleableProperty(MultiRange n) {
                        return (StyleableProperty<Boolean>) n.snapToTicksProperty();
                    }
                };

        private static final CssMetaData<MultiRange, Number> MAJOR_TICK_UNIT =
                new CssMetaData<MultiRange, Number>("-fx-major-tick-unit", //$NON-NLS-1$
                        SizeConverter.getInstance(), 25.0) {

                    @Override
                    public boolean isSettable(MultiRange n) {
                        return n.majorTickUnit == null || !n.majorTickUnit.isBound();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MultiRange n) {
                        return (StyleableProperty<Number>) n.majorTickUnitProperty();
                    }
                };

        private static final CssMetaData<MultiRange, Number> MINOR_TICK_COUNT =
                new CssMetaData<MultiRange, Number>("-fx-minor-tick-count", //$NON-NLS-1$
                        SizeConverter.getInstance(), 3.0) {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void set(MultiRange node, Number value, StyleOrigin origin) {
                        super.set(node, value.intValue(), origin);
                    }

                    @Override
                    public boolean isSettable(MultiRange n) {
                        return n.minorTickCount == null || !n.minorTickCount.isBound();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MultiRange n) {
                        return (StyleableProperty<Number>) n.minorTickCountProperty();
                    }
                };

        private static final CssMetaData<MultiRange, Orientation> ORIENTATION =
                new CssMetaData<MultiRange, Orientation>("-fx-orientation", //$NON-NLS-1$
                        new EnumConverter<>(Orientation.class),
                        Orientation.HORIZONTAL) {

                    @Override
                    public Orientation getInitialValue(MultiRange node) {
                        // A vertical Slider should remain vertical 
                        return node.getOrientation();
                    }

                    @Override
                    public boolean isSettable(MultiRange n) {
                        return n.orientation == null || !n.orientation.isBound();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Orientation> getStyleableProperty(MultiRange n) {
                        return (StyleableProperty<Orientation>) n.orientationProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            styleables.add(BLOCK_INCREMENT);
            styleables.add(SHOW_TICK_LABELS);
            styleables.add(SHOW_TICK_MARKS);
            styleables.add(SNAP_TO_TICKS);
            styleables.add(MAJOR_TICK_UNIT);
            styleables.add(MINOR_TICK_COUNT);
            styleables.add(ORIENTATION);
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
    private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");

}
