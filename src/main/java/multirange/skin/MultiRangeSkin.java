package multirange.skin;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.StackPane;
import multirange.MultiRange;
import multirange.behavior.MultiRangeBehavior;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alberto on 09/01/2017.
 */
public class MultiRangeSkin extends BehaviorSkinBase<MultiRange, MultiRangeBehavior> {

    /**
     * Track if slider is vertical/horizontal and cause re layout
     */
    private NumberAxis tickLine = null;
    private double trackToTickGap = 2;

    private boolean showTickMarks;
    private double thumbWidth;
    private double thumbHeight;

    private Orientation orientation;

    private StackPane track;
    private double trackStart;
    private double trackLength;
    private double lowThumbPos;

    private List<ThumbRange> thumbs;

    // temp fields for mouse drag handling
    private double preDragPos;          // used as a temp value for low and high thumbsRange
    private Point2D preDragThumbPoint;  // in skin coordinates

    private IntegerProperty currentId = new SimpleIntegerProperty(0);
    private int id = 0;

    /**
     * Constructor for all BehaviorSkinBase instances.
     *
     * @param control  The control for which this Skin should attach to.
     * @param behavior The behavior for which this Skin should defer to.
     */
    public MultiRangeSkin(final MultiRange control, final MultiRangeBehavior behavior) {
        super(control, behavior);

        orientation = getSkinnable().getOrientation();

        initTrack();
        initInitialThumbs();

        registerChangeListener(control.valueChangingProperty(), "RANGES");
        registerChangeListener(control.orientationProperty(), "ORIENTATION");
        registerChangeListener(control.showTickMarksProperty(), "SHOW_TICK_MARKS");
        registerChangeListener(control.showTickLabelsProperty(), "SHOW_TICK_LABELS");
        registerChangeListener(control.majorTickUnitProperty(), "MAJOR_TICK_UNIT");
        registerChangeListener(control.minorTickCountProperty(), "MINOR_TICK_COUNT");
        registerChangeListener(control.minProperty(), "MIN");
        registerChangeListener(control.maxProperty(), "MAX");

        getSkinnable().currentRangeIdProperty().bind(currentId);
    }

    private void initInitialThumbs() {
        thumbs = new ArrayList<>();
        ThumbRange initialThumbs = new ThumbRange();
        initThumbs(initialThumbs, getNextId());

        setShowTickMarks(getSkinnable().isShowTickMarks(), getSkinnable().isShowTickLabels());
    }

    private void initThumbs(ThumbRange t, int index) {
        thumbs.add(t);

        getChildren().addAll(t.low, t.high);

        t.low.setOnMousePressed(me -> {
            t.low.setFocus(true);
            currentId.setValue(index);
            preDragThumbPoint = t.low.localToParent(me.getX(), me.getY());
            preDragPos = (getSkinnable().getLowValue() - getSkinnable().getMin()) / (getMaxMinusMinNoZero());
        });

        t.low.setOnMouseDragged(me -> {
            Point2D cur = t.low.localToParent(me.getX(), me.getY());
            double dragPos = cur.getX() - preDragThumbPoint.getX();
            getBehavior().lowThumbDragged(preDragPos + dragPos / trackLength);
        });

        t.high.setOnMousePressed(me -> {
            t.high.setFocus(true);
            currentId.setValue(index);
            preDragThumbPoint = t.high.localToParent(me.getX(), me.getY());
            preDragPos = (getSkinnable().getHighValue() - getSkinnable().getMin()) / (getMaxMinusMinNoZero());
        });

        t.high.setOnMouseDragged(me -> {
            Point2D cur = t.high.localToParent(me.getX(), me.getY());
            double dragPos = cur.getX() - preDragThumbPoint.getX();
            getBehavior().highThumbDragged(me, preDragPos + dragPos / trackLength);
        });
    }

    private ThumbRange getCurrentThumb() {
        return thumbs.get(currentId.get());
    }

    private void positionLowThumb() {
        MultiRange s = getSkinnable();
        double lx = trackStart + (((trackLength * ((s.getLowValue() - s.getMin()) / (getMaxMinusMinNoZero()))) - thumbWidth / 2));
        double ly = lowThumbPos;

        ThumbPane low = getCurrentThumb().low;

        low.setLayoutX(lx);
        low.setLayoutY(ly);
    }

    private void positionHighThumb() {
        MultiRange s = getSkinnable();

        double thumbWidth = getCurrentThumb().low.getWidth();
        double thumbHeight = getCurrentThumb().low.getHeight();
        getCurrentThumb().high.resize(thumbWidth, thumbHeight);

        double trackStart = track.getLayoutX();
        double trackLength = track.getWidth();

        trackLength -= 2;
        double lx = trackStart + (trackLength * ((s.getHighValue() - s.getMin()) / (getMaxMinusMinNoZero())) - thumbWidth / 2D);
        double ly = getCurrentThumb().low.getLayoutY();

        ThumbPane high = getCurrentThumb().high;

        high.setLayoutX(lx);
        high.setLayoutY(ly);
    }


