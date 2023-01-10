package pLaunchControl;

import processing.core.PApplet;

/**
 * A generic controller in the series Launch Control, containing shared functionality
 * for initialization and shut-down operations.
 */
public abstract class MidiController {
    public boolean debug;
    javax.sound.midi.MidiDevice deviceIn;
    javax.sound.midi.MidiDevice deviceOut;
    PApplet parent;

    public MidiController(PApplet parent, boolean debug) {
        parent.registerMethod("dispose", this);
        this.debug = debug;
        this.parent = parent;
    }

    /***
     * Clean-up operations executed when
     * the parent sketch shuts down.
     */
    public void dispose() {

        if (deviceIn != null && deviceIn.isOpen()) {
            deviceIn.close();
        }

        if (deviceOut != null && deviceOut.isOpen()) {
            deviceOut.close();
        }
    }


}
