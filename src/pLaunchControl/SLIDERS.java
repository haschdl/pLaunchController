package pLaunchControl;


/**
 * The 8 sliders of the controller. Sliders are named SLIDER_x,
 * where x goes from 1 to 8.
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

