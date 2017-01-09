package multirange;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    }

    /***************************************************************************
     *                                                                         *
     *                              Properties                                 *
     *                                                                         *
     **************************************************************************/

    private ObjectProperty<ObservableList<Range>> ranges;

    public ObservableList<Range> getRanges() {
        return ranges.get();
    }

    public ObjectProperty<ObservableList<Range>> rangesProperty() {
        if (ranges == null) {
            ranges = new SimpleObjectProperty<>(this, "ranges",
                    FXCollections.observableArrayList());
        }
        return ranges;
    }

    public void setRanges(ObservableList<Range> ranges) {
        this.ranges.set(ranges);
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
