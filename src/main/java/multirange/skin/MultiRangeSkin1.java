package multirange.skin;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import multirange.MultiRange1;
import multirange.behavior.MultiRangeBehavior1;

public class MultiRangeSkin1 extends BehaviorSkinBase<MultiRange1, MultiRangeBehavior1> {

    private NumberAxis tickLine = null;
    private double trackToTickGap = 2;

    private boolean showTickMarks;
    private double thumbWidth;
    private double thumbHeight;

    private double trackStart;
    private double trackLength;
    private double thumbTop;
    private double thumbLeft;
    private double preDragThumbPos;
    private Point2D dragStart; // in skin coordinates

    private StackPane thumb;
    private StackPane track;
    private boolean trackClicked = false;

    public MultiRangeSkin1(final MultiRange1 multiRange) {
        super(multiRange, new MultiRangeBehavior1(multiRange));

        initialize();
        multiRange.requestLayout();
        registerChangeListener(multiRange.minProperty(), "MIN");
        registerChangeListener(multiRange.maxProperty(), "MAX");

    }

    private void initialize() {
        thumb = new StackPane() {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                switch (attribute) {

                    default:
                        return super.queryAccessibleAttribute(attribute, parameters);
                }
            }
        };
        thumb.getStyleClass().setAll("thumb");
        thumb.setAccessibleRole(AccessibleRole.THUMB);
        track = new StackPane();
        track.getStyleClass().setAll("track");

