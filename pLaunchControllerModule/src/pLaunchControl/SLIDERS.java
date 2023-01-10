package pLaunchControl;

/**
 * A list of numbers that represent sliders on midi devices.
 * This list is device-agnostic. To use a list that is
 * limited to a given device, use an implementation of
 * {@link MidiDevice}
 */
public enum SLIDERS {

    SLIDER_1(0),
    SLIDER_2(1),
    SLIDER_3(2),
    SLIDER_4(3),
    SLIDER_5(4),
    SLIDER_6(5),
    SLIDER_7(6),
    SLIDER_8(7);

    private final byte code;
    public byte code() { return code; }
    SLIDERS(int code)
    {
        this.code = (byte)code;
    }
}

