/*
 * Copyright (c) 2013, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package multirange.behavior;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import javafx.geometry.Orientation;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import multirange.MultiRange;
import multirange.Range;
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

        /**
         * Suppose a slider as follows:
         * |--------L-------------H------------|
         * Where L is the position of the lowest value and H is the high value of the range.
         * c will be the position of the click.
         */

        /**
         * |--------L------c------H------------|
         * If the click position is in between a range, the H value of the range which has been clicked over
         * will be changed to __ less than the clicked position. A new range will be created with values
         * [clicked position + __, H].
         * |--------L-----H-L------H------------|
         */
        if (multiRange.isInBetweenRange(newPosition)) {
            Range r = multiRange.getRangeForPosition(newPosition);
            double currentHigh = r.getHigh();
            if(r.getAmplitude() > 0.2) {
                if (r.getLow() > newPosition - 0.02) {
                    r.setHigh(newPosition - 0.02);
                    multiRange.updateRange(r);
                    multiRange.createNewRange(newPosition + 0.02, currentHigh);
                } else {
                    r.setHigh(newPosition - 0.01);
                    multiRange.createNewRange(newPosition + 0.01, currentHigh);
                }
            }
            // TODO refresh r thumbs position!
        }

        /**
         * |--c-----L-------------H------------|
         * If there is enough space between the clicked position and a +__ position, just create that range.
         * |--L----H--L-------------H-----------|
         */
        else if (multiRange.getSpaceToRightRange(newPosition) > 5) {

        }

        /**
         * |------c-L-------------H------------|
         * If there is enough space between the clicked position and a -__ position, just create that range.
         * |--L----H-L-------------H-----------|
         */
        else if (multiRange.getSpaceToLeftRange(newPosition) > 5) {

        }

        /**
         * |--------L-------------H------------|
         * If there is not enough space to place a new range with a difference of __, then place a new one
         * with a difference of only __.
         */
        else {

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
