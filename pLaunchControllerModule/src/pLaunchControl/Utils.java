package pLaunchControl;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

public class Utils {


    /**
     * All LEDs are turned off, and the buffer settings and duty cycle are reset to their default values.
     * The MIDI channel n defines the template for which this message is intended (00h-07h (0-7) for the 8 user templates,
     * and 08h-0Fh (8-15) for the 8 factory templates).
     * @param channel
     * @return
     */
    protected static MidiMessage getResetMessage(int channel) {
        try {
            int status = 0xB0 + channel;
            ShortMessage reset = new ShortMessage(status, 0x00, 0x00);
            return reset;
        } catch (InvalidMidiDataException e) {
            return null;
        }
    }

    /***
     * Returns a message used to reset the controller at first factory template.
     * @return
     */
    protected static MidiMessage getResetMessage() {
        return getResetMessage(0x08);
    }

    /***
     * Changes the current template in the controller to first factory template (0x08)
     * The following message can be used to change the current template of the device:
     * Hex version F0h 00h 20h 29h 02h 0Ah 77h Template F7h
     * Where Template is 00h-07h (0-7) for the 8 user templates, and 08h-0Fh (8-15) for the 8 factory
     * templates.
     */
    protected static MidiMessage getSetTemplateMessage() {
        try {
            byte template = 0x08;
            byte[] msgContent = new byte[]{(byte) 0xF0, 0x00, 0x20, 0x29, 0x02, 0x0A, 0x77, template, (byte) 0xF7};
            SysexMessage setTemplateMsg = new SysexMessage(msgContent, msgContent.length);
            return setTemplateMsg;

        } catch (Exception e) {
            System.out.println("Error setting the template in the Midi controller. Error: " + e);
        }
        return null;
    }

    /**
     * Returns a message that turns all active LEDs to flashing.
     * @param template
     * @return
     */
    protected static MidiMessage getTurnOnFlashing(int template) {
        if (template < 0x00 || template > 0xff)
            throw new IllegalArgumentException("Template must be in the 0x00-0x0F (00h-07h (0-7) for the 8 user templates, and 08h-0Fh (8-15) for the 8 factory templates.");
        try {
            int status = 0xB0 + template;
            ShortMessage turnFlashingLEDsOn = new ShortMessage(status, 0x00, 0x28);
            return turnFlashingLEDsOn;
        } catch (InvalidMidiDataException e) {
            return null;
        }
    }
}
