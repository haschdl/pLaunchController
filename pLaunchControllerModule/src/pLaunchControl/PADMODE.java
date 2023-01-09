package pLaunchControl;

/**
 * Modes of operation of pads.
 */
public enum PADMODE {

    /**
     * Pad works like an on/off switch. The state of the pad is persisted, and pad is lit
     * until a second push. Several pads can be turned on at the same time.
     * This is the default mode. Note: the state of each pad is available from
     * {@link LaunchControl#getPad(PADS)}.
     */
    TOGGLE,
    /**
     * Pads work as a group of "radio buttons", meaning that only one pad can be activated at a time.
     * Pushing one pad will deactivate the other pads.
     */
    RADIO
}
