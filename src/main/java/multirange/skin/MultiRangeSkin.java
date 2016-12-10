package multirange.skin;


import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import multirange.MultiRange;
import multirange.behavior.MultiRangeBehavior;

public class MultiRangeSkin extends BehaviorSkinBase<MultiRange, MultiRangeBehavior> {

  public MultiRangeSkin(final MultiRange multiRange) {
    super(multiRange, new MultiRangeBehavior(multiRange));
  }

}
