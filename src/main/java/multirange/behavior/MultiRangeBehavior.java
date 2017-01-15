package multirange.behavior;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import multirange.MultiRange;

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
    }

    public void lowThumbDragged(MouseEvent e, double position, int id) {
        final MultiRange multiRange = getControl();
        double newValue = clamp(multiRange.getMin(), (position * (multiRange.getMax() - multiRange.getMin())) + multiRange.getMin(), multiRange.getMax());
        System.out.println("Id: " + id + "     New value: " + newValue + "     Low");
        multiRange.setLowRangeValue(id, newValue);
    }

    public void highThumbDragged(MouseEvent e, double position, int id) {
        final MultiRange multiRange = getControl();
        double newValue = clamp(multiRange.getMin(), (position * (multiRange.getMax() - multiRange.getMin())) + multiRange.getMin(), multiRange.getMax());
        System.out.println("Id: " + id + "     New value: " + newValue + "     High");
        multiRange.setHighRangeValue(id, newValue);
    }

    /**
     * Simple utility function which clamps the given value to be strictly
     * between the min and max values.
     */
    public static double clamp(double min, double value, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

}
