package pLaunchControl;

import processing.core.PApplet;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiDeviceProvider;

import javax.sound.midi.*;
import java.lang.reflect.Method;

import static processing.core.PApplet.println;

/**
 * <p>A library for mapping Processing to the MIDI Controller named Novation Launch Control XL.</p>
 * <p>The most trivial application of library is to adjust and control Processing sketches using
 * the 32 knobs and sliders, as well as the 16 pads of the controller, by attaching Knobs and Pads to variables.</p>
 * <p>Note: 1) a slider is handled as a Knob. 2) Pads on bottom two rows are supported, however the controller's
 * other pads (template type selectors, send select, track select, device, mute, solo, and record arm) are not mapped.</p>
 * <p>The easiest way to attach a Knob to a variable in a Processing sketch is to declare the knob object like this:</p>
 * <p>{@code
 * Knob knob01;
 * }</p> 
 * <p>And then inside the sketch's `setup()` method, initialize the object, attach the object to the variable using
 * {@see Knob.variable(String) Knob.variable(String variableName}, and also set the knob's range and default value
 * like this:</p>
 * <p>{@code
 * knob01 = controller.getKnob(KNOBS.values()[0]);
 * knob01.variable("variableName");
 * knob01.range(0f, 100f);
 * knob01.defaultValue(50f);
 * }</p>
 * <p>Similarly, the easiest way to attach a Pad to a variable is to use 
 * {@code
 * Pad pad01;
 * }</p>
 * <p>And then inside the sketch's `setup()` method, initialize the object, attach the object to the variable using
 * {@see Knob.variable(String) Knob.variable(String variableName}, and also set the pad's default value like this:</p>
 * <p>{@code
 * pad01 = controller.getPad(PADS.values()[0]);
 * pad01.variable("variableName");
 * pad01.defaultValue(false);
 * }</p>
 * <p>Alternatively, you can also use the following events (*) to track changes to knobs, sliders, and pads:</p>
 * <p>{@code  void LaunchControlChanged()} is triggered when either a knob, slider, or a pad has changed.
 * This event does not provide additional information of which knob or pad has changed.</p>
 * <p>{@code  void LaunchControlKnobChanged(KNOBS knob)} is trigger when a knob or slider has changed.
 * The parameter knob will have the value of which knob or slider was changed.</p>
 * <p>{@code  void LaunchControlPadChanged(PADS pad)} is trigger when a pad has changed.
 * The parameter pad will have the value of which pad was changed.</p>
 */

public class LaunchControlXL implements pLaunchControl.midi.MidiDevice {
	
    Method controllerChangedEventMethod, knobChangedEventMethod, sliderChangedEventMethod, padChangedEventMethod;

    public static final String DEVICE_NAME_SUFFIX = "Launch Control XL";
    private static final String controlChangedEventName = "LaunchControlChanged";
    private static final String knobChangedEventName = "LaunchControlKnobChanged";
    private static final String sliderChangedEventName = "LaunchControlSliderChanged";
    private static final String padChangedEventName = "LaunchControlPadChanged";

    private Knob[] knobValues = new Knob[24];
    private Slider[] sliderValues = new Slider[8];
    private Pad[] padValues = new Pad[16];

    private PADMODE padMode;    

    javax.sound.midi.MidiDevice deviceIn;
    javax.sound.midi.MidiDevice deviceOut;
    javax.sound.midi.MidiDevice.Info[] infos = null;
    
    LaunchControlDeviceReceiver receiver;
    
    public boolean debug = false;

    PApplet parent;

    public LaunchControlXL(PApplet parent, boolean debug) throws MidiUnavailableException {
        this(parent, debug, null);
    }

    public LaunchControlXL(PApplet parent) throws MidiUnavailableException {
        this(parent, false, null);
    }

