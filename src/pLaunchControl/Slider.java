package pLaunchControl;

import java.lang.reflect.Field;


/**
 * A logical representation of the physical slider on the MIDI controller.
 * The most trivial usage of a Slider is to adjust the value of a variable in
 * your code, using {@link #variable(String) variable} method.
 *
 * A Slider object can be assigned a default value, as well as a range
 *
 */
public class Slider {

    private float defaultValue = 0;
    private float value;
    private int slider_position;
    private float min_value = 0;
    private float max_value = 127;

    private static final float MAX_POSITION = 127f;

    public SLIDERS controllerSlider;
    private Object parent_object;

    private boolean hasDefault = false;

    protected Slider(int sliderCode, float minValue, float maxValue) {
        controllerSlider = SLIDERS.values()[sliderCode];
        range(minValue, maxValue);
    }

    protected Slider(int sliderCode, Object parent) {
        controllerSlider = SLIDERS.values()[sliderCode];
        parent_object = parent;
    }


    /**
     * Sets the default value of the Slider object. You can read the current value of the Slider with {@link #value() value()}.
     * @param defaultValue The default value to assign Slider object. Remember that calling defaultValue() does not change the
     *                     position of the physical slider, but it's useful to set a initial value to the attached variable.
     * @return The Slider itself.
     */
    public Slider defaultValue(float defaultValue) {
        this.defaultValue = defaultValue;

        //approximate the position
        this.slider_position = Math.round(MAX_POSITION * (defaultValue - min_value) / (max_value - min_value));

        this.hasDefault = true;
        if (variable != null)
            setFloatField(parent_object, variable, defaultValue);
        return this;
    }

    /**
     * Sets the value to be returned when the slider is at its minimum position (0 by default).
     * {@see #range(float,float) range(float,float)}
     * @param minValue The value of the Slider object when the slider button is at its leftmost position (default 0).
     * @return The Slider itself.
     */
    public Slider minValue(float minValue) {
        this.min_value = minValue;
        return this;
    }

    /***
     * Sets the value to be returned when the slider is at its maximum position (127 by default).
     * {@see #range(float,float) range(float,float)}
     * @param maxValue The value of the Slider object when the slider button is at its rightmost position (default 128).
     * @return The Slider itself.
     */
    public Slider maxValue(float maxValue) {
        this.max_value = maxValue;
        return this;
    }

    /**
     * Sets the minimum and maximum values to return when sliders is at its minimum and maximum
     * position, respectively. You can use range() instead of calling {@link #minValue(float)}
     * and {@link #maxValue(float)}
     *
     *
     * @param minValue The value of the Slider object when the slider button is at its leftmost position (default 0).
     * @param maxValue The value of the Slider object when the slider button is at its rightmost position (default 127).
     * @return The Slider itself.
     */
    public Slider range(float minValue, float maxValue) {
        this.min_value = minValue;
        this.max_value = maxValue;
        return this;
    }


    /***
     * Sets the stored value of the slider position. If slider has a default value set
     * with `defaultValue(float value)`, this method will only update the stored value
     * after the real slider has "passed over" the default value.
     *
     * @param value
     */
    private void value(float value) {
        this.value = value;
        setFloatField(parent_object, variable, this.value);
    }


    /**
     * @return The current value of the slider object, according to its defined range.
     * Note that value() might not be equivalent to the slider position at all times,
     * because it is not possible to either set the physical position of the Slider,
     * or to read its current position - you need to manually update the slider position
     * for value to be updated in the logical Slider object.
     */
    public float value() {
        if (hasDefault)
            return this.defaultValue;
        return value;

    }

    /**
     * @return The current value of the slider object, normalized between 0 and 1.
     * Note that value() might not be equivalent to the slider position at all times,
     * because it is not possible to either set the physical position of the Slider,
     * or to read its current position - you need to manually update the slider position
     * for value to be updated in the logical Slider object.
     */
    public float valueNormal() {
        float input = 0;
        if (hasDefault)
            input = this.defaultValue;
        else
            input = value;
        //if (input > 0) System.out.println("slider: " + slider_position + ", valueNormal: " + input);
        // else System.out.println("slider: " + slider_position + " no value");
        return (input - min_value) / (max_value - min_value);

    }

    private Field getField(Object parent, String fieldName) {
        if (fieldName == null)
            return null;
        try {
            return parent.getClass().getDeclaredField(fieldName);
        } catch (Exception e) {
            throw new IllegalArgumentException("LaunchControl Error: variable not found in the sketch: " + fieldName + ".");
        }

    }


    private void setFloatField(Object parent, String fieldName, float value) {

        Field field = getField(parent,fieldName);
        if (field == null)
            return;

        field.setAccessible(true);
        try {
            field.setFloat(parent, value);
        }
        catch (IllegalAccessException e) {
            System.err.println(String.format("LaunchControl Error: It was not possible to set the value of the variable %s. Error message: %s", fieldName, e.getMessage()));
        }

    }

    /**
     * Attaches the slider value to a variable. The variable name must match a float variable
     * in your sketch. Currently
     * @param variable
     * @return
     */
    public Slider variable(String variable) {
        this.variable = variable;
        setFloatField(parent_object, variable, value());
        return this;
    }

    public String getVariable() {
        return this.variable;
    }


    public Slider plugTo(Object parent_object) {
        this.parent_object = parent_object;
        return this;
    }

    /***
     * The name of the variable to set when slider changes
     */
    protected String variable;


    public Slider position(int position) {
        if (hasDefault && this.slider_position != position)
            return this;

        this.hasDefault = false;
        this.slider_position = position;
        this.value(min_value + slider_position / MAX_POSITION * (max_value - min_value));
        return this;
    }

    public int position() {
        return this.slider_position;
    }
}