    private void initTrack() {
        track = new StackPane();
        track.getStyleClass().setAll("track");

        getChildren().clear();
        getChildren().add(track);

        track.setOnMousePressed(me -> {
            if (isHorizontal()) {
                getBehavior().trackPress(me, (me.getX() / trackLength));
            } else {
                getBehavior().trackPress(me, (me.getY() / trackLength));
            }

            int index = getNextId();
            currentId.setValue(index);
            initThumbs(new ThumbRange(), index);
            getSkinnable().createNewRange(0.5, 1);
        });
    }

    private int getNextId() {
        return id++;
    }

    private static class ThumbPane extends StackPane {
        public void setFocus(boolean value) {
            setFocused(value);
        }
    }

    private static class ThumbRange {
        ThumbPane low;
        ThumbPane high;

        ThumbRange() {
            low = new ThumbPane();
            low.getStyleClass().setAll("low-thumb");
            low.setFocusTraversable(true);

            high = new ThumbPane();
            high.getStyleClass().setAll("low-thumb");
            high.setFocusTraversable(true);
        }
    }

    /**
     * @return the difference between max and min, but if they have the same
     * value, 1 is returned instead of 0 because otherwise the division where it
     * can be used will return Nan.
     */
    private double getMaxMinusMinNoZero() {
        MultiRange s = getSkinnable();
        return s.getMax() - s.getMin() == 0 ? 1 : s.getMax() - s.getMin();
    }

    @Override
    protected void layoutChildren(final double x, final double y,
                                  final double w, final double h) {

        ThumbPane low = getCurrentThumb().low;

        thumbWidth = low.prefWidth(-1);
        thumbHeight = low.prefHeight(-1);
        low.resize(thumbWidth, thumbHeight);
        // we are assuming the is common radius's for all corners on the track
        double trackRadius = track.getBackground() == null ? 0 : track.getBackground().getFills().size() > 0 ?
                track.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() : 0;

        double tickLineHeight = 0;
        double trackHeight = track.prefHeight(-1);
        double trackAreaHeight = Math.max(trackHeight, thumbHeight);
        double totalHeightNeeded = trackAreaHeight;
        double startY = y + ((h - totalHeightNeeded) / 2); // center slider in available height vertically

        trackLength = w - thumbWidth;
        trackStart = x + (thumbWidth / 2);

        double trackTop = (int) (startY + ((trackAreaHeight - trackHeight) / 2));
        lowThumbPos = (int) (startY + ((trackAreaHeight - thumbHeight) / 2));

        positionLowThumb();
        positionHighThumb();

        // layout track
        track.resizeRelocate(trackStart - trackRadius, trackTop, trackLength + trackRadius + trackRadius, trackHeight);
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("RANGES".equals(p)) {
            positionLowThumb();
            positionHighThumb();
            getSkinnable().valueChangingProperty().setValue(false);
        }
        super.handleControlPropertyChanged(p);
    }

    private double minTrackLength() {
        return 2 * getCurrentThumb().low.prefWidth(-1);
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return (leftInset + minTrackLength() + getCurrentThumb().low.minWidth(-1) + rightInset);
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return (topInset + getCurrentThumb().low.prefHeight(-1) + bottomInset);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 140;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().getInsets().getTop() + Math.max(getCurrentThumb().low.prefHeight(-1), track.prefHeight(-1)) + (0) + bottomInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Double.MAX_VALUE;

    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    private boolean isHorizontal() {
        return true;
    }

    /**
     * When ticks or labels are changing of visibility, we compute the new
     * visibility and add the necessary objets. After this method, we must be
     * sure to add the high Thumb and the rangeBar.
     *
     * @param ticksVisible
     * @param labelsVisible
     */
    private void setShowTickMarks(boolean ticksVisible, boolean labelsVisible) {
        showTickMarks = (ticksVisible || labelsVisible);
        MultiRange multiRange = getSkinnable();
        if (showTickMarks) {
            if (tickLine == null) {
                tickLine = new NumberAxis();
                tickLine.tickLabelFormatterProperty().bind(getSkinnable().labelFormatterProperty());
                tickLine.setAnimated(false);
                tickLine.setAutoRanging(false);
                tickLine.setSide(isHorizontal() ? Side.BOTTOM : Side.RIGHT);
                tickLine.setUpperBound(multiRange.getMax());
                tickLine.setLowerBound(multiRange.getMin());
                tickLine.setTickUnit(multiRange.getMajorTickUnit());
                tickLine.setTickMarkVisible(ticksVisible);
                tickLine.setTickLabelsVisible(labelsVisible);
                tickLine.setMinorTickVisible(ticksVisible);
                // add 1 to the slider minor tick count since the axis draws one
                // less minor ticks than the number given.
                tickLine.setMinorTickCount(Math.max(multiRange.getMinorTickCount(), 0) + 1);
                getChildren().clear();
                getChildren().addAll(tickLine, track, lowThumb);
            } else {
                tickLine.setTickLabelsVisible(labelsVisible);
                tickLine.setTickMarkVisible(ticksVisible);
                tickLine.setMinorTickVisible(ticksVisible);
            }
        } else {
            getChildren().clear();
            getChildren().addAll(track, lowThumb);
//            tickLine = null;
        }

        getSkinnable().requestLayout();
    }

}