    public LaunchControlXL(PApplet parent, boolean debug, String deviceName) throws MidiUnavailableException {
        this.parent = parent;
        for (int i = 0, l = KNOBS.values().length; i < l; i++) {
            knobValues[i] = new Knob(i,parent);
        }
        for (int i = 0, l = SLIDERS.values().length; i < l; i++) {
            sliderValues[i] = new Slider(i,parent);
        }
        for (int i = 0, l = PADS.values().length; i < l; i++) {
            padValues[i] = new Pad(i,parent);
        }
        infos = CoreMidiDeviceProvider.getMidiDeviceInfo();
        for (javax.sound.midi.MidiDevice.Info info : infos) {
            javax.sound.midi.MidiDevice device = MidiSystem.getMidiDevice(info);
            if (this.debug)
                System.out.println(String.format("Device: %s \t Receivers: %d \t Transmitters: %d", info, device.getMaxReceivers(), device.getMaxTransmitters()));
            if ((deviceName != null && info.getName().endsWith(deviceName)) || info.getName().endsWith(DEVICE_NAME_SUFFIX)) {
                if (device.getMaxReceivers() == 0) {
                    deviceIn = device;
                    println("Connected to MIDI Input.");
                } else if (device.getMaxTransmitters() == 0) {
                    println("Connected to MIDI Output.");
                    deviceOut = device;
                }
            }
        }

        if (deviceIn == null) {
            if (deviceName == null)
                System.out.printf("A device with name ending with %s was not detected.\n",DEVICE_NAME_SUFFIX);
            else
                System.out.printf("A device with name you provided was not detected.\nCheck the device name: %s \n", deviceName);
            return;
        }

        try {
            controllerChangedEventMethod =
                    parent.getClass().getMethod(controlChangedEventName);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }

        try {
            knobChangedEventMethod =
                    parent.getClass().getMethod(knobChangedEventName, KNOBS.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }

        try {
            sliderChangedEventMethod =
                    parent.getClass().getMethod(sliderChangedEventName, SLIDERS.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }
        
        try {
            padChangedEventMethod =
                    parent.getClass().getMethod(padChangedEventName, PADS.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }

        if (controllerChangedEventMethod == null
        		&& knobChangedEventMethod == null
                && sliderChangedEventName == null
                && padChangedEventMethod == null) {
            println("WARNING! This sketch is not tracking any changes to the Launch Control XL. Make sure you sketch includes at least one of the following:");
            println("   void " + controlChangedEventName + "()");
            println("   void " + padChangedEventName + "(PADS pad)");
            println("   void " + knobChangedEventMethod + "(KNOBS knob)");

        }

        deviceIn.open();
        deviceOut.open();

        receiver = new LaunchControlDeviceReceiver(this, deviceOut);
        deviceIn.getTransmitter().setReceiver(receiver);
        deviceOut.getReceiver().send(getResetMessage(), 0);
        
        setPadMode(PADMODE.TOGGLE);

    }
    
    /***
     * Enable or disable debug messages
     */
    @Override
    public void debug(boolean debugStatus) {

        debug = debugStatus;
        
    }

    @Override
    public boolean debug() {
        return debug;
    }

    @Override
    public boolean isKnobMessage(MidiMessage message) {
        return (message.getMessage()[0] == -80);
    }

    @Override
    public void setKnobPosition(MidiMessage message) {
        byte[] msgBytes = message.getMessage();
        switch (msgBytes[1]) {
            case 13:
                setKnobPosition(KNOBS.KNOB_01, msgBytes[2]);
                break;
            case 14:
                setKnobPosition(KNOBS.KNOB_02, msgBytes[2]);
                break;
            case 15:
                setKnobPosition(KNOBS.KNOB_03, msgBytes[2]);
                break;
            case 16:
                setKnobPosition(KNOBS.KNOB_04, msgBytes[2]);
                break;
            case 17:
                setKnobPosition(KNOBS.KNOB_05, msgBytes[2]);
                break;
            case 18:
                setKnobPosition(KNOBS.KNOB_06, msgBytes[2]);
                break;
            case 19:
                setKnobPosition(KNOBS.KNOB_07, msgBytes[2]);
                break;
            case 20:
                setKnobPosition(KNOBS.KNOB_08, msgBytes[2]);
                break;
            case 29:
                setKnobPosition(KNOBS.KNOB_09, msgBytes[2]);
                break;
            case 30:
                setKnobPosition(KNOBS.KNOB_10, msgBytes[2]);
                break;
            case 31:
                setKnobPosition(KNOBS.KNOB_11, msgBytes[2]);
                break;
            case 32:
                setKnobPosition(KNOBS.KNOB_12, msgBytes[2]);
                break;
            case 33:
                setKnobPosition(KNOBS.KNOB_13, msgBytes[2]);
                break;
            case 34:
                setKnobPosition(KNOBS.KNOB_14, msgBytes[2]);
                break;
            case 35:
                setKnobPosition(KNOBS.KNOB_15, msgBytes[2]);
                break;
            case 36:
                setKnobPosition(KNOBS.KNOB_16, msgBytes[2]);
                break;
            case 49:
                setKnobPosition(KNOBS.KNOB_17, msgBytes[2]);
                break;
            case 50:
                setKnobPosition(KNOBS.KNOB_18, msgBytes[2]);
                break;
            case 51:
                setKnobPosition(KNOBS.KNOB_19, msgBytes[2]);
                break;
            case 52:
                setKnobPosition(KNOBS.KNOB_20, msgBytes[2]);
                break;
            case 53:
                setKnobPosition(KNOBS.KNOB_21, msgBytes[2]);
                break;
            case 54:
                setKnobPosition(KNOBS.KNOB_22, msgBytes[2]);
                break;
            case 55:
                setKnobPosition(KNOBS.KNOB_23, msgBytes[2]);
                break;
            case 56:
                setKnobPosition(KNOBS.KNOB_24, msgBytes[2]);
                break;
            case 77:
                setSliderPosition(SLIDERS.SLIDER_1, msgBytes[2]);
                break;
            case 78:
                setSliderPosition(SLIDERS.SLIDER_2, msgBytes[2]);
                break;
            case 79:
                setSliderPosition(SLIDERS.SLIDER_3, msgBytes[2]);
                break;
            case 80:
                setSliderPosition(SLIDERS.SLIDER_4, msgBytes[2]);
                break;
            case 81:
                setSliderPosition(SLIDERS.SLIDER_5, msgBytes[2]);
                break;
            case 82:
                setSliderPosition(SLIDERS.SLIDER_6, msgBytes[2]);
                break;
            case 83:
                setSliderPosition(SLIDERS.SLIDER_7, msgBytes[2]);
                break;
            case 84:
                setSliderPosition(SLIDERS.SLIDER_8, msgBytes[2]);
                break;
        }
        return;
    }

    @Override
    public boolean isPad(MidiMessage message) {
        return (message.getMessage()[0] == -112 || message.getMessage()[0] == -128);
    }

    @Override
    public PADS getPadFromMessage(MidiMessage message) {
        byte[] msgBytes = message.getMessage();
        PADS pad = null;

        switch (msgBytes[1]) {
            case 41:
                if (msgBytes[2] == 127)
                    pad = PADS.PAD_1;
                break;
            case 42:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_2;
                }
                break;
            case 43:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_3;
                }
                break;
            case 44:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_4;
                }
                break;
            case 57:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_5;
                }
                break;
            case 58:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_6;
                }
                break;
            case 59:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_7;
                }
                break;
            case 60:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_8;
                }
                break;
            case 73:
                if (msgBytes[2] == 127)
                    pad = PADS.PAD_9;
                break;
            case 74:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_10;
                }
                break;
            case 75:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_11;
                }
                break;
            case 76:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_12;
                }
                break;
            case 89:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_13;
                }
                break;
            case 90:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_14;
                }
                break;
            case 91:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_15;
                }
                break;
            case 92:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_16;
                }
                break;
        }
        return pad;
    }

    /***
     * Clean-up operations executed when when
     * the parent sketch shuts down.
     */
    @Override
    public void close() {

        if (deviceIn.isOpen()) {
            deviceIn.close();
        }

        if (deviceOut.isOpen()) {
            deviceOut.close();
        }
    }
    
    // ****** KNOBS ****** //

    /***
     * Returns the {@link Knob} object for one of the knobs in the controller.
     * @param knob
     * @return A {@link Knob} object corresponding to the physical knob.
     */
    @Override
    public Knob getKnob(KNOBS knob) {
    	if (debug) System.out.println("getKnob called for code..." + knob.code());
        return knobValues[knob.code()];
    }



    @Override
    public void setKnobPosition(KNOBS knob, int position) {
        int curPosition = knobValues[knob.code()].position();
        int newPosition = knobValues[knob.code()].position(position).position();
        if (newPosition != curPosition) {
            midiLaunchControlChanged();
            if (debug) System.out.println("setKnobPosition called for knob " +
            		knob.code() +
            		", new position: " + position);
            knobChanged(knob);
        }
    }


    /***
     * Sets the range of the output of the all controller knobs. By default, the knobs range from 0-127.
     * Call `setKnobValueRange` to make the knobs values fluctuate in another range, for example to
     * match RGB range from 0-255.
     * @param knob_min The value to return when knobs are in the left-most position.
     * @param knob_max The value to return when knobs are in the right-most position.
     */
    @Override
    public void setKnobValueRange(float knob_min, float knob_max) {
        for (int i = 0, l = KNOBS.values().length; i < l; i++) {
            knobValues[i].range(knob_min, knob_max);
        }
    }

    // ****** SLIDERS ****** //

    /***
     * Returns the {@link Slider} object for one of the sliders in the controller.
     * @param slider
     * @return A {@link Slider} object corresponding to the physical slider.
     */
    @Override
    public Slider getSlider(SLIDERS slider) {
    	if (debug) System.out.println("getSlider called for code..." + slider.code());
        return sliderValues[slider.code()];
    }



    @Override
    public void setSliderPosition(SLIDERS slider, int position) {
        int curPosition = sliderValues[slider.code()].position();
        int newPosition = sliderValues[slider.code()].position(position).position();
        if (newPosition != curPosition) {
            midiLaunchControlChanged();
            if (debug) System.out.println("setSliderPosition called for slider " +
            		slider.code() +
            		", new position: " + position);
            sliderChanged(slider);
        }
    }


    /***
     * Sets the range of the output of the all controller knobs. By default, the knobs range from 0-127.
     * Call `setKnobValueRange` to make the knobs values fluctuate in another range, for example to
     * match RGB range from 0-255.
     * @param slider_min The value to return when knobs are in the left-most position.
     * @param slider_max The value to return when knobs are in the right-most position.
     */
    @Override
    public void setSliderValueRange(float slider_min, float slider_max) {
        for (int i = 0, l = SLIDERS.values().length; i < l; i++) {
            sliderValues[i].range(slider_min, slider_max);
        }
    }

    
    // ****** PADS ****** //

    /***
     * The status of a given {@link PADS} as a boolean value.
     * Alternative, you can get the status of a pad a int value with {@link LaunchControl#getPadInt(PADS)}
     * @param pad The pad to check.
     * @return True if the pad is "on", False otherwise.
     */
    @Override
    public Pad getPad(PADS pad) {
        // OLD code: return padStatus[(int) pad.code()];
    	if (debug) println("getPad called for code..." + pad.code());
    	return padValues[pad.code()];
    }

    @Override
    public Pad[] getPads() {
        return this.padValues;
    }

    public void setPad(PADS pad, boolean value) {
        if (debug) System.out.println("setPad called for pad " + pad.code());
        setPad(pad, value, false);
    }

    @Override
    public void setPad(PADS pad, boolean value, boolean disableEvents) {
        if (padValues[pad.code()].value() != value) {
            padValues[pad.code()].value(value);
            receiver.sendLedOnOff(value, pad);
            if(!disableEvents) {
                midiLaunchControlChanged();
                padChanged(pad);
            }
        }
    }

    /**
     * Inverts the value and turns the given pad on and off.
     *
     * @param pad
     */
    @Override
    public void invertPad(PADS pad) {
        boolean curValue = padValues[pad.code()].value();
        padValues[pad.code()].value(!curValue);
        midiLaunchControlChanged();
        padChanged(pad);
    }

    /**
     * @return The {@link PADMODE} describing the mode of operation for pads.
     */
    @Override
    public PADMODE getPadMode() {
        return padMode;
    }


    /**
     * Updates how the controller will handle pads. Default is {@link PADMODE#TOGGLE}.
     * @param padMode The {@link PADMODE} describing the mode of operation for pads.
     */
    @Override
    public void setPadMode(PADMODE padMode) {
        this.padMode = padMode;
    }



    // ****** EVENTS ****** //

    public void midiLaunchControlChanged() {
        if (controllerChangedEventMethod != null) {
            try {
                //controllerChangedEventMethod.invoke(parent, new Object[]{this});
                controllerChangedEventMethod.invoke(parent);
            } catch (Exception e) {
                System.err.println("Disabling controllerChangedEventMethod() for " + this.getClass().getName() +
                        " because of an error.");
                e.printStackTrace();
                controllerChangedEventMethod = null;
            }
        }
    }

    public void knobChanged(KNOBS knob) {
        if (knobChangedEventMethod != null) {
            try {
                //controllerChangedEventMethod.invoke(parent, new Object[]{this});
                knobChangedEventMethod.invoke(parent, knob);
            } catch (Exception e) {
                System.err.println("Disabling " + knobChangedEventMethod + "() for " + this.getClass().getName() +
                        " because of an error.");
                e.printStackTrace();
                knobChangedEventMethod = null;
            }
        }
    }

    public void sliderChanged(SLIDERS slider) {
        if (sliderChangedEventMethod != null) {
            try {
                //controllerChangedEventMethod.invoke(parent, new Object[]{this});
                sliderChangedEventMethod.invoke(parent, slider);
            } catch (Exception e) {
                System.err.println("Disabling " + sliderChangedEventMethod + "() for " + this.getClass().getName() +
                        " because of an error.");
                e.printStackTrace();
                sliderChangedEventMethod = null;
            }
        }
    }


    public void padChanged(PADS pad) {
        if (padChangedEventMethod != null) {
            try {
                //controllerChangedEventMethod.invoke(parent, new Object[]{this});
                padChangedEventMethod.invoke(parent, pad);
            } catch (Exception e) {
                System.err.println("Disabling " + padChangedEventName + "() for " + this.getClass().getName() +
                        " because of an error.");
                e.printStackTrace();
                padChangedEventMethod = null;
            }
        }
    }

    private static MidiMessage getResetMessage() {
        try {
            ShortMessage reset = new ShortMessage(184, 0, 0);
            return reset;
        } catch (InvalidMidiDataException e) {
            return null;
        }
    }
    
}