package pLaunchController;

/**
 * The 16 knobs of the controller.
 */
public enum KNOBS {
    KNOB_1_HIGH(0),
    KNOB_2_HIGH(1),
    KNOB_3_HIGH(2),
    KNOB_4_HIGH(3),
    KNOB_5_HIGH(4),
    KNOB_6_HIGH(5),
    KNOB_7_HIGH(6),
    KNOB_8_HIGH(7),
    KNOB_1_LOW(8),
    KNOB_2_LOW(9),
    KNOB_3_LOW(10),
    KNOB_4_LOW(11),
    KNOB_5_LOW(12),
    KNOB_6_LOW(13),
    KNOB_7_LOW(14),
    KNOB_8_LOW(15);


    private final byte code;
    public byte code() { return code; }
    KNOBS(int code)
    {
        this.code = (byte)code;
    }
}
