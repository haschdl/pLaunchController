package pLaunchControl;

import pLaunchControl.*;

import javax.sound.midi.MidiMessage;

/***
 * An interface for a generic Midi device to be supported by this library.
 * A MidiDevice implementation provides functionality to read sliders,
 * pads and knobs, as well as set state in the device, for example
 * turning lights on and off.
 * The implementations of this interface are device-specific, and might
 * not provide functionality for all features. For example, a Midi device
 * might have pads and buttons but not sliders.
 */
public interface MidiDevice {

    /**
     * Set the debug mode.
     * @param debugStatus
     */
    void debug(boolean debugStatus);

    boolean debug();

    /**
     * Check if a Midi message is from a Knob.
     * @param message
     * @return True if the message is coming from a Knob.
     */
    boolean isKnobMessage(MidiMessage message);

    Knob getKnob(KNOBS knob);

    void setKnobPosition(KNOBS knob, int position);

    void setKnobPosition(MidiMessage message);

    void setKnobValueRange(float knob_min, float knob_max);


    /***
     * Tells if a MidiMessage is from a Pad.
     * @param message
     * @return True if MidiMessage is coming from a Pad button.
     */
    boolean isPad(MidiMessage message);

    PADS getPadFromMessage(MidiMessage message);

    Pad getPad(PADS pad);

    Pad[] getPads();

    /***
     * Set a pad to a new value.
     * @param pad
     * @param value
     */
    void setPad(PADS pad, boolean value);

    /**
     * Sets a pad to a new value, optionally
     * disabling events.
     * events.
     * @param pad
     * @param value New value to set the pad
     * @param disableEvents True to disable any events from being triggered
     */
    void setPad(PADS pad, boolean value, boolean disableEvents);


    void invertPad(PADS pad);

    PADMODE getPadMode();

    void setPadMode(PADMODE padMode);


    Slider getSlider(SLIDERS slider);

    void setSliderPosition(SLIDERS slider, int position);

    void setSliderValueRange(float slider_min, float slider_max);

}
