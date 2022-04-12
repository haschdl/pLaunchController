package pLaunchControl;

/**
 * A list of numbers that represent pads on midi devices.
 * This list is device-agnostic. To use a list that is
 * limited to a given device, use DevicePad
 */
public enum PADS {
    /**
     * The first pad from left to right.
     */
    PAD_1(0),
    /**
     * The second pad from left to right.
     */
    PAD_2(1),
    /**
     * The third pad from left to right.
     */
    PAD_3(2),
    /**
     * The fourth pad from left to right.
     */
    PAD_4(3),
    /**
     * The fifth pad from left to right.
     */
    PAD_5(4),
    /**
     * The sixth pad from left to right.
     */
    PAD_6(5),
    /**
     * The seventh pad from left to right.
     */
    PAD_7(6),
    /**
     * The eighth pad from left to right.
     */
    PAD_8(7),
    /**
     * The ninth pad from left to right.
     */
    PAD_9(8),
    /**
     * The tenth pad from left to right.
     */
    PAD_10(9),
    /**
     * The eleventh pad from left to right.
     */
    PAD_11(10),
    /**
     * The twelfth pad from left to right.
     */
    PAD_12(11),
    /**
     * The thirteenth pad from left to right.
     */
    PAD_13(12),
    /**
     * The fourteenth pad from left to right.
     */
    PAD_14(13),
    /**
     * The fifteenth pad from left to right.
     */
    PAD_15(14),
    /**
     * The sixteenth pad from left to right.
     */
    PAD_16(15);

    private final byte code;

    public byte code() { return code; }

    PADS(int code)
    {
        this.code = (byte)code;
    }
}

