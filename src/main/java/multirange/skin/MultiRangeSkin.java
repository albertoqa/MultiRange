package multirange.skin;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.geometry.Point2D;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import multirange.MultiRange;
import multirange.behavior.MultiRangeBehavior;

/**
 * Created by alberto on 09/01/2017.
 */
public class MultiRangeSkin extends BehaviorSkinBase<MultiRange, MultiRangeBehavior> {

    private StackPane track;
    private double trackStart;
    private double trackLength;
    private ThumbRange thumbs;

    // temp fields for mouse drag handling
    private double preDragPos;          // used as a temp value for low and high thumbs
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
        initThumbs();
    }

    private void initThumbs() {
        thumbs = new ThumbRange();
        getChildren().addAll(thumbs.low);

        thumbs.low.setOnMousePressed(me -> {
            thumbs.high.setFocus(false);
            thumbs.low.setFocus(true);
            preDragThumbPoint = thumbs.low.localToParent(me.getX(), me.getY());
        });

        thumbs.low.setOnMouseDragged(me -> {
            Point2D cur = thumbs.low.localToParent(me.getX(), me.getY());
            double dragPos = cur.getX() - preDragThumbPoint.getX();
        });
    }

    private void positionThumbs() {
        thumbs.low.setLayoutX(0);
        thumbs.low.setLayoutY(0);
        thumbs.high.setLayoutX(20);
        thumbs.high.setLayoutY(20);
    }

    private void initTrack() {
        track = new StackPane();
        track.getStyleClass().setAll("track");
        track.setBackground(new Background(new BackgroundFill(Paint.valueOf("#000000"), null, null)));

        getChildren().clear();
        getChildren().add(track);

        track.setOnMousePressed(me -> getBehavior().trackPress(me, (me.getX() / trackLength)));
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

    @Override
    protected void layoutChildren(final double x, final double y,
                                  final double w, final double h) {

        thumbs.low.resize(10, 10);
        thumbs.low.setBackground(new Background(new BackgroundFill(Paint.valueOf("#999888"), null, null)));

        // we are assuming the is common radius's for all corners on the track
        double trackRadius = track.getBackground() == null ? 0 : track.getBackground().getFills().size() > 0 ?
                track.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() : 0;

        double tickLineHeight = 0;
        double trackHeight = 50;
        double totalHeightNeeded = trackHeight + tickLineHeight;
        double startY = y + ((h - totalHeightNeeded) / 2); // center slider in available height vertically
        double trackTop = (int) (startY + ((trackHeight - trackHeight) / 2));

        trackLength = w;
        trackStart = x;

        // layout track
        track.resizeRelocate(trackStart - trackRadius, trackTop, trackLength + trackRadius + trackRadius, trackHeight);
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return (leftInset + 50 + rightInset);
        } else {
            return (leftInset + 50 + rightInset);
        }
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return (topInset + 50 + bottomInset);
        } else {
            return (topInset + 50 + bottomInset);
        }
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return 140;
        } else {
            //return (padding.getLeft()) + Math.max(thumb.prefWidth(-1), track.prefWidth(-1)) + padding.getRight();
            return leftInset + 50 + rightInset;
        }
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return getSkinnable().getInsets().getTop() + 50 + bottomInset;
        } else {
            return 140;
        }
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return Double.MAX_VALUE;
        } else {
            return getSkinnable().prefWidth(-1);
        }
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (isHorizontal()) {
            return getSkinnable().prefHeight(width);
        } else {
            return Double.MAX_VALUE;
        }
    }

    private boolean isHorizontal() {
        return true;
    }

}