        getChildren().clear();
        getChildren().addAll(track, thumb);
        track.setOnMousePressed(me -> {
            if (!thumb.isPressed()) {
                trackClicked = true;
                getBehavior().trackPress(me, (me.getX() / trackLength));
                trackClicked = false;
            }
        });

//        track.setOnMouseDragged(me -> {
//            if (!thumb.isPressed()) {
//                if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
//                    getBehavior().trackPress(me, (me.getX() / trackLength));
//                } else {
//                    getBehavior().trackPress(me, (me.getY() / trackLength));
//                }
//            }
//        });
//
//        thumb.setOnMousePressed(me -> {
//            getBehavior().thumbPressed(me, 0.0f);
//            dragStart = thumb.localToParent(me.getX(), me.getY());
//            preDragThumbPos = (getSkinnable().getValue() - getSkinnable().getMin()) /
//                    (getSkinnable().getMax() - getSkinnable().getMin());
//        });
//
//        thumb.setOnMouseReleased(me -> {
//            getBehavior().thumbReleased(me);
//        });
//
//        thumb.setOnMouseDragged(me -> {
//            Point2D cur = thumb.localToParent(me.getX(), me.getY());
//            double dragPos = (getSkinnable().getOrientation() == Orientation.HORIZONTAL) ?
//                    cur.getX() - dragStart.getX() : -(cur.getY() - dragStart.getY());
//            getBehavior().thumbDragged(me, preDragThumbPos + dragPos / trackLength);
//        });
    }


    private void setShowTickMarks(boolean ticksVisible, boolean labelsVisible) {
        showTickMarks = (ticksVisible || labelsVisible);
        MultiRange1 multiRange = getSkinnable();
        if (showTickMarks) {
            if (tickLine == null) {
                tickLine = new NumberAxis();
                tickLine.setAutoRanging(false);
                tickLine.setSide(Side.RIGHT);
                tickLine.setUpperBound(multiRange.getMax());
                tickLine.setLowerBound(multiRange.getMin());
                tickLine.setTickMarkVisible(ticksVisible);
                tickLine.setTickLabelsVisible(labelsVisible);
                tickLine.setMinorTickVisible(ticksVisible);
                // add 1 to the MultiRange1 minor tick count since the axis draws one
                // less minor ticks than the number given.

                getChildren().clear();
                getChildren().addAll(tickLine, track, thumb);
            } else {
                tickLine.setTickLabelsVisible(labelsVisible);
                tickLine.setTickMarkVisible(ticksVisible);
                tickLine.setMinorTickVisible(ticksVisible);
            }
        } else {
            getChildren().clear();
            getChildren().addAll(track, thumb);
//            tickLine = null;
        }

        getSkinnable().requestLayout();
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        MultiRange1 MultiRange = getSkinnable();
        if ("ORIENTATION".equals(p)) {
            if (showTickMarks && tickLine != null) {
            }
            getSkinnable().requestLayout();
        } else if ("VALUE".equals(p)) {
            // only animate thumb if the track was clicked - not if the thumb is dragged
            positionThumb(trackClicked);
        } else if ("MIN".equals(p)) {
            if (showTickMarks && tickLine != null) {
                tickLine.setLowerBound(MultiRange.getMin());
            }
            getSkinnable().requestLayout();
        } else if ("MAX".equals(p)) {
            if (showTickMarks && tickLine != null) {
                tickLine.setUpperBound(MultiRange.getMax());
            }
            getSkinnable().requestLayout();
        } else if ("SHOW_TICK_MARKS".equals(p) || "SHOW_TICK_LABELS".equals(p)) {
        } else if ("MAJOR_TICK_UNIT".equals(p)) {
            if (tickLine != null) {
                getSkinnable().requestLayout();
            }
        } else if ("MINOR_TICK_COUNT".equals(p)) {
            if (tickLine != null) {
                getSkinnable().requestLayout();
            }
        } else if ("TICK_LABEL_FORMATTER".equals(p)) {
            if (tickLine != null) {

            }
        } else if ("SNAP_TO_TICKS".equals(p)) {
        }
    }

    /**
     * Called when ever either min, max or value changes, so thumb's layoutX, Y is recomputed.
     */
    void positionThumb(final boolean animate) {
        MultiRange1 s = getSkinnable();
        boolean horizontal = true;
        final double endX = (horizontal) ? trackStart + (((trackLength * ((s.getMax() - s.getMin()) /
                (s.getMax() - s.getMin()))) - thumbWidth / 2)) : thumbLeft;
        final double endY = (horizontal) ? thumbTop :
                snappedTopInset() + trackLength - (trackLength * ((s.getMax() - s.getMin()) /
                        (s.getMax() - s.getMin()))); //  - thumbHeight/2

        if (animate) {
            // lets animate the thumb transition
            final double startX = thumb.getLayoutX();
            final double startY = thumb.getLayoutY();
            Transition transition = new Transition() {
                {
                    setCycleDuration(Duration.millis(200));
                }

                @Override
                protected void interpolate(double frac) {
                    if (!Double.isNaN(startX)) {
                        thumb.setLayoutX(startX + frac * (endX - startX));
                    }
                    if (!Double.isNaN(startY)) {
                        thumb.setLayoutY(startY + frac * (endY - startY));
                    }
                }
            };
            transition.play();
        } else {
            thumb.setLayoutX(endX);
            thumb.setLayoutY(endY);
        }
    }

    @Override
    protected void layoutChildren(final double x, final double y,
                                  final double w, final double h) {
        // calculate the available space
        // resize thumb to preferred size
        thumbWidth = snapSize(thumb.prefWidth(-1));
        thumbHeight = snapSize(thumb.prefHeight(-1));
        thumb.resize(thumbWidth, thumbHeight);
        // we are assuming the is common radius's for all corners on the track
        double trackRadius = track.getBackground() == null ? 0 : track.getBackground().getFills().size() > 0 ?
                track.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() : 0;

            double tickLineHeight = (showTickMarks) ? tickLine.prefHeight(-1) : 0;
            double trackHeight = snapSize(track.prefHeight(-1));
            double trackAreaHeight = Math.max(trackHeight, thumbHeight);
            double totalHeightNeeded = trackAreaHeight + ((showTickMarks) ? trackToTickGap + tickLineHeight : 0);
            double startY = y + ((h - totalHeightNeeded) / 2); // center MultiRange1 in available height vertically
            trackLength = snapSize(w - thumbWidth);
            trackStart = snapPosition(x + (thumbWidth / 2));
            double trackTop = (int) (startY + ((trackAreaHeight - trackHeight) / 2));
            thumbTop = (int) (startY + ((trackAreaHeight - thumbHeight) / 2));

            positionThumb(false);
            // layout track
            track.resizeRelocate((int) (trackStart - trackRadius),
                    trackTop,
                    (int) (trackLength + trackRadius + trackRadius),
                    trackHeight);
            // layout tick line
            if (showTickMarks) {
                tickLine.setLayoutX(trackStart);
                tickLine.setLayoutY(trackTop + trackHeight + trackToTickGap);
                tickLine.resize(trackLength, tickLineHeight);
                tickLine.requestAxisLayout();
            } else {
                if (tickLine != null) {
                    tickLine.resize(0, 0);
                    tickLine.requestAxisLayout();
                }
                tickLine = null;
            }

    }

    double minTrackLength() {
        return 2 * thumb.prefWidth(-1);
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        final MultiRange1 s = getSkinnable();
            return (leftInset + minTrackLength() + thumb.minWidth(-1) + rightInset);

    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        final MultiRange1 s = getSkinnable();
            double axisHeight = showTickMarks ? (tickLine.prefHeight(-1) + trackToTickGap) : 0;
            return topInset + thumb.prefHeight(-1) + axisHeight + bottomInset;

    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        final MultiRange1 s = getSkinnable();
            if (showTickMarks) {
                return Math.max(140, tickLine.prefWidth(-1));
            } else {
                return 140;
            }

    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        final MultiRange1 s = getSkinnable();
            return topInset + Math.max(thumb.prefHeight(-1), track.prefHeight(-1)) +
                    ((showTickMarks) ? (trackToTickGap + tickLine.prefHeight(-1)) : 0) + bottomInset;

    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return Double.MAX_VALUE;

    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return getSkinnable().prefHeight(width);

    }

    private void initFirstThumb() {
        thumb = new MultiRangeSkin1.ThumbPane();
        thumb.getStyleClass().setAll("low-thumb"); //$NON-NLS-1$
        thumb.setFocusTraversable(true);
        track = new StackPane();
        track.getStyleClass().setAll("track"); //$NON-NLS-1$

        getChildren().clear();
        getChildren().addAll(track, thumb);
        track.setOnMousePressed( new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                if (!thumb.isPressed()) {

                        getBehavior().trackPress(me, (me.getX() / trackLength));

                }
            }
        });

        track.setOnMouseReleased( new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                //Nothing being done with the second param in sliderBehavior
                //So, passing a dummy value
                //getBehavior().trackRelease(me, 0.0f);
            }
        });

        thumb.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {

            }
        });

        thumb.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                //getBehavior().lowThumbReleased(me);
            }
        });


    }

    private static class ThumbPane extends StackPane {
        public void setFocus(boolean value) {
            setFocused(value);
        }
    }
}
