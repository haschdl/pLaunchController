package pLaunchControl;

import processing.core.PApplet;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.lang.reflect.Method;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiDeviceProvider;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiNotification;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiException;


import static processing.core.PApplet.println;

/***
 * A wrapper for the MIDI controller Novation LaunchControl.<br > The most trivial application of this
 * wrapper is to adjust and control your Processing sketch using the knobs and pads of the controller,
 * by attaching Knobs to variables.
 *
 * The easiest way to attach a Knob to a variable is to use {@see Knob.variable(String) Knob.variable(String variableName},
 * inside Processing `setup()` method:
 *
 * {@code
 *
 * }
 *
 * Alternatively, you can also use the following events (*) to  track changes to knobs and pads: <br >
 *<p>
 * {@code  void LaunchControlChanged()} is triggered when either a knob or a pad has changed.
 * This event does not provide additional information of which knob or pad has changed.<br >
 * </p>
 * <p>
 * {@code  void LaunchControlKnobChanged(KNOBS knob)} is trigger when a knob has changed.
 * The parameter knob will have the value of which knob was changed.<br >
 *</p>
 * TWO_PI + HALF_PI * .{@code  void LaunchControlPadChanged(PADS pad)} is triggered when a pad has switched states.
 * The parameter pad will have the value of which pad was changed. <br >
 *
 * (*) In Processing, simply add a new method with the names and parameters as above.
 *
 */
public class LaunchControl implements MidiDevice {
    Method controllerChangedEventMethod, knobChangedEventMethod, padChangedEventMethod;
    private static final String controlChangedEventName = "LaunchControlChanged";
    private static final String knobChangedEventName = "LaunchControlKnobChanged";
    private static final String padChangedEventName = "LaunchControlPadChanged";
    private PADMODE padMode;

    private static final int KNOB_COUNT = 16;
    private static final int PAD_COUNT = 8;
    private Knob[] knobValues = new Knob[KNOB_COUNT];
    private Pad[] padValues = new Pad[PAD_COUNT];

    javax.sound.midi.MidiDevice deviceIn;
    javax.sound.midi.MidiDevice deviceOut;
    javax.sound.midi.MidiDevice.Info[] infos = null;
    LaunchControlDeviceReceiver receiver;

    public boolean debug = false;

    PApplet parent;

    /***
     * Returns the {@link Knob} object for one of the knobs in the controller.
     * @param knob
     * @return A {@link Knob} object corresponding to the physical knob.
     */
    public Knob getKnob(KNOBS knob) {
        return knobValues[knob.code()];
    }

    /***
     * Returns the {@link Knob} object for one of the knobs in the controller,
     * given its ordinal position in the device, following a left to right,
     * top to bottom convention.
     * @param knobIndex A number representing the position of the knob in the device.
     * @return A {@link Knob} object corresponding to the physical knob.
     */
    public Knob getKnob(int knobIndex) {
        //TODO validate knobIndex
        KNOBS knobAtPosition = KNOBS.values()[knobIndex];
        return  knobValues[knobAtPosition.code()];
    }


