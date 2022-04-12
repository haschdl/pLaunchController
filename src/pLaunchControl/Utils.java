package pLaunchControl;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

public class Utils {

    protected static MidiMessage getResetMessage() {
        try {
            ShortMessage reset = new ShortMessage(184, 0, 0);
            return reset;
        } catch (InvalidMidiDataException e) {
            return null;
        }
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
}
