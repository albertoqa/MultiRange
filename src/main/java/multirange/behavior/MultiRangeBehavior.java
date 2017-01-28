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
import multirange.MultiRange;
import multirange.Range;
import multirange.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alberto on 09/01/2017.
 */
public class MultiRangeBehavior extends BehaviorBase<MultiRange> {

    private double minSpace;    // min space needed to create a new range
    private double padding;     // padding to apply to fit a thumb
    private double separation;  // distance between the low and high thumbs of a range

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
        setSpaces(0.5);
    }

    /**
     * Invoked by the Slider {@link Skin} implementation whenever a mouse press
     * occurs on the "track" of the slider.
     *
     * @param position The mouse position on track with 0.0 being beginning of
     *                 track and 1.0 being the end
     */
    public boolean trackPress(double position) {
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

        /*
         * Suppose a slider as follows:
         * |--------L-------------H------------|
         * Where L is the position of the lowest value and H is the high value of the range.
         * c will be the position of the click.
         */

        double spaceToRight = multiRange.getSpaceToRightRange(newPosition);
        double spaceToLeft = multiRange.getSpaceToLeftRange(newPosition);

        //System.out.println("MinSpace: " + minSpace + "   Right: " + spaceToRight + "   Left: " + spaceToLeft);

        /*
         * |--c-----L-------------H------------|
         * If there is enough space between the clicked position and a +minSpace position, just create that range.
         * |--L----H--L-------------H-----------|
         */
        if (spaceToRight > minSpace) {
            multiRange.createNewRange(newPosition + padding, newPosition + separation - padding);
        }

        /*
         * |------c-L-------------H------------|
         * If there is enough space between the clicked position and a -minSpace position, just create that range.
         * |--L----H-L-------------H-----------|
         */
        else if (spaceToLeft > minSpace) {
            multiRange.createNewRange(newPosition - separation + padding, newPosition - padding);
        }

        /*
         * |--------L-------------H------------|
         * If there is not enough space to place a new range with a difference of minSpace, then place a new one
         * with a difference of only SPACE_MIN.
         */
        else if (spaceToRight + spaceToLeft > minSpace) {
            multiRange.createNewRange(newPosition - spaceToLeft + padding, newPosition + spaceToRight - padding);
        }

        /*
         * Otherwise the range cannot be created
         */
        else {
            return false;
        }

        return true;
    }

    /**
     * Handle the situation when the user press a rangeBar. In this case we will try to split the current range
     * and create two smaller ranges with the min position of the first one being the same min position of the
     * original range, the high position of the first one just a little bit less than the clicked position, the
     * min position of the second one will be just a little more than the clicked position and the max position
     * of the second one the high value of the original range.
     *
     * @param position clicked position
     * @param id       id of the pressed range bar
     * @return true if range can be created
     */
    public boolean rangeBarPressed(double position, int id) {
        final MultiRange multiRange = getControl();

        // If not already focused, request focus
        if (!multiRange.isFocused()) {
            multiRange.requestFocus();
        }

        Range range = multiRange.getRange(id);
        if (range == null) return false;

        double newPosition;
        if (multiRange.getOrientation().equals(Orientation.HORIZONTAL)) {
            newPosition = position * (multiRange.getMax() - multiRange.getMin()) + multiRange.getMin();
        } else {
            newPosition = (1 - position) * (multiRange.getMax() - multiRange.getMin()) + multiRange.getMin();
        }

        newPosition = newPosition + (range.getLow() - multiRange.getMin());

        /*
         * |--------L------c------H------------|
         * If the click position is in between a range, the H value of the range which has been clicked over
         * will be changed to SPACE_HIGH less than the clicked position. A new range will be created with values
         * [clicked position + SPACE_HIGH, H].
         * |--------L-----H-L------H------------|
         */
        double currentHigh = range.getHigh();
        if (range.getAmplitude() > minSpace * 2) {
            range.setHigh(newPosition - padding);
            multiRange.updateRange(range);
            multiRange.createNewRange(newPosition + padding, currentHigh);
            return true;
        } else {
            // no range will be created
            return false;
        }
    }

    /**
     * @param position The mouse position on track with 0.0 being beginning of
     *                 track and 1.0 being the end
     */
    public void lowThumbDragged(double position) {
        getControl().setLowRangeValue(getNewPosition(position));
    }

    /**
     * @param position The mouse position on track with 0.0 being beginning of
     *                 track and 1.0 being the end
     */
    public void highThumbDragged(double position) {
        getControl().setHighRangeValue(getNewPosition(position));
    }

    /**
     * Calculate the new position of the thumb given the clicked/dragged position
     *
     * @param position clicked position
     * @return new position
     */
    private double getNewPosition(double position) {
        final MultiRange multiRange = getControl();
        return Utils.clamp(multiRange.getMin(), (position * (multiRange.getMax() - multiRange.getMin())) + multiRange.getMin(), multiRange.getMax());
    }

    /**
     * Handle secondary button press over a range bar. In this case the clicked range will be deleted.
     */
    public boolean rangeBarPressedSecondary() {
        return getControl().removeSelectedRange();
    }

    // TODO
    private void setSpaces(double thumbWidth) {
        double total = getControl().getMax() - getControl().getMin();
        minSpace = thumbWidth + (total / 20);
        separation = total / 20;
        padding = thumbWidth;
    }

}
