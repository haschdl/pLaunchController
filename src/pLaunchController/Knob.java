package pLaunchController;

import java.lang.reflect.Field;


/***
 * A logical representation of the physical knob in the MIDI controller.
 * By rotating a knob, the controller sends a note (values 0-127).
 */
public class Knob {

    private float defaultValue;
    private float value;
    private int knob_position;
    private float min_value = 0;
    private float max_value = 127;

    private static final float MAX_POSITION = 127f;

    public KNOBS controllerKnob;
    private Object parent_object;

    private boolean hasDefault = false;

    protected Knob(int knobCode,float minValue,float maxValue) {
        controllerKnob = KNOBS.values()[knobCode];
        range(minValue,maxValue);
    }
    protected Knob(int knobCode, Object parent) {
        controllerKnob = KNOBS.values()[knobCode];
        parent_object = parent;
    }

    public Knob defaultValue(float defaultValue) {
        this.defaultValue = defaultValue;

        //approximate the position
        this.knob_position =   Math.round(MAX_POSITION * (defaultValue-min_value)/(max_value - min_value));

        this.hasDefault = true;
        setFloatField(parent_object,variable,defaultValue);
        return this;
    }

    /**
     * Sets the value to be returned when the knob is at its minimum position (0 by default).
     * @param minValue
     * @return
     */
    public Knob minValue(float minValue) {
        this.min_value= minValue;
        return this;
    }

    /***
     * Sets the value to be returned when the knob is at its maximum position (127 by default).
     * @param maxValue
     * @return
     */
    public Knob maxValue(float maxValue) {
        this.max_value= maxValue;
        return this;
    }

    /**
     * Sets the minimum and maximum values to return when knobs is at its minimum and maximum
     * position, respectively.
     * @param minValue
     * @param maxValue
     * @return
     */
    public Knob range(float minValue,float maxValue) {
        this.min_value= minValue;
        this.max_value= maxValue;
        return this;
    }


    /***
     * Sets the stored value of the knob position. If knob has a default value set
     * with `defaultValue(float value)`, this method will only update the stored value
     * after the knob has passed once over the default value.
     * @param value
     */
    private void value(float value) {
        this.value = value;
        setFloatField(parent_object,variable,this.value);
    }


    public float value() {
        if(hasDefault)
            return this.defaultValue;
        return value;

    }
    private void setFloatField(Object parent, String fieldName, float value){

        if(fieldName == null)
            return;
        try {
            Field field = parent.getClass().getDeclaredField(fieldName);
            //println("Field:" + field.getName());
            field.setAccessible(true);
            field.setFloat(parent, value);
        } catch (Exception e) {
            System.out.println("LaunchController error - variable not found in the sketch: " + variable);
        }
    }

    public Knob variable(String variable) {
        this.variable = variable;
        return this;
    }

    public Knob plugTo(Object parent_object) {
        this.parent_object = parent_object;
        return this;
    }
    /***
     * The name of the variable to set when knob changes
     */
    protected String variable;


    public Knob position(int position) {
        if(hasDefault && this.knob_position != position)
            return this;

        this.hasDefault = false;
        this.knob_position = position;
        this.value(min_value + knob_position/MAX_POSITION * (max_value - min_value));
        return this;
    }

    public int position() {
        return this.knob_position;
    }
}