    public void setKnobPosition(KNOBS knob, int position) {
        int curPosition = knobValues[knob.code()].position();
        int newPosition = knobValues[knob.code()].position(position).position();
        if (newPosition != curPosition) {
            midiLaunchControlChanged();
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
    public void setKnobValueRange(float knob_min, float knob_max) {
        for (int i = 0, l = KNOBS.values().length; i < l; i++) {
            knobValues[i].range(knob_min, knob_max);
        }
    }

    @Override
    public Slider getSlider(SLIDERS slider) {
        throw new UnsupportedOperationException("Method not available. This device does not have sliders.");
    }

    @Override
    public void setSliderPosition(SLIDERS slider, int position) {
        throw new UnsupportedOperationException("Method not available. This device does not have sliders.");
    }

    @Override
    public void setSliderValueRange(float slider_min, float slider_max) {
        throw new UnsupportedOperationException("Method not available. This device does not have sliders.");
    }

    public LaunchControl(PApplet parent) throws MidiUnavailableException {
        this(parent, false);
    }
    public LaunchControl(PApplet parent, boolean debug) throws MidiUnavailableException {
        this.parent = parent;
        this.debug=debug;
        for (int i = 0, l = KNOB_COUNT; i < l; i++) {
            knobValues[i] = new Knob(i, parent);
        }
        for (int i = 0, l = PAD_COUNT; i < l; i++) {
            padValues[i] = new Pad(i,parent);
        }
        infos = CoreMidiDeviceProvider.getMidiDeviceInfo();
        for (javax.sound.midi.MidiDevice.Info info : infos) {
            javax.sound.midi.MidiDevice device = MidiSystem.getMidiDevice(info);
            System.out.println(String.format("Device: %s \t Receivers: %d \t Transmitters: %d", info, device.getMaxReceivers(), device.getMaxTransmitters()));
            if (info.getName().endsWith("Launch Control")) {
                if (device.getMaxReceivers() == 0) {
                    deviceIn = device;
                    println("Connected to Launch Control MIDI Input.");
                } else if(device.getMaxTransmitters()==0) {
                    println("Connected to Launch Control MIDI Output.");
                    deviceOut = device;
                }
            }
        }

        if (deviceIn == null) {
            println("Launch Control not connected!");
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
            padChangedEventMethod =
                    parent.getClass().getMethod(padChangedEventName, PADS.class);
        } catch (Exception e) {
            // no such method, or an error.. which is fine, just ignore
        }


        deviceIn.open();
        deviceOut.open();

        receiver = new LaunchControlDeviceReceiver(this, deviceOut);

        deviceIn.getTransmitter().setReceiver(receiver);


        println("Resetting the controller...");
        deviceOut.getReceiver().send(Utils.getResetMessage(), -1);
        println("Setting to factory template...");
        deviceOut.getReceiver().send(Utils.getSetTemplateMessage(), 10);


        setPadMode(PADMODE.TOGGLE);
        println("LaunchControl ready!");
    }


    /***
     * The status of a given {@link PADS} as a boolean value.
     * Alternative, you can get the status of a pad a int value with {@link LaunchControl#getPadInt(PADS)}
     * @param pad The pad to check.
     * @return True if the pad is "on", False otherwise.
     */
    public Pad getPad(PADS pad) {
        if (debug) println("getPad called for code..." + pad.code());
        return padValues[pad.code()];
    }

    @Override
    public Pad[] getPads() {
        return this.padValues;
    }

    /***
     * Returns the {@link Pad} object for one of the pads in the controller,
     * given its ordinal position in the device, following a left to right,
     * top to bottom convention.
     * @param padIndex A number representing the position of the pad in the device.
     * @return A {@link Pad} object corresponding to the physical knob.
     */
    public Pad getPad(int padIndex) {
        //TODO validate padIndex
        PADS padAtPosition = PADS.values()[padIndex];
        return  padValues[padAtPosition.code()];
    }


    /***
     * The status of a given pad as int value.
     * @param pad The pad to check.
     * @return 1 if the pad is on, 0 otherwise.
     */
    public int getPadInt(PADS pad) {
        return getPad(pad).value() ? 1 : 0;
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
        setPad(pad, !curValue);
    }


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

    /***
     * Clean-up operations executed when when
     * the parent sketch shuts down.
     */
    public void close() {

        if (deviceIn.isOpen()) {
            deviceIn.close();
        }

        if (deviceOut.isOpen()) {
            deviceOut.close();
        }
    }

    /**
     * @return The {@link PADMODE} describing the mode of operation for pads.
     */
    public PADMODE getPadMode() {
        return padMode;
    }


    /**
     * Updates how the controller will handle pads. Default is {@link PADMODE#TOGGLE}.
     *
     * @param padMode The {@link PADMODE} describing the mode of operation for pads.
     */
    public void setPadMode(PADMODE padMode) {
        this.padMode = padMode;
    }

    @Override
    public boolean isKnobMessage(MidiMessage message) {
        return (message.getMessage()[0] == -72);
    }

    @Override
    public void setKnobPosition(MidiMessage message) {
        byte[] lastMessage = message.getMessage();
        switch (lastMessage[1]) {
            case 21:
                setKnobPosition(KNOBS.KNOB_1_HIGH, lastMessage[2]);
                break;
            case 22:
                setKnobPosition(KNOBS.KNOB_2_HIGH, lastMessage[2]);
                break;
            case 23:
                setKnobPosition(KNOBS.KNOB_3_HIGH, lastMessage[2]);
                break;
            case 24:
                setKnobPosition(KNOBS.KNOB_4_HIGH, lastMessage[2]);
                break;
            case 25:
                setKnobPosition(KNOBS.KNOB_5_HIGH, lastMessage[2]);
                break;
            case 26:
                setKnobPosition(KNOBS.KNOB_6_HIGH, lastMessage[2]);
                break;
            case 27:
                setKnobPosition(KNOBS.KNOB_7_HIGH, lastMessage[2]);
                break;
            case 28:
                setKnobPosition(KNOBS.KNOB_8_HIGH, lastMessage[2]);
                break;
            case 41:
                setKnobPosition(KNOBS.KNOB_1_MED, lastMessage[2]);
                break;
            case 42:
                setKnobPosition(KNOBS.KNOB_2_MED, lastMessage[2]);
                break;
            case 43:
                setKnobPosition(KNOBS.KNOB_3_MED, lastMessage[2]);
                break;
            case 44:
                setKnobPosition(KNOBS.KNOB_4_MED, lastMessage[2]);
                break;
            case 45:
                setKnobPosition(KNOBS.KNOB_5_MED, lastMessage[2]);
                break;
            case 46:
                setKnobPosition(KNOBS.KNOB_6_MED, lastMessage[2]);
                break;
            case 47:
                setKnobPosition(KNOBS.KNOB_7_MED, lastMessage[2]);
                break;
            case 48:
                setKnobPosition(KNOBS.KNOB_8_MED, lastMessage[2]);
                break;
        }
        return;
    }

    @Override
    public boolean isPad(MidiMessage message) {
        return message.getMessage()[0] == -104 || message.getMessage()[0] == -120;
    }

    @Override
    public PADS getPadFromMessage(MidiMessage message) {
        PADS pad = null;
        byte[] msgBytes = message.getMessage();

        switch (msgBytes[1]) {
            case 9:
                if (msgBytes[2] == 127)
                    pad = PADS.PAD_1;
                break;
            case 10:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_2;
                }
                break;
            case 11:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_3;
                }
                break;
            case 12:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_4;
                }
                break;
            case 25:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_5;
                }
                break;
            case 26:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_6;
                }
                break;
            case 27:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_7;
                }
                break;
            case 28:
                if (msgBytes[2] == 127) {
                    pad = PADS.PAD_8;
                }
                break;
        }
        return pad;
    }
}