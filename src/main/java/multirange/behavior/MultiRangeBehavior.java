package multirange.behavior;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.behavior.OrientedKeyBinding;
import com.sun.javafx.util.Utils;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventType;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import multirange.MultiRange;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;

public class MultiRangeBehavior extends BehaviorBase<MultiRange> {

    /**************************************************************************
     *                          Setup KeyBindings                             *
     *                                                                        *
     * We manually specify the focus traversal keys because MultiRange has        *
     * different usage for up/down arrow keys.                                *
     *************************************************************************/

    private static final List<KeyBinding> MULTI_RANGE_BINDINGS = new ArrayList<>();

    static {
        MULTI_RANGE_BINDINGS.add(new KeyBinding(F4, "TraverseDebug").alt().ctrl().shift());

        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(LEFT, "DecrementValue"));
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(KP_LEFT, "DecrementValue"));
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(UP, "IncrementValue").vertical());
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(KP_UP, "IncrementValue").vertical());
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(RIGHT, "IncrementValue"));
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(KP_RIGHT, "IncrementValue"));
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(DOWN, "DecrementValue").vertical());
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(KP_DOWN, "DecrementValue").vertical());

        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(LEFT, "TraverseLeft").vertical());
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(KP_LEFT, "TraverseLeft").vertical());
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(UP, "TraverseUp"));
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(KP_UP, "TraverseUp"));
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(RIGHT, "TraverseRight").vertical());
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(KP_RIGHT, "TraverseRight").vertical());
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(DOWN, "TraverseDown"));
        MULTI_RANGE_BINDINGS.add(new MultiRangeKeyBinding(KP_DOWN, "TraverseDown"));

        MULTI_RANGE_BINDINGS.add(new KeyBinding(HOME, KEY_RELEASED, "Home"));
        MULTI_RANGE_BINDINGS.add(new KeyBinding(END, KEY_RELEASED, "End"));
    }

    public MultiRangeBehavior(MultiRange multiRange) {
        super(multiRange, MULTI_RANGE_BINDINGS);
    }

    @Override
    protected void callAction(String name) {
        /*if ("Home".equals(name)) home();
        else if ("End".equals(name)) end();
        else if ("IncrementValue".equals(name)) incrementValue();
        else if ("DecrementValue".equals(name)) decrementValue();
        else super.callAction(name);*/
    }


    /**
     * Invoked by the MultiRange {@link Skin} implementation whenever a mouse press
     * occurs on the "track" of the MultiRange. This will cause the thumb to be
     * moved by some amount.
     *
     * @param position The mouse position on track with 0.0 being beginning of
     *                 track and 1.0 being the end
     */
    public void trackPress(MouseEvent e, double position) {
        // determine the percentage of the way between min and max
        // represented by this mouse event
        final MultiRange multiRange = getControl();

        // If not already focused, request focus
        if (!multiRange.isFocused()) {
            multiRange.requestFocus();
        }

        double newPosition = position * (multiRange.getMax() - multiRange.getMin()) + multiRange.getMin();

        /*
         * If rangeA has no value, this means the user clicked on the track to set a new
         * side of a range. The range will not be active and saved until the other side is
         * clicked.
         *
         * If rangeA has a value, this means the user clicked on the track to close the range
         * being it [rangeA, rangeB] or [rangeB, rangeA]. In this case the range is closed and
         * both sides set again to null.
         */
        if (multiRange.getRangeA() == null) {
            // It is not permitted to set a new range in between a previously closed range
            for (Pair<DoubleProperty, DoubleProperty> range : multiRange.getRanges()) {
                if (isPositionBetweenRange(newPosition, range)) {
                    // TODO Show alert
                    return;
                }
            }
            multiRange.setRangeA(newPosition);
        } else {
            Pair<Double, Double> newRange = new Pair<>(multiRange.getRangeA(), newPosition);
            /*
             * It is not permitted to set a new range in between a previously closed range or
             * close the range with another range between the new one. Sample:
             *      Not permitted: OldRange[2, 5], NewRange[3, X]
             *      Not permitted: OldRange[2, 5], NewRange[1, 3]
             *      Permitted: OldRange[2, 5], NewRange[1, 2], [6, 8]...
             */
            for (Pair<DoubleProperty, DoubleProperty> range : multiRange.getRanges()) {
                if (isPositionBetweenRange(newPosition, range)) {
                    // TODO Show alert
                    return;
                } else if (isOverlapingRange(newRange, range)) {
                    // TODO Show alert
                    return;
                }
            }

            /*
             * New range closed. Add it to the list of closed ranges and reset sides to null.
             */
            // TODO
            //multiRange.getRanges().add(newRange);
            //multiRange.setRangeA(null);
            //multiRange.setRangeB(null);
        }
    }

    // TODO
    private boolean isPositionBetweenRange(double position, Pair<DoubleProperty, DoubleProperty> range) {
        return false;
    }

    // TODO
    private boolean isOverlapingRange(Pair<Double, Double> newRange, Pair<DoubleProperty, DoubleProperty> range) {
        return false;
    }


    public static class MultiRangeKeyBinding extends OrientedKeyBinding {
        public MultiRangeKeyBinding(KeyCode code, String action) {
            super(code, action);
        }

        public MultiRangeKeyBinding(KeyCode code, EventType<KeyEvent> type, String action) {
            super(code, type, action);
        }

        public
        @Override
        boolean getVertical(Control control) {
            return false;
        }
    }

    /**
     * @param position The mouse position on track with 0.0 being beginning of
     *       track and 1.0 being the end
     */
    public void thumbPressed(MouseEvent e, double position) {
        // If not already focused, request focus
        final MultiRange MultiRange = getControl();
        if (!MultiRange.isFocused())  MultiRange.requestFocus();
        MultiRange.setValueChanging(true);
    }

    /**
     * @param position The mouse position on track with 0.0 being beginning of
     *        track and 1.0 being the end
     */
    public void thumbDragged(MouseEvent e, double position) {
        final MultiRange MultiRange = getControl();
        MultiRange.setValue(Utils.clamp(MultiRange.getMin(), (position * (MultiRange.getMax() - MultiRange.getMin())) + MultiRange.getMin(), MultiRange.getMax()));
    }

    /**
     * When thumb is released valueChanging should be set to false.
     */
    public void thumbReleased(MouseEvent e) {
        final MultiRange MultiRange = getControl();
        MultiRange.setValueChanging(false);
        // RT-15207 When snapToTicks is true, MultiRange value calculated in drag
        // is then snapped to the nearest tick on mouse release.
        MultiRange.adjustValue(MultiRange.getValue());
    }

}
