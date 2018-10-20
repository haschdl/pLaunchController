package pLaunchController;

import processing.core.*;

import java.lang.reflect.*;

import javax.sound.midi.*;

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
 * {@code  void launchControllerChanged()} is triggered when either a knob or a pad has changed.
 * This event does not provide additional information of which knob or pad has changed.<br >
 * </p>
 * <p>
 * {@code  void launchControllerKnobChanged(KNOBS knob)} is trigger when a knob has changed.
 * The parameter knob will have the value of which knob was changed.<br >
 *</p>
 * TWO_PI + HALF_PI * .{@code  void launchControllerPadChanged(PADS pad)} is triggered when a pad has switched states.
 * The parameter pad will have the value of which pad was changed. <br >
 *
 * (*) In Processing, simply add a new method with the names and parameters as above.
 *
 */
public class LaunchController {
    Method controllerChangedEventMethod, knobChangedEventMethod, padChangedEventMethod;
    private static final String controlChangedEventName = "launchControllerChanged";
    private static final String knobChangedEventName = "launchControllerKnobChanged";
    private static final String padChangedEventName = "launchControllerPadChanged";
    private PADMODE padMode;

    private Knob[] knobValues = new Knob[16];

    MidiDevice deviceIn;
    MidiDevice deviceOut;
    MidiDevice.Info[] infos = null;
    LaunchControllerReceiver receiver;

    private boolean[] padStatus = new boolean[8];

    PApplet parent;

    /***
     * Returns the {@link Knob} object for one of the knobs in the controller.
     * @param knob
     * @return A {@link Knob} object corresponding to the physical knob.
     */
    public Knob getKnob(KNOBS knob) {
        return knobValues[knob.code()];
    }



    public void setKnobPosition(KNOBS knob, int position) {
        int curPosition = knobValues[knob.code()].position();
        int newPosition = knobValues[knob.code()].position(position).position();
        if (newPosition != curPosition) {
            midiLaunchControllerChanged();
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


    public LaunchController(PApplet parent) throws MidiUnavailableException {
        this.parent = parent;
        for (int i = 0, l = KNOBS.values().length; i < l; i++) {
            knobValues[i] = new Knob(i,parent);
        }
        infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            if (info.getName().equals("Launch Control")) {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                if (info.getClass().getName().equals("com.sun.media.sound.MidiInDeviceProvider$MidiInDeviceInfo")) {
                    deviceIn = device;
                    println("Connected to Launch Control MIDI Input.");
                } else {
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

        if (controllerChangedEventMethod == null
                && padChangedEventName == null
                && knobChangedEventMethod == null) {
            println("WARNING! This sketch is not tracking any changes to the Launch Control. Make sure you sketch includes at least one of the following:");
            println("   void " + controlChangedEventName + "()");
            println("   void " + padChangedEventName + "(PADS pad)");
            println("   void " + knobChangedEventMethod + "(KNOBS knob)");

        }

        deviceIn.open();
        deviceOut.open();

        receiver = new LaunchControllerReceiver(this, deviceOut);
        deviceIn.getTransmitter().setReceiver(receiver);
        deviceOut.getReceiver().send(getResetMessage(), 0);


        setPadMode(PADMODE.TOGGLE);
    }


    /***
     * The status of a given {@link PADS} as a boolean value.
     * Alternative, you can get the status of a pad a int value with {@link LaunchController#getPadInt(PADS)}
     * @param pad The pad to check.
     * @return True if the pad is "on", False otherwise.
     */
    public boolean getPad(PADS pad) {
        return padStatus[(int) pad.code()];
    }

    /***
     * The status of a given pad as int value.
     * @param pad The pad to check.
     * @return 1 if the pad is on, 0 otherwise.
     */
    public int getPadInt(PADS pad) {
        return padStatus[(int) pad.code()] ? 1 : 0;
    }

    public void setPad(PADS pad, boolean value) {
        if (padStatus[(int) pad.code()] != value) {
            padStatus[(int) pad.code()] = value;
            receiver.sendLedOnOff(value, pad);
            midiLaunchControllerChanged();
            padChanged(pad);
        }
    }


    /**
     * Inverts the value and turns the given pad on and off.
     *
     * @param pad
     */
    public void invertPad(PADS pad) {
        boolean curValue = padStatus[(int) pad.code()];
        padStatus[(int) pad.code()] = !curValue;
        midiLaunchControllerChanged();
        padChanged(pad);
    }


    public void midiLaunchControllerChanged() {
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

    private static MidiMessage getResetMessage() {
        try {
            ShortMessage reset = new ShortMessage(184, 0, 0);
            return reset;
        } catch (InvalidMidiDataException e) {
            return null;
        }
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
     * @param padMode The {@link PADMODE} describing the mode of operation for pads.
     */
    public void setPadMode(PADMODE padMode) {
        this.padMode = padMode;
    }
}