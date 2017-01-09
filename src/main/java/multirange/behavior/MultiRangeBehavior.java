package multirange.behavior;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import multirange.MultiRange;

import java.util.List;

/**
 * Created by alberto on 09/01/2017.
 */
public class MultiRangeBehavior extends BehaviorBase<MultiRange> {

    /**
     * Create a new BehaviorBase for the given control. The Control must not
     * be null.
     *
     * @param control     The control. Must not be null.
     * @param keyBindings The key bindings that should be used with this behavior.
     */
    public MultiRangeBehavior(MultiRange control, List<KeyBinding> keyBindings) {
        super(control, keyBindings);
    }

}
