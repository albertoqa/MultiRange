package multirange.skin;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import multirange.MultiRange;
import multirange.behavior.MultiRangeBehavior;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alberto on 09/01/2017.
 */
public class MultiRangeSkin extends BehaviorSkinBase<MultiRange, MultiRangeBehavior> {

    private double thumbWidth;
    private double thumbHeight;

    private StackPane track;
    private double trackStart;
    private double trackLength;
    private double lowThumbPos;

    private List<ThumbRange> thumbs;

    // temp fields for mouse drag handling
    private double preDragPos;          // used as a temp value for low and high thumbsRange
    private Point2D preDragThumbPoint;  // in skin coordinates

    /**
     * Constructor for all BehaviorSkinBase instances.
     *
     * @param control  The control for which this Skin should attach to.
     * @param behavior The behavior for which this Skin should defer to.
     */
    public MultiRangeSkin(final MultiRange control, final MultiRangeBehavior behavior) {
        super(control, behavior);

        initTrack();
        initInitialThumbs();

        registerChangeListener(control.rangesProperty(), "VALUE"); //$NON-NLS-1$
    }

    private void initInitialThumbs() {
        thumbs = new ArrayList<>();
        ThumbRange initialThumbs = new ThumbRange();
        initThumbs(initialThumbs);
    }

    private void initThumbs(ThumbRange t) {
        thumbs.add(t);

        getChildren().addAll(t.low);

        ThumbPane low = t.low;

        low.setOnMousePressed(me -> {
            low.setFocus(true);
            preDragThumbPoint = low.localToParent(me.getX(), me.getY());
            preDragPos = (getSkinnable().getLowValue() - getSkinnable().getMin()) / (getMaxMinusMinNoZero());
        });

        low.setOnMouseDragged(me -> {
            Point2D cur = low.localToParent(me.getX(), me.getY());
            double dragPos = cur.getX() - preDragThumbPoint.getX();
            getBehavior().thumbDragged(me, preDragPos + dragPos / trackLength, 1);
        });
    }

    private void positionLowThumb() {
        MultiRange s = getSkinnable();
        boolean horizontal = isHorizontal();
        double lx = trackStart + (((trackLength * ((s.getLowValue() - s.getMin()) /
                (getMaxMinusMinNoZero()))) - thumbWidth / 2));
        double ly = lowThumbPos;

        ThumbPane low = thumbs.get(0).low;

        low.setLayoutX(lx);
        low.setLayoutY(ly);
    }

    private void initTrack() {
        track = new StackPane();
        track.getStyleClass().setAll("track");
//        track.setBackground(new Background(new BackgroundFill(Paint.valueOf("#000000"), null, null)));

        getChildren().clear();
        getChildren().add(track);

        track.setOnMousePressed(me -> {
            getBehavior().trackPress(me, (me.getX() / trackLength));
            initThumbs(new ThumbRange());
        });
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

        ThumbPane low = thumbs.get(0).low;

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

        // layout track
        track.resizeRelocate(trackStart - trackRadius, trackTop, trackLength + trackRadius + trackRadius, trackHeight);

    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("VALUE".equals(p)) { //$NON-NLS-1$
            positionLowThumb();
        }
        super.handleControlPropertyChanged(p);
    }

    private double minTrackLength() {
        return 2 * thumbs.get(0).low.prefWidth(-1);
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return (leftInset + minTrackLength() + thumbs.get(0).low.minWidth(-1) + rightInset);
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return (topInset + thumbs.get(0).low.prefHeight(-1) + bottomInset);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {

        return 140;

    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().getInsets().getTop() + Math.max(thumbs.get(0).low.prefHeight(-1), track.prefHeight(-1)) +
                (0) + bottomInset;

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

}
