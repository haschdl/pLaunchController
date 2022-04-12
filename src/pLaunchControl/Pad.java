package pLaunchControl;

import java.lang.reflect.Field;


/**
 * A logical representation of the physical pad in the MIDI controller.
 * The most trivial usage of a Pad is to adjust the value of a variable in
 * your code, using {@link #variable(String) variable} method.
 *
 * A Pad object can be assigned a default value.
 *
 */
public class Pad {

    private boolean defaultValue;
    private boolean value;

    public PADS controllerPad;
    private Object parent_object;

    private boolean hasDefault = false;

    protected Pad(int padCode) {
        controllerPad = PADS.values()[padCode];
    }

    protected Pad(int padCode, Object parent) {
        controllerPad = PADS.values()[padCode];
        parent_object = parent;
    }


    /**
     * Sets the default value of the Pad object. You can read the current value of the Pad with {@link #value() value()}.
     * @param defaultValue The default value to assign Pad object. Remember that calling defaultValue() does not change the
     *                     value of the physical pad, but it's useful to set an initial value to the attached variable.
     * @return The Pad itself.
     */
    public Pad defaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;

        this.hasDefault = true;
        if (variable != null) {
            setBooleanField(parent_object, variable, defaultValue);
        }
        return this;
    }

    /***
     * Sets the stored value of the pad position. If pad has a default value set
     * with `defaultValue(boolean value)`, this method will only update the stored value
     * after the real pad has been pressed.
     *
     * @param value The default value of the pad.
     */
    public Pad value(boolean value) {
        if (hasDefault) hasDefault = false;
        this.value = value;
        setBooleanField(parent_object, variable, this.value);
        return this;
    }


    /**
     * @return The current value of the pad object.
     */
    public boolean value() {
        if (hasDefault)
            return this.defaultValue;
        return value;
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


    private void setBooleanField(Object parent, String fieldName, boolean value) {

        Field field = getField(parent,fieldName);
        if (field == null)
            return;

        field.setAccessible(true);
        try {
            field.setBoolean(parent, value);
        }
        catch (IllegalAccessException e) {
            System.err.printf("LaunchControl Error: It was not possible to set the value of the variable %s. Error message: %s%n", fieldName, e.getMessage());
        }

    }

    /**
     * Attaches the pad value to a variable. The variable name must match a boolean variable
     * in your sketch. This method returns the current instance of Pad, to enable chaining
     * of methods during start up.
     * @param variable
     * @return The current instance of Pad.
     */
    public Pad variable(String variable) {
        this.variable = variable;
        setBooleanField(parent_object, variable, value());
        return this;
    }

    public String getVariable() {
        return this.variable;
    }


    public Pad plugTo(Object parent_object) {
        this.parent_object = parent_object;
        return this;
    }

    /***
     * The name of the variable to set when pad changes
     */
    protected String variable;

}
