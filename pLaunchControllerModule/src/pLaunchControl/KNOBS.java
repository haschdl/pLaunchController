package pLaunchControl;

/**
 * A list of numbers that represent knobs on midi devices.
 * This list is device-agnostic. To use a list that is
 * limited to a given device, use an implementation of
 * {@link MidiDevice}
 */
public enum KNOBS {
    KNOB_01(0),
    KNOB_02(1),
    KNOB_03(2),
    KNOB_04(3),
    KNOB_05(4),
    KNOB_06(5),
    KNOB_07(6),
    KNOB_08(7),
    KNOB_09(8),
    KNOB_10(9),
    KNOB_11(10),
    KNOB_12(11),
    KNOB_13(12),
    KNOB_14(13),
    KNOB_15(14),
    KNOB_16(15),
    KNOB_17(16),
    KNOB_18(17),
    KNOB_19(18),
    KNOB_20(19),
    KNOB_21(20),
    KNOB_22(21),
    KNOB_23(22),
    KNOB_24(23);

    private final byte code;
    public byte code() { return code; }
    KNOBS(int code)
    {
        this.code = (byte)code;
    }
}
