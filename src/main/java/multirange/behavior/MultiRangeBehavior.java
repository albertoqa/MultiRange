package multirange.behavior;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import javafx.geometry.Orientation;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import multirange.MultiRange;
import multirange.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alberto on 09/01/2017.
 */
public class MultiRangeBehavior extends BehaviorBase<MultiRange> {

    private static final List<KeyBinding> MULTI_RANGE_BINDINGS = new ArrayList<>();

    public MultiRangeBehavior(MultiRange control) {
        this(control, MULTI_RANGE_BINDINGS);
    }

    /**
     * Create a new BehaviorBase for the given control. The Control must not
     * be null.
     *
     * @param control     The control. Must not be null.
     * @param keyBindings The key bindings that should be used with this behavior.
     */
    private MultiRangeBehavior(MultiRange control, List<KeyBinding> keyBindings) {
        super(control, keyBindings);
    }

    /**
     * Invoked by the Slider {@link Skin} implementation whenever a mouse press
     * occurs on the "track" of the slider.
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

        double newPosition;
        if (multiRange.getOrientation().equals(Orientation.HORIZONTAL)) {
            newPosition = position * (multiRange.getMax() - multiRange.getMin()) + multiRange.getMin();
        } else {
            newPosition = (1 - position) * (multiRange.getMax() - multiRange.getMin()) + multiRange.getMin();
        }

    }

    public void lowThumbPressed() {
        // If not already focused, request focus
        final MultiRange multiRange = getControl();
        if (!multiRange.isFocused()) multiRange.requestFocus();
        multiRange.setValueChanging(true);
    }

    public void lowThumbDragged(double position) {
        getControl().setLowRangeValue(getNewPosition(position));
    }

    /**
     * When lowThumb is released lowValueChanging should be set to false.
     */
//    public void lowThumbReleased(MouseEvent e) {
//        final MultiRange multiRange = getControl();
//        multiRange.setValueChanging(false);
//        // RT-15207 When snapToTicks is true, slider value calculated in drag
//        // is then snapped to the nearest tick on mouse release.
//        if (multiRange.isSnapToTicks()) {
//            multiRange.setLowValue(snapValueToTicks(multiRange.getLowValue()));
//        }
//    }
    public void highThumbDragged(MouseEvent e, double position) {
        getControl().setHighRangeValue(getNewPosition(position));
    }

    private double getNewPosition(double position) {
        final MultiRange multiRange = getControl();
        return Utils.clamp(multiRange.getMin(), (position * (multiRange.getMax() - multiRange.getMin())) + multiRange.getMin(), multiRange.getMax());
    }

}
