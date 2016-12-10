/**
 *
 *
 *
 *
 *
 *
 *
 *
**/


// package ....

import javafx.scene.control.Control;

public class MultiRangeSelector extends Control {

  /**
   * Creates a default MultiRangeSelector instance.
   * @return [description]
   */
  public MultiRangeSelector() {
      this(0, 1.0);
  }

  /**
   * Constructs a MultiRangeSelector control with the specified selector min and max value values.
   * @param  double min           Selector minimum value
   * @param  double max           Selector maximum value
   * @return        [description]
   */
  public MultiRangeSelector(double min, double max) {
      setMax(max);
      setMin(min);
      adjustValues();
      initialize();
  }

  /**
   * Initialize the style class
   */
  private void initialize() {
    getStyleClass().setAll(DEFAULT_STYLE_CLASS);
  }





}
