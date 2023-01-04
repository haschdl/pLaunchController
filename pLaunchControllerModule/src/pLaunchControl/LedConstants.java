package pLaunchControl;

/**
 * Hexadecimal values representing the possible combinations of color and intensity of
 * pads in the MIDI controller.
 * Values were extracted from the manufacturer documentation.
 */
public class LedConstants {

    public static final byte OFF =  0x0C;
    public static final byte RED_LOW=  0x0D;
    public static final byte RED_FULL =  0x0B; //0x0F for normal, 0x0B to enable flashing
    public static final byte AMBER_LOW =  0x1D;
    public static final byte AMBER_FULL=  0x3F;
    public static final byte YELLOW_FULL =  0x3A; //3F for normal use, 0x3A to enable flashing
    public static final byte GREEN_LOW =  0x1C;
    public static final byte GREEN_FULL =  0x3C; //0x3c for normal use, 0x38 to enable flashing

}
