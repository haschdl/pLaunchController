package pLaunchControl;

/**
 * The 8 pads of the controller.
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
    PAD_8(7);

    private final byte code;
    protected byte code() { return code; }

    PADS(int code)
    {
        this.code = (byte)code;
    }
}

