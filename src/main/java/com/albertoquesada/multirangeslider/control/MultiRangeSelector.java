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


  /**
  * Ensures that min is always < max, that value is always
  * somewhere between the two, and that if snapToTicks is set then the
  * value will always be set to align with a tick mark.
  */
  private void adjustValues() {
    if ((getValue() < getMin() || getValue() > getMax()) /* &&  !isReadOnly(value)*/)
    setValue(Utils.clamp(getMin(), getValue(), getMax()));
  }
  
  /**
  * Adjusts {@link #valueProperty() value} to match <code>newValue</code>. The
  * <code>value</code>is the actual amount between the
  * {@link #minProperty() min} and {@link #maxProperty() max}. This function
  * also takes into account {@link #snapToTicksProperty() snapToTicks}, which
  * is the main difference between adjustValue and setValue. It also ensures
  * that the value is some valid number between min and max.
  *
  * @expert This function is intended to be used by experts, primarily
  *         by those implementing new Skins or Behaviors. It is not common
  *         for developers or designers to access this function directly.
  */
  public void adjustValue(double newValue) {
    // figure out the "value" associated with the specified position
    final double _min = getMin();
    final double _max = getMax();
    if (_max <= _min) return;
    newValue = newValue < _min ? _min : newValue;
    newValue = newValue > _max ? _max : newValue;

    setValue(snapValueToTicks(newValue));
  }

  /****************************************
  *                                      *
  * Properties copied from Slider        *
  *                                      *
  ***************************************/

  /**
  * The maximum value represented by this Slider. This must be a
  * value greater than {@link #minProperty() min}.
  */
  private DoubleProperty max;
  public final void setMax(double value) {
    maxProperty().set(value);
  }

  public final double getMax() {
    return max == null ? 100 : max.get();
  }

  public final DoubleProperty maxProperty() {
    if (max == null) {
      max = new DoublePropertyBase(100) {
        @Override protected void invalidated() {
          if (get() < getMin()) {
            setMin(get());
          }
          adjustValues();
        }

        @Override
        public Object getBean() {
          return Slider.this;
        }

        @Override
        public String getName() {
          return "max";
        }
      };
    }
    return max;
  }

  /**
  * The minimum value represented by this Slider. This must be a
  * value less than {@link #maxProperty() max}.
  */
  private DoubleProperty min;
  public final void setMin(double value) {
    minProperty().set(value);
  }

  public final double getMin() {
    return min == null ? 0 : min.get();
  }

  public final DoubleProperty minProperty() {
    if (min == null) {
      min = new DoublePropertyBase(0) {
        @Override protected void invalidated() {
          if (get() > getMax()) {
            setMax(get());
          }
          adjustValues();
        }

        @Override
        public Object getBean() {
          return Slider.this;
        }

        @Override
        public String getName() {
          return "min";
        }
      };
    }
    return min;
  }




}
