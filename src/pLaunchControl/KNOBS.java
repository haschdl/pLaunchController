package pLaunchControl;

/**
 * The 16 knobs of the controller, plus 8 sliders.
 * Knobs from the upper row are named KNOB_x_HIGH, where x goes
 * from 1 to 8, as labeled on the controller.
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
    KNOB_1_MED(8),
    KNOB_2_MED(9),
    KNOB_3_MED(10),
    KNOB_4_MED(11),
    KNOB_5_MED(12),
    KNOB_6_MED(13),
    KNOB_7_MED(14),
    KNOB_8_MED(15),
    KNOB_1_LOW(16),
    KNOB_2_LOW(17),
    KNOB_3_LOW(18),
    KNOB_4_LOW(19),
    KNOB_5_LOW(20),
    KNOB_6_LOW(21),
    KNOB_7_LOW(22),
    KNOB_8_LOW(23);

    private final byte code;
    public byte code() { return code; }
    KNOBS(int code)
    {
        this.code = (byte)code;
    }
}
